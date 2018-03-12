package messenger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static messenger.Values.PORT;
import static messenger.Values.Server;
/**
 *
 * @author Meluleki
 */
public class ChatClient extends JFrame implements Runnable{
    Socket socket;
    JTextArea ta;
    JButton send ,logout;
    JTextField tf;
    
    
    Thread thread;
    
    //Data inout and output
    DataInputStream din;
    DataOutputStream dout;
    
    String loginName;

    public ChatClient(String login) throws IOException {
        super(login);
        this.loginName=login;
        
        ta= new JTextArea (18, 50);
        tf=new JTextField(50);
        send=new JButton("Send");
        logout= new JButton("Logout");
        
        {
            send.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        dout.writeUTF(loginName+"  "+"Data"+"  "+tf.getText());
                        tf.setText("");
                    } catch (IOException ex) {
                        Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                        System.exit(0x1);
                    }
                }
            });
            
            logout.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        dout.writeUTF(loginName+" "+"LOGOUT");
                    } catch (IOException ex) {
                        Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        
        
        socket= new Socket(Server, (int) PORT);
        
        din=new DataInputStream(socket.getInputStream());
        dout=new DataOutputStream(socket.getOutputStream());
        
        dout.writeUTF(login);
        dout.writeUTF(loginName+" "+"LOGIN");
        
        
        thread = new Thread(this);
        thread.start();
        
        setUp();
    }
    private void setUp() {
        setSize(600,400);
        
        JPanel panel= new JPanel();
        
        panel.add(new JScrollPane(ta));
        panel.add(tf);
        panel.add(send);
        panel.add(logout);
        add(panel);
        
        setVisible(true);
        
    }
    
    @Override
    public void run() {
         while(true){
             try{
                 ta.append("\n"+din.readUTF());
             }catch (IOException e){
                 e.printStackTrace();
             }
         }
    }
    
    public static void main(String[]args) throws IOException{
        ChatClient cc= new ChatClient("User3");
    }
}
