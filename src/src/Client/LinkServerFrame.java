package Client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//Client.LinkServerFrame 登录服务器窗体
public class LinkServerFrame extends JFrame {
    private JTextField JTip;
    private JTextField JTusername;

    private void linkPerformed() {
        //  客户端连接时文本框不为空
        if (!JTip.getText().equals("") && !JTusername.getText().equals("")) {
            setVisible(false);
            //  销毁客户端窗体
            System.out.println("正在注册：" + JTusername.getText().trim() + "···");
            ClientFrame clientFrame = new ClientFrame(JTip.getText().trim(), JTusername.getText().trim());
            clientFrame.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "文本框内容不能为空", "warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    LinkServerFrame() {
        //背景
        ImageIcon img = new ImageIcon("ZWS/src/src/image/_北地之怒.jpg");//这是背景图片
        JLabel imgLabel = new JLabel(img);//将背景图放在标签里。
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        imgLabel.setBounds(0,0,img.getIconWidth(), img.getIconHeight());

        setTitle("zws~");
        Container c = getContentPane();
        setLayout(new GridLayout(4, 1));
        JPanel JPip = new JPanel();
        JPanel JPUserName = new JPanel();


        ((JComponent) c).setOpaque(false); //注意这里，将内容面板设为透明。这样LayeredPane面板中的背景才能显示出来。
        JPip.setOpaque(false);
        JPUserName.setOpaque(false);

        //hello
        JLabel JLtitle = new JLabel("BEST WISHES FOR YOU!", JLabel.CENTER);
        JLtitle.setOpaque(false);
        JLtitle.setFont(new Font("黑体", Font.PLAIN, 50));
        JLtitle.setForeground(Color.getHSBColor(0.3138f, 0.1f, 1f));

        //ip面板组件
        JLabel JLip = new JLabel("服务器ip  ：", JLabel.CENTER);
        JLip.setFont(new Font("黑体", Font.PLAIN, 30));
        JTip = new JTextField("localhost");
        JTip.setFont(new Font("黑体", Font.PLAIN, 30));
        JTip.setColumns(21);
        JLip.setForeground(Color.getHSBColor(0.3138f, 0.1f, 1f));
        JTip.setForeground(Color.getHSBColor(0.6444f, 0.82f, 0.44f));
        JLip.setOpaque(false);//透明
        JTip.setOpaque(false);//
        JPip.add(JLip);
        JPip.add(JTip);
        JTip.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                char word = e.getKeyChar();
                if (word == '\n') {
                    linkPerformed();
                }
            }
        });
        //username面板组件
        JLabel JLusername = new JLabel("客户端name：", JLabel.CENTER);
        JLusername.setFont(new Font("黑体", Font.PLAIN, 30));
        JTusername = new JTextField("小机灵鬼");
        JTusername.setColumns(21);
        JTusername.setFont(new Font("黑体", Font.PLAIN, 30));
        JLusername.setForeground(Color.getHSBColor(0.3138f, 0.1f, 1f));
        JTusername.setForeground(Color.getHSBColor(0.6444f, 0.82f, 0.44f));
        JPUserName.setOpaque(false);
        JTusername.setOpaque(false);
        JPUserName.add(JLusername);
        JPUserName.add(JTusername);

        //登录事件
        JPanel JPevent = new JPanel();
        JPevent.setOpaque(false);
        //登录事件
        JButton JBlogin = new JButton(new ImageIcon("ZWS/src/src/image/denglu.png"));
        JBlogin.setBorderPainted(false);//不绘制边框
        JBlogin.setContentAreaFilled(false);
        JBlogin.setSize(20, 12);
//        JBlogin.setFont(new Font("黑体", Font.PLAIN, 50));
        JBlogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                linkPerformed();
            }
        });
        JPevent.add(JBlogin);


        JButton JBsignin = new JButton(new ImageIcon("ZWS/src/src/image/zhuce.png"));
        JBsignin.setBorderPainted(false);//不绘制边框
        JBsignin.setContentAreaFilled(false);
        JPevent.add(JBsignin);

        c.add(JLtitle);
        c.add(JPip);
        c.add(JPUserName);
        c.add(JPevent );
        setSize(576, 324);
        int windowWidth = getWidth(); // 获得窗口宽
        int windowHeight = getHeight(); // 获得窗口
        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width; // 获取屏幕的宽
        int screenHeight = screenSize.height; // 获取屏幕的高
        System.out.println(getSize());
        setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);// 设置窗口居中显示

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
