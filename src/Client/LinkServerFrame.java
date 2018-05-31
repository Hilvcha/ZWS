package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

//Client.LinkServerFrame 登录服务器窗体
public class LinkServerFrame extends JFrame {
    private JTextField JTip;
    private JTextField JTusername;
    private JPasswordField JTpassword;

    private static final String host="192.168.31.112";

    private void linkPerformed() {
        //客户端连接时文本框不为空
        if (JTip.getText().equals("") || JTusername.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "文本框内容不能为空", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            //setVisible(false);
            //销毁客户端窗体
            System.out.println("正在注册：" + JTusername.getText().trim() + "......");
            ClientFrame clientFrame = new ClientFrame(JTip.getText().trim(), JTusername.getText().trim());
            clientFrame.setVisible(true);
            dispose();
        }
    }

    static Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    public static Connection getConnection(){
//        String url="jdbc:mysql://127.0.0.1:3306/wsychat";
        String url="jdbc:mysql://"+host+":3306/wsychat";
        String userName="root";
        String password="045X";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("找不到驱动！");
            e.printStackTrace();
        }
        try {
            conn=DriverManager.getConnection(url, userName, password);
            if(conn!=null){
                System.out.println("服务器连接成功......");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println( "服务器连接失败......");
            e.printStackTrace();
        }
        return conn;
    }

    private void login(){
        try {
            String account = JTusername.getText();
            String password =String.valueOf(JTpassword.getPassword());

            System.out.println(account );
            System.out.println(password);
            Connection con = getConnection();
            if (!con.isClosed()) {
                System.out.println("wstchat数据库连接成功");
            }
            Statement statement = con.createStatement();
            String sql = "select * from account_number_login where account_number=\""+account+"\";";//我的表格叫home
            System.out.println(sql);
            ResultSet resultSet = statement.executeQuery(sql);
//                            System.out.println("account_number:" + account);
            String account_number="";
            String password_number="";
            while (resultSet.next()) {
                account_number = resultSet.getString("account_number");
                password_number=resultSet.getString("password");
                System.out.println("account_number:" + account_number+"password_number"+password_number);
            }
            System.out.println(","+account_number);
            if(""==account_number)  System.out.println("账号不存在!");
            else if(!password_number.equals(password)){
//                System.out.println(account_number);
                System.out.println("密码错误");
                JOptionPane.showMessageDialog(null, "密码错误！", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            else {
                linkPerformed();
            }

            resultSet.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
        }
    }


    private void signin(){
        try {

            String account = JTusername.getText();
            String password = JTpassword.getText();
            Connection con = getConnection();
            if (!con.isClosed()) {
                System.out.println("wstchat数据库连接成功");
            }
            Statement statement = con.createStatement();
            String sql = "select * from account_number_login where account_number=\""+account+"\";";//我的表格叫home
            System.out.println(sql);
            ResultSet resultSet = statement.executeQuery(sql);
//                            System.out.println("account_number:" + account);
            String account_number="";
            while (resultSet.next()) {
                account_number = resultSet.getString("account_number");
                System.out.println("account_number:" + account);
            }
            System.out.println(","+account_number);
            if(""!=account_number){
                System.out.println(account_number);
                System.out.println("账户已存在");
                JOptionPane.showMessageDialog(null, "账号已存在", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            else {
                try {
                    Connection co = getConnection();
                    if (!con.isClosed()) {
                        System.out.println("wstchat数据库连接成功");
                    }
                    Statement state = co.createStatement();
                    String sqlInset = "insert into account_number_login values ( \"" + account + "\",\"" + password + "\"," + "0);";
//                    state = co.prepareStatement(sqlInset);   //会抛出异常
                    System.out.println(sqlInset);
                    statement.executeUpdate(sqlInset);
                    System.out.println("注册成功");
                    JOptionPane.showMessageDialog(null, "注册成功", "Succcesful", JOptionPane.WARNING_MESSAGE);

                }catch (SQLException e) {
                    System.out.println("注册连接失败");
                }
            }

            resultSet.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
        }
    }


    //构造登录窗口
    LinkServerFrame() {
        //背景
        ImageIcon img = new ImageIcon("src/image/_北地之怒.jpg");  //这是背景图片
        JLabel imgLabel = new JLabel(img);  //将背景图放在标签里。
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        imgLabel.setBounds(0,0,img.getIconWidth(), img.getIconHeight());

        setTitle("zws~");
        Container c = getContentPane();
        setLayout(new GridLayout(4, 1));

        JPanel JPip = new JPanel();
        JPanel JPUserName = new JPanel();
        JPanel JPpassword = new JPanel();

        ((JComponent) c).setOpaque(false);  //注意这里，将内容面板设为透明。这样LayeredPane面板中的背景才能显示出来。
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

        JTusername = new JTextField("A");
        JTusername.setFont(new Font("黑体", Font.PLAIN, 30));
        JTusername.setColumns(21);

        JLusername.setForeground(Color.getHSBColor(0.3138f, 0.1f, 1f));
        JTusername.setForeground(Color.getHSBColor(0.6444f, 0.82f, 0.44f));
        JPUserName.setOpaque(false);
        JTusername.setOpaque(false);

        JPUserName.add(JLusername);
        JPUserName.add(JTusername);

        JTusername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                char word = e.getKeyChar();
                if (word == '\n') {
                    linkPerformed();
                }
            }
        });

        JLabel JLpassword = new JLabel("客户端密码：", JLabel.CENTER);
        JLpassword.setFont(new Font("黑体", Font.PLAIN, 30));

        JTpassword = new JPasswordField();
        JTpassword.setFont(new Font("黑体", Font.PLAIN, 30));
        JTpassword.setColumns(21);

        JLpassword.setForeground(Color.getHSBColor(0.3138f, 0.1f, 1f));
        JTpassword.setForeground(Color.getHSBColor(0.6444f, 0.82f, 0.44f));
        JPpassword.setOpaque(false);
        JTpassword.setOpaque(false);

        JPpassword.add(JLpassword);
        JPpassword.add(JTpassword);

        JTpassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                char word = e.getKeyChar();
                if (word == '\n') {
                    linkPerformed();
                }
            }
        });

        //登录事件
        JPanel JPevent = new JPanel();
        JPevent.setOpaque(false);
        //登录事件
        JButton JBlogin = new JButton(new ImageIcon("src/image/denglu.png"));
        JBlogin.setBorderPainted(false);  //不绘制边框
        JBlogin.setContentAreaFilled(false);
        JBlogin.setSize(20, 12);
        //JBlogin.setFont(new Font("黑体", Font.PLAIN, 50));

        JBlogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        JPevent.add(JBlogin);

        JButton JBsignin = new JButton(new ImageIcon("src/image/zhuce.png"));
        JBsignin.setBorderPainted(false);  //不绘制边框
        JBsignin.setContentAreaFilled(false);

        JBsignin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signin();
            }
        });
        JPevent.add(JBsignin);

        c.add(JLtitle);
//        c.add(JPip);
        c.add(JPUserName);
        c.add(JPpassword);
        c.add(JPevent );

        setSize(576, 324);
        int windowWidth = getWidth();  //获得窗口宽
        int windowHeight = getHeight();  //获得窗口

        Toolkit kit = Toolkit.getDefaultToolkit();  //定义工具包
        Dimension screenSize = kit.getScreenSize();  //获取屏幕的尺寸
        int screenWidth = screenSize.width;  //获取屏幕的宽
        int screenHeight = screenSize.height;  //获取屏幕的高
        System.out.println(getSize());
        setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);  //设置窗口居中显示

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
