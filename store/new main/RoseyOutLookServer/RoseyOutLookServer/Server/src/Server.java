

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.getConnection;

public class Server {
    public Server() {
        runServer();
    }

    static Connection connect = null;
    static Statement state = null;

    /**
     * Connects to the database
     */
    public void connectToDB(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            return;
        }

        try {
            //String connectionString = "jdbc:mysql://localhost:3306/roseyoutlook";//Junaids local instance
            String connectionString = "jdbc:mysql://mysql-roseyoutlook.alwaysdata.net:3306/roseyoutlook_db";//online version no foreign keys
            //String user= "root";//Junaids local instance
            String user= "322785";//online version
            //String password="lefleurking";//Junaids local
            String password="lefleurking";//online
            connect = getConnection(connectionString, user, password);
            state = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (Exception e) {
            System.out.println("why");

        }

    }

    /**
     * Starts the server
     * Receives and handles requests from clients
     */
    public void runServer() {
        connectToDB();
        ServerSocket server;
        Socket connection;
        OutputStream OS;
        ObjectOutputStream OOS;
        InputStream IS;
        ObjectInputStream OIS;

        try {
            // Step 1: Create a ServerSocket.
            server = new ServerSocket(500, 100);
            System.out.println("SERVER: Server started "
                    + InetAddress.getLocalHost().getHostAddress()
            );


            while (true) {

                // Step 2: Wait for a connection.
                connection = server.accept();
                System.out.println("SERVER: Connection "
                        + " received from: "
                        + connection.getInetAddress().getHostName());
                // Step 3: Get dis and dos streams.//would here onwards also need a while?
                IS = new DataInputStream(connection.getInputStream());
                OIS = new ObjectInputStream(IS);
                Map<String, Map<String,String>> requests = (Map) OIS.readObject();
                Map<String,String> output = new HashMap<>();
                List<String> keys = new ArrayList<>(requests.keySet());
                for(int i = 0; i < keys.size();i++){
                    switch(keys.get(i)) {
                        case "login":
                            login(requests.get(keys.get(i)),output);
                            break;
                        case "friend":
                            getFriends(requests.get(keys.get(i)).get("username"),output);
                            break;
                        case "following":
                            getFollowing(requests.get(keys.get(i)).get("username"),output);
                            break;
                        case "addfriend":
                            addFriend(requests.get(keys.get(i)),output);
                            break;
                        case "follow":
                            follow(requests.get(keys.get(i)),output);
                            break;
                        case "findPublic":
                            findPublic(requests.get(keys.get(i)),output);
                            break;
                        case "findInfluencer":
                            findInfluencer(requests.get(keys.get(i)),output);
                            break;
                        case "getRecievedFriends":
                            getRecievedFriends(requests.get(keys.get(i)),output);
                            break;
                        case "getSentFriends":
                            getSentFriends(requests.get(keys.get(i)),output);
                            break;
                        default:
                            break;
                    }
                }
                OS = new DataOutputStream(connection.getOutputStream());
                OOS = new ObjectOutputStream(OS);
                OOS.writeObject(output);
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if current information is valid
     * @param data - log in info
     * @param output - extra info
     */
    public void login(Map<String,String> data, Map<String,String> output){
        try{
            ResultSet result = state.executeQuery("SELECT * FROM user WHERE username='"+data.get("username")+"' OR email='"+data.get("username")+"'");
            if(!result.last()){
                output.put("error", "user");
                return;
            }
            result.beforeFirst();
            while(result.next()){
                /*String username = result.getString("username");
                String email = result.getString("email");*/
                String password = result.getString("password");
                if(!password.equals(data.get("password"))){
                    output.put("error","match");
                    return;
                }
                output.put("username",result.getString("username"));
                String type = result.getString("type");
                switch(type){
                    case "PUBLIC":
                        getPublicData(output);
                        break;
                    case "INFLUENCER":
                        getInfluencerData(output);
                        break;
                    case "ADMIN":
                        getAdminData(output);
                        break;
                }
                output.put("type",type);
                Blob blob = result.getBlob("profilePic");
                byte[] bdata = blob.getBytes(1, (int) blob.length());
                output.put("image",new String(bdata));
               /* String value = (s);//how to convert back to blob in main
                byte[] buff = value.getBytes();
                Blob blob = new SerialBlob(buff);*/
            }
        }
        catch(Exception e){
            /*password.setText("");
            error.setText("Internal error please try again");
            error.setTextColor(Color.RED);*/
            return;
        }
    }
    public void getAdminData(Map<String,String> output){
        try{
            ResultSet result = state.executeQuery("SELECT * FROM admin WHERE username='"+output.get("username")+"'");
            while(result.next()){
                output.put("name",result.getString("FirstName")+" "+result.getString("Surname"));
            }
        }
        catch(Exception e){

        }
    }
    public void getPublicData(Map<String,String> output){
        try{
            ResultSet result = state.executeQuery("SELECT * FROM public WHERE username='"+output.get("username")+"'");
            while(result.next()){
                output.put("name",result.getString("prefferedName"));
                //get friends and those they follow
            }
        }
        catch(Exception e){

        }
    }
    public void getInfluencerData(Map<String,String> output){
        try{
            ResultSet result = state.executeQuery("SELECT * FROM influencer WHERE username='"+output.get("username")+"'");
            while(result.next()){
                output.put("name",result.getString("prefferedName"));
                //get friends and those they follow
            }
        }
        catch(Exception e){

        }
    }
    public void getFriends(String username, Map<String,String> output){//here
        try{
            ResultSet result = state.executeQuery("SELECT * FROM friendship  WHERE F1_Username='"+username+"' Or F2_Username='"+username+"'");
            int cur =0;
            while(result.next()){
                String friend = result.getString("F1_Username");
                if(friend.equals(username)) {
                    ResultSet typeResult = state.executeQuery("SELECT * FROM user  WHERE username='"+result.getString("F2_Username")+"'");
                    output.put("username" + cur, typeResult.getString("username"));
                    output.put("type"+cur,typeResult.getString("type"));

                    ResultSet nameResult = state.executeQuery("SELECT * FROM "+(output.get("type").equalsIgnoreCase("PUBLIC")?"public":"influencer")+"  WHERE username='"+result.getString("F2_Username")+"'");
                    output.put("name" + cur++, nameResult.getString("prefferedName"));
                    /*Blob blob = result.getBlob("profilePic");
                    byte[] bdata = blob.getBytes(1, (int) blob.length());
                    output.put("image" + cur++, new String(bdata));*/
                }
                else{
                    ResultSet typeResult = state.executeQuery("SELECT * FROM user  WHERE username='"+result.getString("F1_Username")+"'");
                    output.put("username" + cur, typeResult.getString("username"));
                    output.put("type"+cur,typeResult.getString("type"));

                    ResultSet nameResult = state.executeQuery("SELECT * FROM "+(output.get("type").equalsIgnoreCase("PUBLIC")?"public":"influencer")+"  WHERE username='"+result.getString("F2_Username")+"'");
                    output.put("name" + cur++, nameResult.getString("prefferedName"));
                    /*Blob blob = result.getBlob("profilePic");
                    byte[] bdata = blob.getBytes(1, (int) blob.length());
                    output.put("image" + cur++, new String(bdata));*/
                }
            }
        }
        catch(Exception e){
            output.put("error","error");
        }
    }
    public void getFollowing(String username, Map<String,String> output){
        try{
            ResultSet result = state.executeQuery("SELECT influencer.username,influencer.prefferedName FROM following join influencer on following.InfluencerUsername = influencer.username WHERE following.Username='"+username+"'");
            int cur = 0;
            while(result.next()){
                output.put("username"+cur,result.getString("username"));
                output.put("name"+cur++,result.getString("prefferedName"));
                /*Blob blob = result.getBlob("profilePic");
                byte[] bdata = blob.getBytes(1, (int) blob.length());
                output.put("image"+cur++,new String(bdata));*/
            }
        }
        catch(Exception e){
            output.put("error","error");
        }
    }
    public void addFriend(Map<String,String> input,Map<String,String> output){
        try{//remember you added a not
            String u1 =input.get("username1");
            String u2 = input.get("username2");
            if(!state.execute("INSERT INTO friendinvite (fromUsername,toUsername) VALUES ('"+input.get("username1")+"','"+input.get("username2")+"')"))
                output.put("inserted","true");
            else throw new Exception();
        }
        catch(Exception e){
            output.put("error","false");
        }
    }

    public void follow(Map<String,String> input,Map<String,String> output){
        try{
            if(!state.execute("INSERT INTO following (Username,InfluencerUsername) VALUES ('"+input.get("username1")+"','"+input.get("username2")+"')"))
                output.put("inserted","true");
            else throw new Exception();
        }
        catch(Exception e){
            output.put("error","false");
        }
    }
    public void viewFollow(Map<String,String> input,Map<String,String> output){
        try{
            state.executeQuery("INSERT INTO following (Username,InfluencerUsername) VALUES ("+input.get("username1")+","+input.get("username2")+")");
            output.put("inserted","true");
        }
        catch(Exception e){
            output = null;
        }
    }
    public void findPublic(Map<String,String> input,Map<String,String> output){
        try{
            String username = input.get("from");
            ResultSet result = state.executeQuery("SELECT * FROM public WHERE (username LIKE'%"+input.get("find")+"%' OR prefferedName LIKE '%"+input.get("find")+"%')AND NOT username ='"+username+"' AND NOT username = 'admin'");
            int cur = 0;
            while(result.next()){
                String searched = result.getString("username");
                output.put("username"+cur,searched);
                output.put("name"+cur,result.getString("prefferedName"));
                boolean found = false;
                ResultSet findResult = state.executeQuery("SELECT * FROM friendinvite WHERE (fromUsername ='"+searched+"' AND toUsername ='"+username+"') OR (fromUsername ='"+username+"' AND toUsername ='"+searched+"')");
                while(findResult.next()){
                    output.put("relation"+cur,"pending");
                    found = true;
                }
                findResult = state.executeQuery("SELECT * FROM friendship WHERE (F1_Username ='"+searched+"' AND F2_Username ='"+username+"') OR (F1_Username ='"+username+"' AND F2_Username ='"+searched+"')");
                while(findResult.next()){
                    output.put("relation"+cur,"friends");
                    found = true;
                }
                if(!found)output.put("relation"+cur,"none");
                cur++;
            }
        }
        catch(Exception e){

        }
    }
    public void getRecievedFriends(Map<String,String> input,Map<String,String> output){
        try {
            String username = input.get("username");

            String selectQuery = "SELECT * FROM friendinvite WHERE toUsername = ?";
            try (PreparedStatement selectStatement = connect.prepareStatement(selectQuery)) {
                selectStatement.setString(1, username);

                try (ResultSet result = selectStatement.executeQuery()) {
                    int cur = 0;
                    while (result.next()) {
                        String sender = result.getString("fromUsername");
                        output.put("username" + cur, sender);

                        String typeQuery = "SELECT type FROM user WHERE username = ?";
                        try (PreparedStatement typeStatement = connect.prepareStatement(typeQuery)) {
                            typeStatement.setString(1, sender);

                            try (ResultSet typeResult = typeStatement.executeQuery()) {
                                if (typeResult.next()) {
                                    String userType = typeResult.getString("type");

                                    String nameQuery = "";
                                    if ("public".equalsIgnoreCase(userType)) {
                                        nameQuery = "SELECT prefferedName FROM public WHERE username = ?";
                                    } else {
                                        nameQuery = "SELECT prefferedName FROM influencer WHERE username = ?";
                                    }

                                    try (PreparedStatement nameStatement = connect.prepareStatement(nameQuery)) {
                                        nameStatement.setString(1, sender);

                                        try (ResultSet nameResult = nameStatement.executeQuery()) {
                                            if (nameResult.next()) {
                                                output.put("name" + cur, nameResult.getString("prefferedName"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        cur++;
                    }
                }
            }
        } catch (Exception e) {
            output.put("error", "error");
        }
        /*try{
            ResultSet result = state.executeQuery("SELECT * FROM friendinvite WHERE toUsername='"+input.get("username")+"'");
            int cur = 0;
            while(result.next()){
                String sender = result.getString("fromUsername");
                output.put("username"+cur,sender);
                ResultSet typeResult = state.executeQuery("SELECT * FROM user WHERE username='"+sender+"'");
                while(typeResult.next()){
                    if(typeResult.getString("type").equalsIgnoreCase("public")){
                        ResultSet nameResult = state.executeQuery("SELECT * FROM public WHERE username='"+sender+"'");
                        while(nameResult.next()){
                            output.put("name"+cur++,nameResult.getString("prefferedName"));
                        }
                        nameResult.close();
                    }
                    else{
                        ResultSet nameResult = state.executeQuery("SELECT * FROM influencer WHERE username='"+sender+"'");
                        while(nameResult.next()){
                            output.put("name"+cur++,nameResult.getString("prefferedName"));
                        }
                        nameResult.close();
                    }
                    break;
                }
                typeResult.close();
            }
            result.close();
        }
        catch(Exception e){
            output.put("error","error");
        }*/
    }
    public void getSentFriends(Map<String,String> input,Map<String,String> output){
        try {
            String username = input.get("username");

            String selectQuery = "SELECT * FROM friendinvite WHERE fromUsername = ?";
            try (PreparedStatement selectStatement = connect.prepareStatement(selectQuery)) {
                selectStatement.setString(1, username);

                try (ResultSet result = selectStatement.executeQuery()) {
                    int cur = 0;
                    while (result.next()) {
                        String sender = result.getString("toUsername");
                        output.put("username" + cur, sender);

                        String typeQuery = "SELECT type FROM user WHERE username = ?";
                        try (PreparedStatement typeStatement = connect.prepareStatement(typeQuery)) {
                            typeStatement.setString(1, sender);

                            try (ResultSet typeResult = typeStatement.executeQuery()) {
                                if (typeResult.next()) {
                                    String userType = typeResult.getString("type");

                                    String nameQuery = "";
                                    if ("public".equalsIgnoreCase(userType)) {
                                        nameQuery = "SELECT prefferedName FROM public WHERE username = ?";
                                    } else {
                                        nameQuery = "SELECT prefferedName FROM influencer WHERE username = ?";
                                    }

                                    try (PreparedStatement nameStatement = connect.prepareStatement(nameQuery)) {
                                        nameStatement.setString(1, sender);

                                        try (ResultSet nameResult = nameStatement.executeQuery()) {
                                            if (nameResult.next()) {
                                                output.put("name" + cur, nameResult.getString("prefferedName"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        cur++;
                    }
                }
            }
        } catch (Exception e) {
            output.put("error", "error");
        }
        /*try{
            ResultSet result = state.executeQuery("SELECT * FROM friendinvite WHERE fromUsername='"+input.get("username")+"'");
            int cur = 0;
            while(result.next()){
                String sender = result.getString("toUsername");
                output.put("username"+cur,sender);
                ResultSet typeResult = state.executeQuery("SELECT * FROM user WHERE username='"+sender+"'");
                while(typeResult.next()){
                    if(typeResult.getString("type").equalsIgnoreCase("public")){
                        ResultSet nameResult = state.executeQuery("SELECT * FROM public WHERE username='"+sender+"'");
                        while(nameResult.next()){
                            output.put("name"+cur++,nameResult.getString("prefferedName"));
                        }
                    }
                    else{
                        ResultSet nameResult = state.executeQuery("SELECT * FROM influencer WHERE username='"+sender+"'");
                        while(nameResult.next()){
                            output.put("name"+cur++,nameResult.getString("prefferedName"));
                        }
                    }
                }
            }
        }
        catch(Exception e){
            output.put("error","error");
        }*/
    }
    public void findInfluencer(Map<String,String> input,Map<String,String> output) {
        try{
            String username = input.get("from");
            ResultSet result = state.executeQuery("SELECT * FROM influencer WHERE (username LIKE'%"+input.get("find")+"%' OR prefferedName LIKE '%"+input.get("find")+"%')AND NOT username ='"+username+"' AND NOT username = 'admin'");
            int cur = 0;
            while(result.next()){
                String searched = result.getString("username");
                output.put("username"+cur,searched);
                output.put("name"+cur,result.getString("prefferedName"));
                boolean found = false;
                ResultSet findResult = state.executeQuery("SELECT * FROM following WHERE (Username ='"+username+"' AND InfluencerUsername ='"+searched+"')");
                while(findResult.next()){
                    output.put("relation"+cur,"following");
                    found = true;
                }
                /*findResult = state.executeQuery("SELECT * FROM friendship WHERE (F1_Username ='"+searched+"' AND F2_Username ='"+username+"') OR (F1_Username ='"+username+"' AND F2_Username ='"+searched+"')");
                while(findResult.next()){
                    output.put("relation"+cur,"friends");
                    found = true;
                }*/
                if(!found)output.put("relation"+cur,"none");
                cur++;
            }
        }
        catch(Exception e){

        }
    }
    public static void main(String args[]) {
        new Server();
    }
}
