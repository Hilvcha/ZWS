package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Base64;
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

    private JComboBox selectBox;
    private File file_output;

    private void sendPerformed() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        client.sendMessage("%START%:" + userName + "  " + df.format(date) + ":\n" + tfMessage.getText() + "%END%");
    }

    private void choosePerform() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("将要发送的文件");
        chooser.showDialog(new JLabel(), "选择");
        try {
            file_output = chooser.getSelectedFile();
            //System.out.println(file_output.getAbsolutePath());
            //System.out.println(file_output.getName());
            //System.out.println(file_output.length());

            //client.sendMessage("%FILE%:" + userName + "  " + df.format(date) + "：\n" + "文件名.文件后缀名" + "\n" + 文件转为二进制码 + "%END%");
            //当做message发送过去
            //接收到后根据后缀名解析，二进制码转回文件，存储到Download文件夹中

            String temp = file_output.getName();
            String file_name = temp.substring(0, temp.lastIndexOf("."));
            String file_type = temp.substring(temp.lastIndexOf(".") + 1);
            long file_length = file_output.length();
            //System.out.println(file_name);
            //System.out.println(file_type);
            //System.out.println(file_length);

            String transmit_data = fileToBase64(file_output);
            //System.out.println(transmit_data);  //将要传输的base64字符串
            //base64ToFile(transmit_data,"test.txt");

            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            client.sendMessage("%FILE%:" + userName + "  " + df.format(date) + "：\n已接收文件：" + temp + "\n" + transmit_data + "%END%");

        } catch (Exception e) {
            System.out.println("未选择文件");
        }
    }

    //构造聊天室窗口（由登录窗口调用）
    ClientFrame(String ip, String userName) {
        ImageIcon img = new ImageIcon("image/纯色.jpg");  //这是背景图片
        JLabel imgLabel = new JLabel(img);  //将背景图放在标签里
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        imgLabel.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());

        setTitle(userName + "的聊天室");
        Container c = getContentPane();
        ((JComponent) c).setOpaque(false);  //注意这里，将内容面板设为透明。这样LayeredPane面板中的背景才能显示出来。
        setLayout(new BorderLayout());
        this.userName = userName;

        onlineuser = new DefaultListModel();
        ReadMessageThread messageThread = new ReadMessageThread();

        try {  //发送消息事件
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

        JPanel westp = new JPanel(new BorderLayout());  //西侧面板（在线列表、私聊勾选）
        //在线列表
        onlineuserlist = new JList(onlineuser);
        onlineuserlist.setOpaque(false);
        //(JLabel)onlineuserlist.getCellRenderer.setOpaque(false);
        onlineuserlist.setBackground(new Color(0, 0, 0, 0));
        onlineuserP = new JScrollPane(onlineuserlist);
        //onlineuserP.setBorder(BorderFactory.createTitledBorder("在线用户"));
        //onlineuserP.setBorder(BorderFactory.createLineBorder(Color.getHSBColor(0.3138f, 0.1f, 1f)));
        onlineuserP.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("在线用户"), BorderFactory.createLineBorder(Color.getHSBColor(0.3138f, 0.1f, 1f))));
        onlineuserP.setOpaque(false);
        onlineuserP.getViewport().setOpaque(false);
        //私聊勾选框
        JCheckBox chatone = new JCheckBox("私聊");
        //chatone.setBackground(Color.yellow);
        chatone.setFont(new Font("微软雅黑", Font.BOLD, 15));
        chatone.setOpaque(false);
        //上述2个组件加入到Panel中
        westp.add(onlineuserP, BorderLayout.CENTER);
        westp.add(chatone, BorderLayout.SOUTH);
        //设置westp属性
        westp.setPreferredSize(new Dimension(200, 350));
        westp.setOpaque(false);


        contentPane = new JPanel();  //南侧面板（用户名、输入框、下拉框、多功能按钮）
        //用户名
        lblUsername = new JLabel(userName);
        lblUsername.setOpaque(false);
        //文本输入框
        tfMessage = new JTextField();
        tfMessage.setColumns(20);
        tfMessage.setOpaque(false);
        tfMessage.addKeyListener(new KeyAdapter() {
            char word;

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                word = e.getKeyChar();
                if (word == '\n') {
                    if (chatone.isSelected()) {
                        if (onlineuserlist.getSelectedValuesList().equals(Collections.emptyList())) {
                            JOptionPane.showMessageDialog(ClientFrame.this, "请选中要私聊的对象~", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else {
                            List namelist = onlineuserlist.getSelectedValuesList();
                            for (final String remotename : onlineuserlist.getSelectedValuesList()) {
                                System.out.println(remotename);
                                client.sendMessage("%ONE%:" + remotename);
                                sendPerformed();
                            }
                            tfMessage.setText("");
                        }
                    } else {
                        if (tfMessage.getText().equals("")) {
                        } else {
                            client.sendMessage("%ALL%");
                            sendPerformed();
                            tfMessage.setText("");
                        }
                    }
                }
            }
        });
        //下拉框
        selectBox = new JComboBox();
        selectBox.addItem("文字");
        selectBox.addItem("表情");
        selectBox.addItem("文件");
        selectBox.addItem("语音");
        selectBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    if (e.getItem().toString() == "文字") {
                        btnSend.setText("发送");
                    } else if (e.getItem().toString() == "表情") {
                        btnSend.setText("表情");
                    } else if (e.getItem().toString() == "文件") {
                        btnSend.setText("选择");
                    } else if (e.getItem().toString() == "语音") {
                        btnSend.setText("录制");
                    }
                }
            }
        });
        //发送按钮
        btnSend = new JButton(new ImageIcon("image/fasong.png"));
        btnSend.setBorderPainted(false);  //不绘制边框
        btnSend.setContentAreaFilled(false);
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnSend.getText() == "发送") {  //发送按钮事件
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
                            tfMessage.setText("");
                        }
                    } else {
                        if (tfMessage.getText().equals("")) {
                        } else {
                            client.sendMessage("%ALL%");
                            sendPerformed();
                            tfMessage.setText("");
                        }
                    }
                } else if (btnSend.getText() == "表情") {  //表情按钮事件
                    System.out.println("emoji");  //待加入表情选择框
                } else if (btnSend.getText() == "选择") {  //选择按钮事件
                    if (chatone.isSelected()) {
                        if (onlineuserlist.getSelectedValuesList().equals(Collections.emptyList())) {
                            JOptionPane.showMessageDialog(ClientFrame.this, "请选中要私聊的对象~", "warning", JOptionPane.WARNING_MESSAGE);
                        } else {
                            List namelist = onlineuserlist.getSelectedValuesList();
                            for (final String remotename : onlineuserlist.getSelectedValuesList()) {
                                System.out.println(remotename);
                                client.sendMessage("%ONE%:" + remotename);
                                choosePerform();
                            }
                        }
                    } else {
                        client.sendMessage("%ALL%");
                        choosePerform();
                    }
                } else if (btnSend.getText() == "录制") {  //录制按钮事件
                    System.out.println("sound");  //待加入语音录制模块
                }
            }
        });
        //上述4个组件加入到Panel中
        contentPane.add(lblUsername);
        contentPane.add(tfMessage);
        contentPane.add(selectBox);
        contentPane.add(btnSend);
        //设置contentPane属性
        contentPane.setOpaque(false);


        textArea = new JTextArea();  //中部文字区域
        JScrollPane talkwindow = new JScrollPane(textArea);
        talkwindow.setBorder(BorderFactory.createLineBorder(Color.getHSBColor(0.3138f, 0.1f, 1f)));
        talkwindow.setPreferredSize(new Dimension(400, 340));
        talkwindow.setOpaque(false);
        talkwindow.getViewport().setOpaque(false);
        textArea.setOpaque(false);


        JPanel JP = new JPanel(/*new GridLayout(1, 2)*/);
        JP.add(westp);
        JP.add(talkwindow);
        JP.setPreferredSize(new Dimension(600, 350));
        JP.setOpaque(false);


        c.add(JP, BorderLayout.NORTH);
        c.add(contentPane, BorderLayout.SOUTH);
        //c.add(westp, BorderLayout.WEST);
        //c.add(talkwindow, BorderLayout.CENTER);


        setSize(768, 432);
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

        //关闭聊天室客户端事件
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

    //file编码为base64字符串
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            //byte[] bytes = new byte[in.available()];  //这行会造成乱码，应替换为下面两行
            byte[] bytes = new byte[(int) file.length()];
            in.read(bytes);  //缓冲区in中读取字节存入数组bytes中
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    //base64字符串解码为file
    public static void base64ToFile(String base64, String fileName) {
        File file = null;
        String filePath = "./Downloads";
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            file = new File(filePath + "/" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //负责不断解析收到的一行一行的信息
    private class ReadMessageThread extends Thread {
        public volatile boolean exit = false;

        public void run() {
            while (!exit) {
                String str = client.reciveMessage();
                if (str.contains("%NAMEERROR%")) {  //重名，关闭原登录窗口，弹出提示窗口，然后打开新登录窗口
                    ClientFrame.this.dispose();
                    JOptionPane.showMessageDialog(ClientFrame.this, "你的名字重复了~", "Warning", JOptionPane.WARNING_MESSAGE);
                    new LinkServerFrame();
                } else if (str.contains("%USERSTART%")) {
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
                } else if (str.contains("%USERADD%")) {
                    onlineuser.addElement(str.split(":")[1]);
                } else if (str.contains("%USERDEL%")) {
                    onlineuser.removeElement(str.split(":")[1]);
                } else if (str.contains("%ALL%") || str.contains("%ONE%")) {
                    if (str.contains("%ONE%")) {
                        String remotename = str.split(":")[1];
                        textArea.append("[与" + remotename + "的私聊]" + "\n");
                    }
                    str = client.reciveMessage();
                    if (str.contains("%START%")) {  //发送消息
                        //System.out.println("将要打印文本："+str.split(":")[0]);

//                        if (str.contains("%END%")) {  //开始结束在同一行
//                            textArea.append(str.replaceAll("%START%", ""));
//                            textArea.append(str.replaceAll("%END%", ""));
//                            textArea.append(str + "\n");
//                            textArea.setSelectionStart(textArea.getText().length());
//                            continue;
//                        }

                        textArea.append(str.split(":", 2)[1] + "\n");
                        textArea.setSelectionStart(textArea.getText().length());

                        while (true) {
                            str = client.reciveMessage();
                            if (str.contains("%END%")) {
                                textArea.append(str.replaceAll("%END%", "") + "\n\n");
                                break;
                            }
                            textArea.append(str + "\n");
                            textArea.setSelectionStart(textArea.getText().length());
                        }
                    } else if (str.contains("%FILE%")) {  //发送文件
                        textArea.append(str.split(":", 2)[1] + "\n");
                        textArea.setSelectionStart(textArea.getText().length());  //光标跟随往下
                        str = client.reciveMessage();
                        String fileName = str.substring(6);  //"\已接收文件：" + temp，从第6位开始取子串，即temp，得到的是文件名
                        //System.out.println(fileName);
                        textArea.append(str + "\n\n");
                        String incoming_data = "";
                        while (true) {
                            str = client.reciveMessage();
                            incoming_data = incoming_data + str;
                            if (incoming_data.contains("%END%")) {  //判断文本结束
                                incoming_data = incoming_data.replaceAll("%END%", "");
                                break;
                            }
                        }
                        //System.out.println(incoming_data);  //接收到的base64字符串
                        base64ToFile(incoming_data, fileName);
                    }
                }
            }
        }
    }
}