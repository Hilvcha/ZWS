import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatRoomClient {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter pWriter;

    //构造客户端
    public ChatRoomClient(String host, int port) throws UnknownHostException, IOException {
        System.out.println("link");
        socket = new Socket(host, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pWriter = new PrintWriter(socket.getOutputStream());
    }

    public static void main(String[] args){
        new LinkServerFrame();
    }
    //发送消息
    public void sendMessage(String str) {
        pWriter.println(str);
        pWriter.flush();
    }

    // 接受消息
    public String reciveMessage() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//LinkServerFrame 登录服务器窗体
class LinkServerFrame extends JFrame{
    private JTextField JTip;
    private JTextField JTusername;
    private void linkPerformed(){
        //  客户端连接时文本框不为空
        if(!JTip.getText().equals("")&&!JTusername.getText().equals("")){
            dispose();
            //  销毁客户端窗体
            System.out.println("正在注册："+JTusername.getText().trim()+"···");
            ClientFrame clientFrame=new ClientFrame(JTip.getText().trim(),JTusername.getText().trim());
            clientFrame.setVisible(true);
        }else{
            JOptionPane.showMessageDialog(null,"文本框内容不能为空","warning",JOptionPane.WARNING_MESSAGE);
        }
    }
    LinkServerFrame(){
        int windowWidth = getWidth(); // 获得窗口宽
        int windowHeight = getHeight(); // 获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width; // 获取屏幕的宽
        int screenHeight = screenSize.height; // 获取屏幕的高
        setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);// 设置窗口居中显示

        setTitle("zws~");
        Container c=getContentPane();
        setLayout(new GridLayout(3,1,100,30));
        JPanel JPip=new JPanel(new GridLayout(1,2));
        JPanel JPUserName=new JPanel(new GridLayout(1,2));

        //ip面板组件
        JLabel JLip=new JLabel("服务器ip：",JLabel.CENTER);
        JLip.setFont(new Font("黑体",Font.PLAIN,50));
        JTip=new JTextField("localhost");
        JTip.setFont(new Font("黑体",Font.PLAIN,50));
        JTip.setColumns(21);
        JPip.add(JLip); JPip.add(JTip);
        JTip.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                char word=e.getKeyChar();
                if(word=='\n'){
                    linkPerformed();
                }
            }
        });
        //username面板组件
        JLabel JLusername=new JLabel("客户端name：",JLabel.CENTER);
        JLusername.setFont(new Font("黑体",Font.PLAIN,50));
        JTusername=new JTextField("小机灵鬼");
        JTusername.setColumns(21);
        JTusername.setFont(new Font("黑体",Font.PLAIN,50));
        JPUserName.add(JLusername); JPUserName.add(JTusername);

        //登录事件
        JButton JBlogin=new JButton("连接");
        JBlogin.setSize(100,61);
        JBlogin.setFont(new Font("黑体",Font.PLAIN,50));
        JBlogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                linkPerformed();
            }
        });
        c.add(JPip); c.add(JPUserName); c.add(JBlogin);
        setSize(700,432);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
class ClientFrame extends JFrame{
    private JPanel contentPane;
    private JLabel lblUsername;
    private JTextField tfMessage;
    private JButton btnSend;
    private JTextArea textArea;
    private String userName;
    private ChatRoomClient client;
    private void sendPerformed(){
        Date date=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        client.sendMessage("%START%:"+userName+"  "+df.format(date)+":\n"+ tfMessage.getText()+"%END%");
        tfMessage.setText("");
    }

    ClientFrame(String ip,String userName){
        setTitle(userName+"的聊天室");
        Container c=getContentPane();
        setLayout(new BorderLayout());
        this.userName=userName;

        try {
            client=new ChatRoomClient(ip,4560);
        }catch (UnknownHostException el){
            System.out.println("host 无法处理");
            el.printStackTrace();
        }catch (IOException el){
            el.printStackTrace();
        }
        ReadMessageThread messageThread=new ReadMessageThread();
        messageThread.start();
//        发送消息事件

        btnSend=new JButton("发送");

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendPerformed();
            }
        });
        lblUsername=new JLabel(userName);
        contentPane=new JPanel();
        tfMessage=new JTextField();
        tfMessage.setColumns(20);
        textArea=new JTextArea();
        contentPane.add(lblUsername);
        contentPane.add(tfMessage);
        contentPane.add(btnSend);
        tfMessage.addKeyListener(new KeyAdapter() {
            char word;
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                word=e.getKeyChar();
                if(word=='\n'){
                    sendPerformed();
                }
            }
        });
        JScrollPane talkwindow=new JScrollPane(textArea);
        c.add(talkwindow,BorderLayout.CENTER);
        c.add(contentPane,BorderLayout.SOUTH);
        setSize(600,370);
        setVisible(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        //刚打开窗口的焦点聚焦
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                tfMessage.requestFocusInWindow();
            }
        });

        //        关闭聊天室客户端事件
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent atg0) {
                super.windowClosing(atg0);
                int op=JOptionPane.showConfirmDialog(ClientFrame.this,"确定要退出聊天室吗~","确定",JOptionPane.YES_NO_OPTION);
                if(op==JOptionPane.YES_OPTION){
                    client.sendMessage("%EXIT%:"+userName);
                    try{
                        Thread.sleep(200);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    client.close();
                    System.exit(0);
                }
            }
        });

    }

    private class ReadMessageThread extends Thread{
        public void run(){
            while(true){
                String str=client.reciveMessage();
                System.out.println("接收到消息:"+str);
                if(str.contains("%START%")){
//                    System.out.println("将要打印文本："+str.split(":")[0]);
                    if(str.contains("%END%")){//开始结束在同一行
                        textArea.append(str.replaceAll("%START%",""));
                        textArea.append(str.replaceAll("%END%",""));
                        textArea.append(str+"\n");
                        textArea.setSelectionStart(textArea.getText().length());
                        continue;
                    }
                    textArea.append(str.split(":",2)[1]+"\n");
                    textArea.setSelectionStart(textArea.getText().length());

                    while(true){
                        str=client.reciveMessage();
                        if(str.contains("%END%")){
                            textArea.append(str.replaceAll("%END%","")+"\n");
                            break;
                        }
                        textArea.append(str+"\n");
                        textArea.setSelectionStart(textArea.getText().length());

                    }
                }
            }
        }
    }
}