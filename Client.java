import java.net.*;
import java.io.*;
import java.util.*;

public class Client{
    private Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        Client client = new Client();
        String msg;
        try{
            client.startConnection("localhost",5001);        
            while(true){
                msg = sc.nextLine();
                System.out.println(client.sendMessage(msg));
            }
        }
        catch(Exception e){
            System.out.println(e);
        }        
    }

    public void startConnection(String ip, int port) throws Exception{
        clientSocket = new Socket(ip, port);
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println(sendMessage("Hello from client 1"));
    }

    public String sendMessage(String msg) {
        try{
            output.println(msg+"\n");
            String resp = input.readLine();
            return resp;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void stopConnection() {
        try{
            input.close();
            output.close();
            clientSocket.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}