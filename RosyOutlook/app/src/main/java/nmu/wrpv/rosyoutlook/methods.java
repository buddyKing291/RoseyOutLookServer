package nmu.wrpv.rosyoutlook;

import android.os.Build;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class methods {
    private Connection connect = null;
    private Statement state = null;
    public void connectToDB(){
        System.out.println("Establishing connection to database...");

        System.out.println("   Loading JDBC driver for MS SQL Server database...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.printf("   Unable to load JDBC driver... '%s'\n", e.getMessage());
            return;
        }

        System.out.println("   Use driver to connect to MS SQL Server (OPENBOX\\WRR)...");

        try {
            System.out.println("   Locate database to open (using connection string)...");

            String connectionString = "jdbc:mysql://localhost:3306/roseyoutlook";
            System.out.println("      Connection string = " + connectionString);
            //in.nextLine();
            System.out.print("Enter username: ");
            String user= "root";
            System.out.print("Enter password: ");
            String password="lefleurking";
            // create connection to DB, ubcluding username & password
            // NEVER, EVER, include a username and password in your code!!!!
            connect = DriverManager.getConnection(connectionString, user, password);

            // create statement object for manipulating DB
            state = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println("final connection test");
        } catch (Exception e) {
            System.out.printf("   Unable to connect to DB... '%s'\n", e.getMessage());
        }

        System.out.println();
    }

    public void basic(){
        try {
            // perform query on database and retrieve results
            String sql = "SELECT * FROM Track join Album on Track.AID = Album.AID WHERE Artist=''";
            ResultSet result = state.executeQuery(sql);

            // while there are tuples in the result set, display them
            while (result.next()) {
                String song = result.getString("Name");
                String album = result.getString("Title");
                System.out.println("Song:   "+song+"\t\tAlbum:  "+album);
            }

            System.out.println("----------------------------------------------------------------------");

            // close result set when done with it
            System.out.println("   Closing query result set...");
            result.close();

            System.out.println();
        } catch (Exception e) {
            System.out.println("   Was not able to query database...");
        }
    }
    void login(){
        User account = null;
        String username="dummy";
        String password = "dummy";
        try{
            ResultSet result = state.executeQuery("SELECT * FROM user WHERE username='"+username+"'");
            //am i doing this in the right order, does the if need to be in the if?
            if(result == null) throw new Exception();//username doesnt exist
            while(result.next()){
                if(!password.equals(result.getString("password"))) throw new Exception();

                try{
                    ResultSet resultNext ;
                    if("PUBLIC".equals(result.getString("type"))){
                        resultNext = state.executeQuery("SELECT * FROM public WHERE username='"+username+"'");
                        while(resultNext.next()){
                            account = new User(resultNext.getString("prefferedname"),username,UserType.REGULAR,resultNext.getBlob("profilePic"));
                            List<User> friends = new ArrayList<>();
                            for(int i = 1; i<= 2; i++){
                                ResultSet friendResult = state.executeQuery("SELECT * FROM friendship join user on  friendship.F"+i+"_Username=user.username WHERE F"+i+"_Username='"+username+"'");
                                ResultSet curResult;
                                if("PUBLIC".equals(friendResult.getString("type"))){
                                    curResult = state.executeQuery("SELECT * FROM public WHERE username='"+username+"'");
                                }
                                else{
                                    curResult = state.executeQuery("SELECT * FROM influencer WHERE username='"+username+"'");
                                }
                                //lazy now but remeber to add whiles else i dont think will work
                                friends.add(new User(curResult.getString("prefferedname"), friendResult.getString("username"),UserRelationship.FRIEND, curResult.getBlob("profilePic")));
                            }
                            account.setFriends(friends);
                            List<User> following = new ArrayList<>();
                            ResultSet followingResult = state.executeQuery("SELECT * FROM following join influencer on following.InfluencerUsername = influencer.username WHERE Username='"+username+"'");
                            while(followingResult.next()){
                                following.add(new User(followingResult.getString("prefferedname"), followingResult.getString("InfluencerUsername"),UserRelationship.FOLLOWING,followingResult.getBlob("profilePic")));
                            }
                            account.setFollowing(following);
                        }
                    }
                    else if("INFLUENCER".equals((result.getString("type")))){
                        resultNext = state.executeQuery("SELECT * FROM influencer WHERE username='"+username+"'");
                        while(resultNext.next()){

                        }
                    }
                    else{
                        resultNext = state.executeQuery("SELECT * FROM admin WHERE username='"+username+"'");
                        while(resultNext.next()){

                        }
                    }
                    resultNext.close();
                }
                catch(Exception e){

                }
            }

            result.close();
        }
        catch(Exception e){
            //error label to show username and password does not match
        }
    }
    void denyFriend(){
        int fID = 0;
        try {
            state.execute("DELETE FROM friendinvte WHERE FI_ID=" + fID);
        }
        catch(Exception e){

        }
    }
    void acceptFriend(){
        int fID = 0;
        try {
            ResultSet result = state.executeQuery("Select * FROM friendinvite WHERE FI_ID=" + fID);
            while(result.next()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    state.execute("INSERT INTO friendship(F1_Username,F2_Username, Date) VALUES('"+result.getString("toUsername")+"','"+ result.getString("fromUsername")+"',"+ LocalDate.now()+")");
                }
            }
        }
        catch(Exception e){

        }
        try {
            state.execute("DELETE FROM friendinvite WHERE FI_ID=" + fID);
        }
        catch(Exception e){

        }
    }
    void acceptInfluencer(){}
    void rejectInfluencer(){}
    void acceptPetal(){}
    void rejectPetal(){}
}
