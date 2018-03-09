/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import static messenger.ChatServer.clientsockets;
import static messenger.ChatServer.loginnames;

/**
 *
 * @author Meluleki
 */
public class ChatServer {

    static ArrayList<Socket> clientsockets;
    static ArrayList<String> loginnames;

    public ChatServer() throws IOException {
        ServerSocket server = new ServerSocket(5217);
        clientsockets = new ArrayList<>();
        loginnames = new ArrayList<>();

        while (true) {
            Socket client = server.accept();
            AcceptClient acceptClient = new AcceptClient(client);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        ChatServer server = new ChatServer();
    }

}

class AcceptClient extends Thread {

    Socket ClientSocket;
    DataInputStream din;
    DataOutputStream dout;

    public AcceptClient(Socket client) throws IOException {
        this.ClientSocket = client;
        this.din = new DataInputStream(ClientSocket.getInputStream());
        this.dout = new DataOutputStream(ClientSocket.getOutputStream());

        String LoginName = din.readUTF();
        loginnames.add(LoginName);
        clientsockets.add(ClientSocket);

        start();//Okay aqs we will nnot inherit from the class AcceptClient
    }

    @Override
    public void run() {
        while (true) {
            try {
                // do something in heere to accept a connection from the clients to the server.
                String msgFromClient = din.readUTF();
                StringTokenizer st = new StringTokenizer(msgFromClient);// to check which client is sending the message
                String LoginName = st.nextToken();
                String MsgeType = st.nextToken();
                String msg = "";
                int lo=-1;
                while (st.hasMoreTokens()) {
                    msg=msg+" "+st.nextToken();
                }

                if (MsgeType.equals("LOGIN")) {
                    for (int i = 0; i < loginnames.size(); i++) {
                        Socket pSocket = (Socket) clientsockets.get(i);
                        DataOutputStream out = new DataOutputStream(pSocket.getOutputStream());
                        out.writeUTF(LoginName + " has logged in");
                    }
                } else if (MsgeType.equals("LOGOUT")) {
                    for (int i = 0; i < loginnames.size(); i++) {
                        if(LoginName.equals(loginnames.get(i))){
                            lo=i;
                        }
                        Socket pSocket = (Socket) clientsockets.get(i);
                        DataOutputStream out = new DataOutputStream(pSocket.getOutputStream());
                        out.writeUTF(LoginName + " has logged out");
                    }
                    if(lo>=0){
                        loginnames.remove(lo);
                        clientsockets.remove(lo);
                    }
                } else {
                    for (int i = 0; i < loginnames.size(); i++) {
                        Socket pSocket = (Socket) clientsockets.get(i);
                        DataOutputStream out = new DataOutputStream(pSocket.getOutputStream());
                        out.writeUTF(LoginName + ":" +msg);
                    }
                }
            } catch (IOException e) {
                //print the exception or do someting witht the exception tjat is foudn
                e.printStackTrace();
            }
        }
    }

}
