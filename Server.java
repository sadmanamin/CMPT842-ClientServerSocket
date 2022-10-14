import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

public class Server{
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
            out.println("inside mamaeuser");
            this.client = client;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(),true);
            out.println("inside mamaeuser");
            userName = input.readLine();
            out.println(userName);
            output.println("Hello from server");
            
            // String chatRoomNames = getChatRoomNames();
            // output.println("Available chatrooms, please respond with your selected ChatRoom name: \n"+chatRoomNames);
            // String chatRoomName = input.readLine();
            users.add(userName);

            start();
        }

        public void sendMessage(String chatUser, String message) throws IOException {
            out.println("Inside sendmsg");
            output.println(chatUser + " Says:" + message);
        }

        public String getChatUser() {
            return userName;
        }

        public void addUserToChatRoom(String chatRoomName){
            Vector<ManageUser> chatRoom = chatRoomUsers.get(chatRoomName);
            chatRoom.add(this);
        }

        @Override
        public void run() {
            out.println("Inside run");
            String line;
            try {
                while (true) {
                    line = input.readLine();
                    out.println(line);
                    if (line.equals("end")) {
                        clients.remove(this);
                        users.remove(userName);
                        client.close();
                        break;
                    }
                    sendtToAll(userName, line); 
                }
            } 
            catch (Exception ex) {
                System.out.println("exp in create server : " + ex.getMessage());
            }
        } 
    }
}