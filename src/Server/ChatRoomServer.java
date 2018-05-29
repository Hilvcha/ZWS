package Server;

import redis.clients.jedis.Jedis;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatRoomServer {
    private ServerSocket serverSocket;
    private HashMap<Socket, String> alluser;
    private Jedis jedis;
    //聊天室服务器构造方法
    public ChatRoomServer() {
        try {
            serverSocket = new ServerSocket(4560);
            jedis = new Jedis("localhost");
        } catch (IOException e) {
            e.printStackTrace();
        }
        alluser = new HashMap<>();
    }

    //启动服务器
    public void startService() throws IOException {
        System.out.println("服务器已启动，等待用户加入......");
        while (true) {
            Socket s = serverSocket.accept();
            System.out.println("新的连接请求:"+s);
            new ServerThread(s).start();
        }
    }

    //服务线程内部类
    private class ServerThread extends Thread {
        Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    String str = br.readLine();
                    if (str.contains("%NAME%")) {
                        String name = str.split(":")[1];
                        if (alluser.values().contains(name)) {  //用户名有重复
                            String message = "%NAMEERROR%";
                            sendMessageToONEClient(message, socket);
                        } else {  //用户加入
                            alluser.put(socket, name);
                            sendMessageTOAllClient("%USERADD%:" + name);
                        }
                        continue;
                    } else if (str.contains("%EXIT%")) {  //用户退出
                        sendMessageTOAllClient("%USERDEL%:" + alluser.get(socket));
                        alluser.remove(socket);
                        sendMessageTOAllClient(str.split(":")[1] + "用户已退出聊天室");
                        socket.close();
                        return;
                    } else if (str.contains("%REQUESTALLUSER%")) {  //确认用户列表
                        sendMessageToONEClient("%USERSTART%", socket);
                        for (String name : alluser.values()) {
                            sendMessageToONEClient(name, socket);
                        }
                        sendMessageToONEClient("%USEREND%", socket);
                    } else if (str.contains("%ALL%")) {  //发送群聊消息
                        sendMessageTOAllClient("%ALL%");
                        while (true) {
                            str = br.readLine();
                            sendMessageTOAllClient(str);
                            if (str.contains("%END%")) {
                                break;
                            }
                        }
                    } else if (str.contains("%ONE%")) {  //发送私聊消息
                        String remotename = str.split(":")[1];
                        Socket remoteskt = socket;
                        for (Socket skt : alluser.keySet()) {
                            if (alluser.get(skt).equals(remotename)) {
                                remoteskt = skt;
                                sendMessageToONEClient("%ONE%:" + alluser.get(socket), remoteskt);
                                sendMessageToONEClient("%ONE%:" + remotename, socket);
                                break;
                            }
                        }
                        while (true) {
                            str = br.readLine();
                            sendMessageToONEClient(str, remoteskt);
                            sendMessageToONEClient(str, socket);
                            if (str.contains("%END%")) {
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessageToONEClient(String message, Socket s) throws IOException {
            System.out.println("将要向" + alluser.get(s) + "发送:" + message);
            jedis.lpush(alluser.get(s),message);
            PrintWriter pw = new PrintWriter(s.getOutputStream());
            pw.println(message);
            pw.flush();
        }

        public void sendMessageTOAllClient(String message) throws IOException {
            System.out.println("将要向所有用户发送:" + message);
            for (Socket s : alluser.keySet()) {
                PrintWriter pw = new PrintWriter(s.getOutputStream());
                jedis.lpush(alluser.get(s),message);
                pw.println(message);
                pw.flush();
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
