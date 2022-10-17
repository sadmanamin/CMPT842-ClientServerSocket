import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

public class Server{
    final String welcome_message = "\n############\n\n Welcome to CMPT842 Chatserver. Use the following commands to operate with the server. \n\n0. Show Menu.\n1. List all the chatrooms.\n2. Join Chatroom.\n3. Create Chatroom.\n4. Leave Chatroom.\n\n Type CMD<SPACE>CMD_NO to run each command.\n\n To send a message, type SEND<SPACE>CHATROOM_NAME<SPACE>YOUR MESSAGE.\n\n############\n";
    Vector<String> users = new Vector<String>();
    Vector<ManageUser> clients = new Vector<ManageUser>();
    Vector<String> chatRoomList = new Vector<String>();
    HashMap<String, String > chatRoomMessages = new HashMap<String, String >();
    HashMap<String, Vector<ManageUser> > chatRoomUsers = new HashMap<String, Vector<ManageUser> >();
    ServerSocket server;
    public static void main(String[] args) throws Exception {
        new Server().createServer();
    }

    public void createServer() throws Exception {
        try {
            server = new ServerSocket(5001);
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
            server.close();
        }
    }

    public void sendMenu(ManageUser user) throws IOException{
        user.sendMessage(welcome_message);
    }

    public String getChatRoomMessages(String chatRoomName){
        String message = chatRoomMessages.get(chatRoomName);
        return message;
    }

    public String getChatRoomNames(){
        String chatRooms = "";

        if(chatRoomList.size() == 0){
            chatRooms = "\nNo chatroom available. Type \"CMD 3 Chatroom_Name\"to create a chatroom.";
        }
        else{
            for(String chatRoom : chatRoomList){
                chatRooms = chatRoom + "\n";
            }

            for(int i = 0; i<chatRoomList.size(); i++){
                chatRooms = Integer.toString(i+1) + ": " +chatRooms + "\n";
            }
        }
        return chatRooms;
    }

    public void createChatRoom(String chatRoomName, ManageUser user) throws IOException{
        if(chatRoomList.indexOf(chatRoomName) != -1){
            user.sendMessage("\nChatroom "+chatRoomName+" already exists. Create a different chatroom.");
        }
        else{
            chatRoomList.add(chatRoomName);
            chatRoomMessages.put(chatRoomName, "");
            chatRoomUsers.put(chatRoomName, new Vector<ManageUser>());
            user.sendMessage("\nChatroom "+chatRoomName+" is created. Type CMD 2 "+chatRoomName+" to join this chatroom.");
        }
    }

    public void joinChatRoom(String chatRoomName, ManageUser user) throws IOException{
        if(chatRoomList.indexOf(chatRoomName) == -1 ){
            user.sendMessage("\nChatroom doesn't exist.");
        }
        else if(chatRoomUsers.get(chatRoomName).indexOf(user) == -1){
            Vector<ManageUser> chatRoom = chatRoomUsers.get(chatRoomName);
            chatRoom.add(user);
            String chatRoomMessage = getChatRoomMessages(chatRoomName);
            sendtToAll(chatRoomName, user.userName+" has joined this chatroom!", user);
            user.sendMessage("\nYou have joined chatroom named "+chatRoomName+".\n" + chatRoomMessage + "\n\nType SEND "+chatRoomName+" YOUR_MESSAGE to send message to this room.\n\nIf you want to leave the room, type CMD 4 "+chatRoomName);
        }
        else{
            user.sendMessage("\nYou are already in this chatroom.");
        }
    }

    public void leaveChatRoom(String chatRoomName, ManageUser user) throws IOException{
        if(chatRoomList.indexOf(chatRoomName) == -1 ){
            user.sendMessage("\nChatroom doesn't exist.");
        }
        else if(chatRoomUsers.get(chatRoomName).indexOf(user) != -1){
            chatRoomUsers.get(chatRoomName).remove(user);
            user.sendMessage("\nYou have left the chatroom.");
            sendtToAll(chatRoomName, user.userName+" has left the chatoom.", user);
        }
        else{
            user.sendMessage("\nYou are not in this chatroom.");
        }
    }

    public void sendtToAll(String chatRoomName, String message, ManageUser user) throws IOException {
        if(chatRoomList.indexOf(chatRoomName) == -1 ){
            user.sendMessage("\nChatroom doesn't exist.");
        }
        else if(chatRoomUsers.get(chatRoomName).indexOf(user) == -1){
            user.sendMessage("\nYou are not in this chatroom.");
        }
        else{
            out.println("Inside sendtoall");
            Vector<ManageUser> clients = chatRoomUsers.get(chatRoomName);
            String chatRoomMessageUpdate = chatRoomMessages.get(chatRoomName) + "\n"+message;
            chatRoomMessages.put(chatRoomName, chatRoomMessageUpdate);

            for (ManageUser c : clients) {
                if (c != user) {
                    try {
                        c.sendMessage("\n("+chatRoomName+") "+message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            this.client = client;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(),true);
           
            userName = input.readLine();
            users.add(userName);

            start();
        }

        public void sendMessage(String message) throws IOException {
            output.println(message);
        }

        public String getChatUser() {
            return userName;
        }

        public void disconnect(){
            try{
                clients.remove(this);
                users.remove(userName);
                client.close(); 
                System.out.println(userName+  " is disconnected.");
            }  
            catch (Exception ex) {
                System.out.println("exp in disconnect : " + ex.getMessage());
            }       
        }

        public void parse_message(String msg) throws IOException{
            String[] statements = msg.split(" ");

            if(statements[0].equals("CMD")){
                if(statements[1].equals("1")){
                    String chatRooms = getChatRoomNames();
                    sendMessage("\n List of chatrooms\n\n" + chatRooms+"\n\nType \"CMD 2 Chatroom_Name\" to join a chatroom.\n");
                }
                else if(statements[1].equals("2")){
                    String chatRoomName = statements[2];
                    joinChatRoom(chatRoomName, this);
                }
                else if(statements[1].equals("3")){
                    String chatRoomName = statements[2];
                    createChatRoom(chatRoomName, this);
                }
                else if(statements[1].equals("4")){
                    String chatRoomName = statements[2];
                    leaveChatRoom(chatRoomName, this);
                }
                else if(statements[1].equals("0")){
                    sendMenu(this);
                }
                else{
                    sendMessage("Invalid Message format!");
                }
            }
            else if(statements[0].equals("SEND")){
                String chatRoomName = statements[1];
                String userMsg = statements[2];
                userMsg = userName+" > "+userMsg;
                sendtToAll(chatRoomName, userMsg, this);
            }
            else{
                sendMessage("Invalid Message format!");
            }
        }

        @Override
        public void run() {
            // out.println("Inside run");
            String msg;
            try {
                sendMenu(this);
                msg = input.readLine();

                while(msg!=null){
                    out.println(msg);
                    parse_message(msg);
                    msg = input.readLine();
                }
                disconnect();
            } 
            catch (Exception ex) {
                System.out.println("exp in create server : " + ex.getMessage());
            }
        } 
    }
}
