import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class ChatRoomServer {
    private ServerSocket serverSocket;
    private HashMap<Socket,String> alluser;
//    聊天室服务器构造方法
    public ChatRoomServer(){
        try {
            serverSocket=new ServerSocket(4560);
        }catch (IOException e){
            e.printStackTrace();
        }
        alluser=new HashMap<>();
    }
//    启动服务器
    public void startService() throws IOException{
        System.out.println("服务器已启动，等待用户加入···");
        while  (true){
            Socket s= serverSocket.accept();
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
                    if(str.contains("%NAME%")){
                        String name=str.split(":")[1];
                        //用户名有重复
                        if(alluser.values().contains(name)){
                            String message="%NAMEERROR%";
                            sendMessageToONEClient(message,socket);
                        }else {
                            alluser.put(socket,name);
                        }
                        continue;
                    }
                    if (str.contains("%EXIT%")) {
                        alluser.remove(socket);
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
        public void sendMessageToONEClient(String message,Socket s) throws IOException{
            System.out.println("将要向"+s+"发送:"+message);
            PrintWriter pw=new PrintWriter(s.getOutputStream());
            pw.println(message);
            pw.flush();
        }
        public void sendMessageTOAllClient(String message) throws IOException{
            System.out.println("将要向所有用户发送:"+message);
                for(Socket s:alluser.keySet()){
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
