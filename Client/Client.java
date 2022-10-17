import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket; // socket used by client to send and recieve data from server
    private BufferedReader in;   // object to read data from socket
    private PrintWriter out; 
    final Scanner sc = new Scanner(System.in); 
    public static void main(String[] args){
            // object to write data into socket
        try {
            Client client = new Client();
            client.connect("localhost", 5001);           
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void connect(String host, int port) throws Exception{
        clientSocket = new Socket(host , port);
        out = new PrintWriter(clientSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Type your name:");
        String name = sc.nextLine();
        sendMessage(name);
        
        startSender();
        startReceiver();
    }

    public void sendMessage(String msg) throws Exception{
        if(msg.equals("EXIT")){
            System.exit(0);
        }

        out.println(msg);
        out.flush();
    }

    public void startSender(){
        Thread sender = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
                try{
                    while(true){
                        msg = sc.nextLine();
                        sendMessage(msg);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        sender.start();
    }

    public void startReceiver(){
        Thread receiver = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
                try {
                    msg = in.readLine();
                    while(msg!=null){
                        System.out.println(msg);
                        msg = in.readLine();
                    }
                    System.out.println("Server out of service");
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receiver.start();
    }
}
