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

        setTitle("zws~");
        Container c = getContentPane();
        setLayout(new GridLayout(3, 1, 100, 30));
        JPanel JPip = new JPanel(new GridLayout(1, 2));
        JPanel JPUserName = new JPanel(new GridLayout(1, 2));

        //ip面板组件
        JLabel JLip = new JLabel("服务器ip：", JLabel.CENTER);
        JLip.setFont(new Font("黑体", Font.PLAIN, 50));
        JTip = new JTextField("localhost");
        JTip.setFont(new Font("黑体", Font.PLAIN, 50));
        JTip.setColumns(21);
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
        JLusername.setFont(new Font("黑体", Font.PLAIN, 50));
        JTusername = new JTextField("小机灵鬼");
        JTusername.setColumns(21);
        JTusername.setFont(new Font("黑体", Font.PLAIN, 50));
        JPUserName.add(JLusername);
        JPUserName.add(JTusername);

        //登录事件
        JButton JBlogin = new JButton("连接");
        JBlogin.setSize(100, 61);
        JBlogin.setFont(new Font("黑体", Font.PLAIN, 50));
        JBlogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                linkPerformed();
            }
        });
        c.add(JPip);
        c.add(JPUserName);
        c.add(JBlogin);
        setSize(700, 432);
        int windowWidth = getWidth(); // 获得窗口宽
        int windowHeight = getHeight(); // 获得窗口高
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
