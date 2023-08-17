package nmu.wrpv.rosyoutlook;


import static nmu.wrpv.rosyoutlook.MainActivity.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewFriendActivity extends AppCompatActivity {
    private List<User> users;
    private ViewUserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        setFriends();

    }
    public void setFriends(){
        //users = User.dummyUsers();
        users = new ArrayList<>();



        adapter = new ViewUserAdapter(users);
        adapter.setUser(true);

        adapter.setOnClickListener( view -> {
        });//posssibly delete
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.friendsView);
        lstUsers.setLayoutManager(layoutManager);
        lstUsers.setAdapter(adapter);
        TextView searchBar = findViewById(R.id.menubar);
        searchBar.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);

        Thread getFriends = new Thread(()->{
            Socket client;
            OutputStream OS;
            ObjectOutputStream OOS;
            InputStream IS;
            ObjectInputStream OIS;

            //ActivityCompat.requestPermissions(this, "Manifest.permission.",1);
            try {
                // Step 1: Create a Socket to make connection.
                //client = new Socket("10.202.36.38", 500);//campus stuff
                client = new Socket("192.168.0.196", 500);//junaid home
                //client = new Socket("10.202.36.38", 500);

                // Step 2: Get the input and output streams.
                OS = new DataOutputStream(client.getOutputStream());
                OOS = new ObjectOutputStream(OS);
                Map<String, Map<String,String>> map = new HashMap<>();
                Map<String,String> details = new HashMap<>();
                details.put("username",account.username);
                map.put("friend",details);
                OOS.writeObject(map);
                IS = new DataInputStream(client.getInputStream());
                OIS = new ObjectInputStream(IS);
                Map<String, String> received = (Map) OIS.readObject();
                if(received.get("error") != null ){
                    Toast.makeText(this, "Internal error", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i = 0; i<received.size()/3;i++){//username prefname profile pic
                    //add to adapter
                    User user = new User(received.get("username"+i),received.get("name"+1));
                    //get image//decide if you want to include profilepics
                    user.setImage(null);
                    adapter.add(user);
                }
                client.close();
            } catch (IOException e) {//make toast
                Log.e("error", e.getMessage());
            } catch (ClassNotFoundException e) {//make toast
            }
        });
        getFriends.start();

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Perform the desired action here
                    // This code will be executed when the Enter button is clicked
                    return true; // Return true to indicate that the event has been handled
                }
                return false; // Return false to allow other default actions to proceed
            }
        });
        searchBar.setOnClickListener(v->{
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("from","0");
            startActivity(intent);
        });
    }

    public void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(ViewFriendActivity.this,findViewById(R.id.mainMenuBtn));
        //if(MainActivity.account.type == UserType.REGULAR){
        popupMenu.getMenuInflater().inflate(R.menu.reg_friend_menu, popupMenu.getMenu());
        /*}
        else if(MainActivity.account.type == UserType.INFLUENCER){
        popupMenu.getMenuInflater().inflate(R.menu.influencer_following_menu, popupMenu.getMenu());
        }*/
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {//if you can find a way to terminate activities to end up at the first
                startActivity(new Intent(ViewFriendActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.following) {
                startActivity(new Intent(ViewFriendActivity.this, ViewFollowingActivity.class));
                return true;
            } else if (id == R.id.GH) {
                //call view gratitude history
                return true;
            } else if (id == R.id.infApp) {
                //call influencer application
                return true;
            } else if (id == R.id.logout) {
                //call log in
                return true;
            }

            return false;

        });
        popupMenu.show();
    }

    private List<User> requests;
    private UserRequestAdapter requestAdapter;
    private List<User> sent;
    private UserSentAdapter sentAdapter;
    public void setRequests(){
        //sent = User.dummyUsers();
        Thread recievedRequests = new Thread(()->{
            Socket client;
            OutputStream OS;
            ObjectOutputStream OOS;
            InputStream IS;
            ObjectInputStream OIS;

            //ActivityCompat.requestPermissions(this, "Manifest.permission.",1);
            try {
                // Step 1: Create a Socket to make connection.
                //client = new Socket("10.202.36.38", 500);//campus stuff
                client = new Socket("192.168.0.196", 500);//junaid home
                //client = new Socket("10.202.36.38", 500);

                // Step 2: Get the input and output streams.
                OS = new DataOutputStream(client.getOutputStream());
                OOS = new ObjectOutputStream(OS);
                Map<String,Map<String,String>> map = new HashMap<>();
                Map<String,String> details = new HashMap<>();
                details.put("username", account.username);
                map.put("getRecievedFriends",details);
                OOS.writeObject(map);
                IS = new DataInputStream(client.getInputStream());
                OIS = new ObjectInputStream(IS);
                Map<String, String> received = (Map) OIS.readObject();
                client.close();
                if(received.containsKey("error")){
                    return;
                }
                requests = new ArrayList<>();
                for(int i = 0; i< received.size()/2;i++){
                    requests.add(new User(received.get("name"+i),received.get("username"+i)));
                }
                requestAdapter = new UserRequestAdapter(requests);
                requestAdapter.setUser(true);
                ViewFriendActivity.this.runOnUiThread(()->((RecyclerView)findViewById(R.id.requestsView)).setAdapter(requestAdapter));
            } catch (IOException e) {
                //error.setText("Internal Error please try again");
                Log.e("error", e.getMessage());
            } catch (ClassNotFoundException e) {
                //error.setText("Internal Error please try again");
            }
        });
        recievedRequests.start();
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView requestView = findViewById(R.id.requestsView);
        requestView.setLayoutManager(rlayoutManager);
        Thread sentRequests = new Thread(()->{
            Socket client;
            OutputStream OS;
            ObjectOutputStream OOS;
            InputStream IS;
            ObjectInputStream OIS;

            //ActivityCompat.requestPermissions(this, "Manifest.permission.",1);
            try {
                // Step 1: Create a Socket to make connection.
                //client = new Socket("10.202.36.38", 500);//campus stuff
                client = new Socket("192.168.0.196", 500);//junaid home
                //client = new Socket("10.202.36.38", 500);

                // Step 2: Get the input and output streams.
                OS = new DataOutputStream(client.getOutputStream());
                OOS = new ObjectOutputStream(OS);
                Map<String,Map<String,String>> map = new HashMap<>();
                Map<String,String> details = new HashMap<>();
                details.put("username", account.username);
                map.put("getSentFriends",details);
                OOS.writeObject(map);
                IS = new DataInputStream(client.getInputStream());
                OIS = new ObjectInputStream(IS);
                Map<String, String> received = (Map) OIS.readObject();
                client.close();
                if(received.containsKey("error")){
                    return;
                }
                sent = new ArrayList<>();
                for(int i = 0; i< received.size()/2;i++){
                    sent.add(new User(received.get("name"+i),received.get("username"+i)));
                }
                sentAdapter = new UserSentAdapter(sent);
                sentAdapter.setUser(true);
                ViewFriendActivity.this.runOnUiThread(()->((RecyclerView)findViewById(R.id.sentView)).setAdapter(sentAdapter));
            } catch (IOException e) {
                //error.setText("Internal Error please try again");
                Log.e("error", e.getMessage());
            } catch (ClassNotFoundException e) {
                //error.setText("Internal Error please try again");
            }
        });
        sentRequests.start();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView sentView = findViewById(R.id.sentView);
        sentView.setLayoutManager(layoutManager);


        //requests = User.dummyUsers();
    }
    public void myFriends(View view) {
        setContentView(R.layout.activity_view_friend);
        setFriends();
    }

    public void myRequests(View view) {
        setContentView(R.layout.activity_view_friend_request);
        setRequests();
    }
}