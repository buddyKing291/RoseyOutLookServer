package nmu.wrpv.rosyoutlook;


import static nmu.wrpv.rosyoutlook.MainActivity.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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

public class ViewFollowingActivity extends AppCompatActivity {
    private List<User> users;
    private ViewUserAdapter adapter;
    private static Activity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_following);
        instance =this;
        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.followingList);
        Thread following = new Thread(()->{
            Socket client;
            OutputStream OS;
            ObjectOutputStream OOS;
            InputStream IS;
            ObjectInputStream OIS;

            try {
                // Step 1: Create a Socket to make connection.
                client = new Socket("192.168.0.196", 500);

                // Step 2: Get the input and output streams.
                OS = new DataOutputStream(client.getOutputStream());
                OOS = new ObjectOutputStream(OS);
                Map<String, Map<String,String>> map = new HashMap<>();
                Map<String,String> details = new HashMap<>();
                details.put("username", account.username);
                map.put("following",details);
                OOS.writeObject(map);
                IS = new DataInputStream(client.getInputStream());
                OIS = new ObjectInputStream(IS);
                Map<String, String> received = (Map) OIS.readObject();
                client.close();
                users = new ArrayList<>();
                for(int i = 0; i<received.size()/2;i++){
                    users.add(new User(received.get("username"+i),received.get("name"+i),UserRelationship.FOLLOWING/*,received.get("date"+i)*/));
                }
                if(account.getFollowing()==null||account.getFollowing().size()< users.size())MainActivity.account.setFollowing(users);
                adapter = new ViewUserAdapter(users);
                adapter.setUser(true);
                if(users.size()>0)instance.runOnUiThread(()->lstUsers.setAdapter(adapter));
                else throw new Exception();

            } catch (Exception e) {
                toast("error");
            }
        });
        following.start();


        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        lstUsers.setLayoutManager(layoutManager);
        TextView searchBar = findViewById(R.id.followingSearchBar);
        searchBar.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        searchBar.setOnClickListener(v->{
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("from","1");
            startActivity(intent);
        });
    }

    public void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(ViewFollowingActivity.this,findViewById(R.id.mainMenuBtn));
        //if(MainActivity.account.type == UserType.REGULAR){
        popupMenu.getMenuInflater().inflate(R.menu.reg_following_menu, popupMenu.getMenu());
        /*}
        else if(MainActivity.account.type == UserType.INFLUENCER){
        popupMenu.getMenuInflater().inflate(R.menu.influencer_following_menu, popupMenu.getMenu());
        }*/
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {//if you can find a way to terminate activities to end up at the first
                startActivity(new Intent(ViewFollowingActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.friends) {
                startActivity(new Intent(ViewFollowingActivity.this, ViewFriendActivity.class));
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
    public static void toast(String message){
        instance.runOnUiThread(() -> Toast.makeText(instance, message, Toast.LENGTH_SHORT).show());
    }
}