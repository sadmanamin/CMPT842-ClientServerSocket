import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

public class Server{
    final welcome_message = "Welcome to CMPT842 Chatserver. Use the following commands to operate with the server. \n
    1. List all the chatrooms.\n
    2. Join Chatroom.\n
    3. Create Chatroom.\n
    4. Leave Chatroom.\n\n
    "


    public static void main(String[] args) throws Exception {
        new Server().createServer();
    }

    Vector<String> users = new Vector<String>();
    Vector<ManageUser> clients = new Vector<ManageUser>();
    Vector<String> chatRoomList = new Vector<String>();
    HashMap<String, Vector<String> > chatRoomMessages = new HashMap<String, Vector<String> >();
    HashMap<String, Vector<ManageUser> > chatRoomUsers = new HashMap<String, Vector<ManageUser> >();

    public void createServer() throws Exception {
        try {
            ServerSocket server = new ServerSocket(5001);
            out.println("Now Server Is Running");
            while (true) {
                Socket client = server.accept();
                out.println("New client came");
                ManageUser c = new ManageUser(client);
                clients.add(c);
            }
        }
        catch(Exception e){
            out.println("exp in create server : " + e);
        }
    }

    public String getChatRoomNames(){
        String chatRooms = "";
        for(String chatRoom : chatRoomList){
            chatRooms = chatRoom + "\n";
        }
        return chatRooms;
    }

    public void sendtToAll(String user, String message) {
        out.println("Inside sendtoall");
        for (ManageUser c : clients) {
            if (!c.getChatUser().equals(user)) {
                try {
                    c.sendMessage(user, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ManageUser extends Thread{
        String userName = "";
        String chatRoom = "";
        BufferedReader input;
        PrintWriter output;
        Socket client;
    
        public ManageUser(Socket client) throws Exception {
            // out.println("inside mamaeuser");
            this.client = client;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(),true);
            // out.println("inside mamaeuser");
            userName = input.readLine();
            out.println(userName);
            // output.println("Hello from server");
            
            // String chatRoomNames = getChatRoomNames();
            // output.println("Available chatrooms, please respond with your selected ChatRoom name: \n"+chatRoomNames);
            // String chatRoomName = input.readLine();
            users.add(userName);

            start();
        }

        public void sendMessage(String chatUser, String message) throws IOException {
            // out.println("Inside sendmsg");
            output.println(chatUser + " Says:" + message);
        }

        public String getChatUser() {
            return userName;
        }

        public void addUserToChatRoom(String chatRoomName){
            Vector<ManageUser> chatRoom = chatRoomUsers.get(chatRoomName);
            chatRoom.add(this);
        }

        public void disconnect(){
            try{
                clients.remove(this);
                users.remove(userName);
                client.close(); 
                sendtToAll(userName, userName+  " is disconnected.");  
            }  
            catch (Exception ex) {
                System.out.println("exp in disconnect : " + ex.getMessage());
            }       
        }

        @Override
        public void run() {
            // out.println("Inside run");
            // String line;
            String msg;
            try {

                msg = input.readLine();

                while(msg!=null){
                    out.println(msg);
                    if (msg.equals("end")) {
                        // disconnect();
                        break;                        
                    }
                    sendtToAll(userName, msg); 
                    msg = input.readLine();
                }
                disconnect();
                System.out.println("Client déconecté");
            } 
            catch (Exception ex) {
                System.out.println("exp in create server : " + ex.getMessage());
            }
        } 
    }
}