package Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class ClientFrame extends JFrame {
    private JPanel contentPane;
    private JLabel lblUsername;
    private JTextField tfMessage;
    private JButton btnSend;
    private JTextArea textArea;
    private String userName;
    private ChatRoomClient client;
    private DefaultListModel onlineuser;
    private JList<String> onlineuserlist;
    private JScrollPane onlineuserP;


    private void sendPerformed() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        client.sendMessage("%START%:" + userName + "  " + df.format(date) + ":\n" + tfMessage.getText() + "%END%");
        tfMessage.setText("");
    }

    ClientFrame(String ip, String userName) {

        ImageIcon img = new ImageIcon("image/_寒冰射手.jpg");//这是背景图片
        JLabel imgLabel = new JLabel(img);//将背景图放在标签里。
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        imgLabel.setBounds(0,0,img.getIconWidth(), img.getIconHeight());

        setTitle(userName + "的聊天室");
        Container c = getContentPane();
        ((JComponent) c).setOpaque(false); //注意这里，将内容面板设为透明。这样LayeredPane面板中的背景才能显示出来。
        setLayout(new BorderLayout());
        this.userName = userName;

        onlineuser = new DefaultListModel();
        ReadMessageThread messageThread = new ReadMessageThread();

        try {
            client = new ChatRoomClient(ip, 4560);
            messageThread.start();
            client.sendMessage("%NAME%:" + userName);
            client.sendMessage("%REQUESTALLUSER%");

        } catch (UnknownHostException el) {
            System.out.println("host 无法处理");
            el.printStackTrace();
        } catch (IOException el) {
            el.printStackTrace();
        }
//        发送消息事件

        onlineuserlist = new JList(onlineuser);
        onlineuserlist.setOpaque(false);
//        (JLabel)onlineuserlist.getCellRenderer.setOpaque(false);
        onlineuserlist.setBackground(new Color(0, 0, 0, 0));


        btnSend = new JButton(new ImageIcon("image/fasong.png"));
        btnSend.setBorderPainted(false);//不绘制边框
        btnSend.setContentAreaFilled(false);


        lblUsername = new JLabel(userName);
        contentPane = new JPanel();
        tfMessage = new JTextField();
        tfMessage.setColumns(20);
        textArea = new JTextArea();
        lblUsername.setOpaque(false);
        contentPane.setOpaque(false);
        tfMessage.setOpaque(false);
        textArea.setOpaque(false);

        contentPane.add(lblUsername);
        contentPane.add(tfMessage);
        contentPane.add(btnSend);
        tfMessage.addKeyListener(new KeyAdapter() {
            char word;

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                word = e.getKeyChar();
                if (word == '\n') {
                    if (tfMessage.getText().equals("")) {
                    } else {
                        sendPerformed();
                    }
                }
            }
        });

        onlineuserP = new JScrollPane(onlineuserlist);
        JScrollPane talkwindow = new JScrollPane(textArea);
        talkwindow.setPreferredSize(new Dimension(400, 340));

        JPanel westp = new JPanel(new BorderLayout());
        westp.add(onlineuserP, BorderLayout.CENTER);
        westp.setPreferredSize(new Dimension(200, 350));
        JCheckBox chatone = new JCheckBox("私聊");
//        chatone.setBackground(Color.yellow);
        chatone.setFont(new Font("微软雅黑", Font.BOLD, 15));

//        onlineuserP.setBorder(BorderFactory.createLineBorder(Color.getHSBColor(0.3138f, 0.1f, 1f)));
        onlineuserP.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("在线用户"),BorderFactory.createLineBorder(Color.getHSBColor(0.3138f, 0.1f, 1f))));
