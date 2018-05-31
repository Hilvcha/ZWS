package Server;

import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class ChatRoomServer {
    private ServerSocket serverSocket;
    private volatile HashMap<Socket, String> alluser;
    private HashSet<String> users;
    private Jedis jedis;
    //聊天室服务器构造方法
    public ChatRoomServer() {
        try {
            serverSocket = new ServerSocket(4560);
            jedis = UseJedisPool.getJedis();
            jedis.flushAll();
            UseJedisPool.returnResource(jedis);

        } catch (IOException e) {
            e.printStackTrace();
        }
        alluser = new HashMap<>();
        users=new HashSet<>();
    }

    //启动服务器
    public void startService() throws IOException {
        System.out.println("服务器已启动，等待用户加入......");
        while (true) {
            Socket s = serverSocket.accept();
            System.out.println("新的连接请求:"+s);
            new ProducerThread(s).start();
        }
    }

    //服务线程内部类
    private class ProducerThread extends Thread {
        private Socket socket;
        private ConsumerThread myConsumer;
        public ProducerThread(Socket socket) {
            this.socket = socket;
        }
        private String sktname;
        private Jedis myjedis;

        public void run() {
            myjedis = UseJedisPool.getJedis();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    String str = receivemessage(br);
                    if (str.contains("%NAME%")) {
                        sktname = str.split(":")[1];
                        if (alluser.values().contains(sktname)) {  //用户名有重复
                            String message = "%NAMEERROR%";
                            sendToONEClient(message, socket);

                        } else {  //用户加入,启动消费者进程
                            alluser.put(socket, sktname);
                            users.add(sktname);
                            sendToAllClient("%USERADD%:" + sktname);

                        }
                        continue;
                    } else if (str.contains("%EXIT%")) {  //用户退出
                        myConsumer.exit=true;
//                        myConsumer.interrupt();
                        sendToONEClient("quit",socket);
                        sendToAllClient("%USERDEL%:" + sktname);
                        socket.close();

                        alluser.remove(socket);
                        return;
                    } else if (str.contains("%REQUESTALLUSER%")) {  //确认用户列表
                        sendToONEClient("%USERSTART%", socket);
                        for (String name : alluser.values()) {
                            sendToONEClient(name, socket);
                        }
                        sendToONEClient("%USEREND%", socket);
                        //                            初始化完毕，可以开始读取消息
                        myConsumer=new ConsumerThread(socket);
                        myConsumer.start();
                    } else if (str.contains("%ALL%")) {  //发送群聊消息
                        sendMessageToAllClient("%ALL%");
                        while (true) {
                            str = receivemessage(br);
                            sendMessageToAllClient(str);
                            if (str.contains("%END%")) {
                                break;
                            }
                        }
                    } else if (str.contains("%ONE%")) {  //发送私聊消息
                        String remotename = str.split(":")[1];
                        sendMessageToONEClient("%ONE%:" + sktname, remotename);
                        sendMessageToONEClient("%ONE%:" + remotename, sktname);
                        while (true) {
                            str = receivemessage(br);
                            sendMessageToONEClient(str, remotename);
                            sendMessageToONEClient(str, sktname);
                            if (str.contains("%END%")) {
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(myjedis!=null){
                    UseJedisPool.returnResource(myjedis);
                }
            }
        }
        public void sendToONEClient(String  message,Socket socket){
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.println(message);
                pw.flush();
            }catch (IOException e){
                e.printStackTrace();
            }

        }


        public void sendMessageToONEClient(String message, String name) throws IOException {
            System.out.println("向" + name + "发送:" + message);

            myjedis.lpush(name,message);

//            PrintWriter pw = new PrintWriter(s.getOutputStream());
//            pw.println(message);
//            pw.flush();
        }

        public void sendMessageToAllClient(String message) throws IOException {
            System.out.println("将要向所有用户发送:" + message+users);
            for (String myname : users) {

                myjedis.lpush(myname,message);

//                PrintWriter pw = new PrintWriter(s.getOutputStream());
//                pw.println(message);
//                pw.flush();
            }
        }
        public void sendToAllClient(String  message){
            try {
                System.out.println("向所有在线用户发送:" + message+alluser.values());
                for (Socket s : alluser.keySet()) {
                    PrintWriter pw = new PrintWriter(s.getOutputStream());
                    pw.println(message);
                    pw.flush();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        private String receivemessage(BufferedReader br){
            String str="";
            try {
                str = br.readLine();
                System.out.println("!!!"+str+"!!!");

            }catch (IOException e) {
                e.printStackTrace();

            }
            return str;
        }
    }

    private class ConsumerThread extends Thread{
        public Jedis myjedis;
        public volatile boolean exit = false;
        private Socket socket;
        private List<String> message;
        public ConsumerThread(Socket socket) {
            this.socket = socket;
        }
        private final  int BLOCK_TIMEOUT=30;
        public void run() {
            try {
                System.out.println("构造消费者");
                myjedis=UseJedisPool.getJedis();
                String myname=alluser.get(socket);

                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                while(!exit){
                    System.out.println( myname+"等待pop"+myjedis.keys("*"));
                    message=myjedis.brpop(BLOCK_TIMEOUT,myname);

                    System.out.println("我是"+alluser.get(socket)+"的消费者:"+message);

                    if(message.equals(Collections.emptyList())) {
                        continue;
                    }
                    if(exit==true){
                        myjedis.rpush(myname,message.get(1));
                        break;
                    }
                    String m=message.get(1);
                    pw.println(m);
                    pw.flush();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(myjedis!=null){
                    UseJedisPool.returnResource(myjedis);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            new ChatRoomServer().startService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
