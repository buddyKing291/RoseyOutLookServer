package nmu.wrpv.rosyoutlook;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    char from;
    private List<User> users;
    private SearchAdapter adapter;
    String where;
    static Activity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        instance= this;
        EditText searchBar = findViewById(R.id.menubar);
        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            String receivedData = receivedIntent.getStringExtra("from");
            if (receivedData.equals("0")) where = "findPublic";
            else where = "findInfluencer";
        }
        /*searchBar.setOnEditorActionListener((TextView textView, int actionId, KeyEvent keyEvent)-> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Perform the desired action here
                    // This code will be executed when the Enter button is clicked
                    searchBar.setText("works");
                    Toast.makeText(getApplicationContext(), "Enter button clicked!", Toast.LENGTH_SHORT).show();
                    return true; // Return true to indicate that the event has been handled
                }
                return false; // Return false to allow other default actions to proceed
            }
        );*/
        //users = User.dummySearch();


        //adapter = new SearchAdapter(users);
        //adapter.setUser(true);

        /*adapter.setOnClickListener( view -> {
        });*///posssibly delete
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.sentView);
        lstUsers.setLayoutManager(layoutManager);
        //lstUsers.setAdapter(adapter);
        searchBar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Perform your desired action here when the "Done" button is clicked on the keyboard
                // Inside your activity or any other context
                Thread find = new Thread(()->{
                    Socket client;
                    OutputStream OS;
                    ObjectOutputStream OOS;
                    InputStream IS;
                    ObjectInputStream OIS;

                    try{

                        client = new Socket("192.168.0.196", 500);//junaid home
                        //client = new Socket("10.202.36.38", 500);

                        // Step 2: Get the input and output streams.
                        OS = new DataOutputStream(client.getOutputStream());
                        OOS = new ObjectOutputStream(OS);
                        Map<String, Map<String,String>> map = new HashMap<>();
                        Map<String,String> details = new HashMap<>();
                        details.put("find",searchBar.getText().toString());
                        details.put("from",MainActivity.account.username);
                        map.put(where,details);


                        OOS.writeObject(map);
                        IS = new DataInputStream(client.getInputStream());
                        OIS = new ObjectInputStream(IS);
                        Map<String, String> received = (Map) OIS.readObject();
                        client.close();
                        users = new ArrayList<>();
                        for(int i = 0; i <received.size()/3;i++){

                            /*String name = received.get("name"+i);
                            String username =received.get("username"+i);
                            UserRelationship r = UserRelationship.NONE;
                            if(received.get("relation"+i).equals("friends")) r = UserRelationship.FRIEND;
                            else if(received.get("relation"+i).equals("pending")) r=UserRelationship.PENDING;
                            System.out.println(username);*/
                            //User u = new User(received.get("name"+i),received.get("username"+i),received.get("type+"+i).equals("friends")?UserRelationship.FRIEND:received.get("type+"+i).equals("pending")?UserRelationship.PENDING:UserRelationship.NONE);
                            users.add(new User(received.get("name"+i),received.get("username"+i),received.get("relation"+i).equals("friends")?UserRelationship.FRIEND:received.get("relation"+i).equals("pending")?UserRelationship.PENDING:received.get("relation"+i).equals("following")?UserRelationship.FOLLOWING:UserRelationship.NONE,where.equals("findPublic")?UserType.REGULAR:UserType.INFLUENCER));
                        }
                        adapter = new SearchAdapter(users);
                        if(users.size()>0)SearchActivity.this.runOnUiThread(() ->lstUsers.setAdapter(adapter));
                        else SearchActivity.this.runOnUiThread(() ->{
                            lstUsers.setAdapter(null);
                            toast("No users found");
                        });
                    }
                    catch(Exception e){
                        SearchActivity.this.runOnUiThread(() ->internalError());
                    }

                });
                find.start();
                return true; // Return true to indicate that the event has been handled
            }
            return false; // Return false if you want to allow further processing of the event
        });
    }
    public void internalError(){
        Toast.makeText(this, "Internal Error, try again", Toast.LENGTH_SHORT).show();}
    public static void toast(String message){
        instance.runOnUiThread(() ->Toast.makeText(instance, message, Toast.LENGTH_SHORT).show());
    }

}