//        onlineuserP.setBorder(BorderFactory.createTitledBorder("在线用户"));
        talkwindow.setBorder(BorderFactory.createLineBorder(Color.getHSBColor(0.3138f, 0.1f, 1f)));


        JPanel JP = new JPanel(/*new GridLayout(1, 2)*/);
        JP.setOpaque(false);
        JP.add(westp);
        JP.add(talkwindow);
        JP.setPreferredSize(new Dimension(600, 350));



        onlineuserP.setOpaque(false);
        onlineuserP.getViewport().setOpaque(false);
        talkwindow.setOpaque(false);
        talkwindow.getViewport().setOpaque(false);
        westp.setOpaque(false);
        chatone.setOpaque(false);

        westp.add(chatone, BorderLayout.SOUTH);

        c.add(JP,BorderLayout.NORTH);
//        c.add(talkwindow, BorderLayout.CENTER);
        c.add(contentPane, BorderLayout.SOUTH);
//        c.add(westp, BorderLayout.WEST);
        setSize(768, 432);

        setVisible(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chatone.isSelected()) {
                    if (onlineuserlist.getSelectedValuesList().equals(Collections.emptyList())) {
                        JOptionPane.showMessageDialog(ClientFrame.this, "请选中要私聊的对象~", "warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        List namelist = onlineuserlist.getSelectedValuesList();
                        for (final String remotename : onlineuserlist.getSelectedValuesList()) {
                            System.out.println(remotename);
                            client.sendMessage("%ONE%:" + remotename);
                            sendPerformed();
                        }
                    }
                } else {
                    if (tfMessage.getText().equals("")) {
                    } else {
                        client.sendMessage("%ALL%");
                        sendPerformed();
                    }
                }


            }
        });
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
                System.out.println(onlineuser);

                super.windowClosing(atg0);
                int op = JOptionPane.showConfirmDialog(ClientFrame.this, "确定要退出聊天室吗~", "确定", JOptionPane.YES_NO_OPTION);
                if (op == JOptionPane.YES_OPTION) {
                    messageThread.exit = true;
                    client.sendMessage("%EXIT%:" + userName);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    client.close();
                    System.exit(0);
                }
            }
        });

    }

    //负责不断解析收到的一行一行的信息
    private class ReadMessageThread extends Thread {
        public volatile boolean exit = false;

        public void run() {
            while (!exit) {
                String str = client.reciveMessage();
                if (str.contains("%NAMEERROR%")) {
                    ClientFrame.this.dispose();
                    JOptionPane.showMessageDialog(ClientFrame.this, "你的名字重复了~", "warning", JOptionPane.WARNING_MESSAGE);
                    new LinkServerFrame();
                }else if (str.contains("%USERSTART%")) {
                    while (true) {
                        str = client.reciveMessage();
                        if (str.contains("%USEREND%")) {
                            break;
                        }
                        if (onlineuser.contains(str)) {
                        } else {
                            onlineuser.addElement(str);
                        }


                    }
                }
                else if (str.contains("%USERADD%")) {
                    onlineuser.addElement(str.split(":")[1]);
                }
                else if (str.contains("%USERDEL%")) {
                    onlineuser.removeElement(str.split(":")[1]);
                }
                else if(str.contains("%ALL%")||str.contains("%ONE%")){
                    if(str.contains("%ONE%")){
                        String remotename=str.split(":")[1];
                        textArea.append("[私聊]:");
                    }
                    str = client.reciveMessage();
                    if (str.contains("%START%")) {
//                    System.out.println("将要打印文本："+str.split(":")[0]);
                        if (str.contains("%END%")) {//开始结束在同一行
                            textArea.append(str.replaceAll("%START%", ""));
                            textArea.append(str.replaceAll("%END%", ""));
                            textArea.append(str + "\n");
                            textArea.setSelectionStart(textArea.getText().length());
                            continue;
                        }
                        textArea.append(str.split(":", 2)[1] + "\n");
                        textArea.setSelectionStart(textArea.getText().length());

                        while (true) {
                            str = client.reciveMessage();
                            if (str.contains("%END%")) {
                                textArea.append(str.replaceAll("%END%", "") + "\n");
                                break;
                            }
                            textArea.append(str + "\n");
                            textArea.setSelectionStart(textArea.getText().length());

                        }
                    }
                }
            }
        }
    }
}