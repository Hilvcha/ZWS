package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

class ChatRoomClient {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter pWriter;

    //构造客户端
    ChatRoomClient(String host, int port) throws UnknownHostException, IOException {
        System.out.println("link");
        socket = new Socket(host, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pWriter = new PrintWriter(socket.getOutputStream());
    }

    //发送消息
    public void sendMessage(String str) {
        pWriter.println(str);
        pWriter.flush();
    }

    // 接受消息
    public String reciveMessage() {
        try {
            String str = bufferedReader.readLine();
            System.out.println("接收到消息:" + str);
            return str;
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
