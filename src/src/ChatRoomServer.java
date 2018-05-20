import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatRoomServer {
    private ServerSocket serverSocket;
    private HashSet<Socket> allSocket;
//    聊天室服务器构造方法
    public ChatRoomServer(){
        try {
            serverSocket=new ServerSocket(4560);
        }catch (IOException e){
            e.printStackTrace();
        }
        allSocket=new HashSet<>();
    }
//    启动服务器
    public void startService() throws IOException{
        while  (true){
            System.out.println("服务器已启动，等待用户加入···");
            Socket s= serverSocket.accept();
            System.out.println("用户已进入聊天室");
            allSocket.add(s);
            new ServerThread(s).start();
        }
    }
//    服务线程内部类
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
                    if (str.contains("%EXIT%")) {
                        allSocket.remove(socket);
                        sendMessageTOAllClient(str.split(":")[1] + "用户已退出聊天室");
                        socket.close();
                        return;
                    }
                    sendMessageTOAllClient(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendMessageTOAllClient(String message) throws IOException{
                for(Socket s:allSocket){
                    PrintWriter pw=new PrintWriter(s.getOutputStream());
                    pw.println(message);
                    pw.flush();
                }
        }
    }
    public static void main(String[] args){
        try {
            new ChatRoomServer().startService();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
