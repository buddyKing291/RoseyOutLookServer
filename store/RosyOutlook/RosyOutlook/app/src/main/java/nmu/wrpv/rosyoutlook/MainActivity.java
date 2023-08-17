package nmu.wrpv.rosyoutlook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static User account;
    private List<Post> posts;
    private PostAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(account == null){
            //log in
            //setfriends and following
        }
        /*if(account.type == UserType.REGULAR)
            setContentView(R.layout.activity_main);
        else if(account.type == UserType.INFLUENCER)*/
        setContentView(R.layout.activity_main_influencer);
        /*else if(account.type == UserType.ADMIN)
            setContentView(R.layout.activity_main_admin);*/

        if(account != null && account.type == UserType.ADMIN) setAdminPage();
        else setPage();

    }
    public void setAdminPage(){

    }
    public void setPage(){
        posts = Post.dummyList();


        adapter = new PostAdapter(posts);

        adapter.setOnClickListener( view -> {
        });//posssibly delete
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.sentView);
        lstUsers.setLayoutManager(layoutManager);
        lstUsers.setAdapter(adapter);
        //set up Rosebud and answer Blooming
        if(account != null && account.type == UserType.INFLUENCER){
            //set up post petal
        }
    }
    public static void logout(){
        account = null;
        //call log in
        //how to i terminate all other windows/activities and let the app start from fresh?
    }

    public void showMenu(View view) {
        PopupMenu popupMenu= new PopupMenu(MainActivity.this,findViewById(R.id.mainMenuBtn));
        //if(account.type == UserType.REGULAR){
        popupMenu.getMenuInflater().inflate(R.menu.reg_main_menu, popupMenu.getMenu());
        /*}
        else if(account.type == UserType.INFLUENCER){
        popupMenu.getMenuInflater().inflate(R.menu.influencer_main_menu, popupMenu.getMenu());
        }
        else if(account.type == UserType.ADMIN){
            popupMenu.getMenuInflater().inflate(R.menu.admin_main_menu, popupMenu.getMenu());
        }*/
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.friends) {
                startActivity(new Intent(MainActivity.this, ViewFriendActivity.class));
                return true;
            } else if (id == R.id.following) {
                startActivity(new Intent(MainActivity.this, ViewFollowingActivity.class));
                return true;
            } else if (id == R.id.GH) {
                //call view gratitude history
                return true;
            }else if (id == R.id.infApp) {
                //call influencer application
                return true;
            } else if (id == R.id.logout) {
                logout();
                //call log in
                return true;
            }

            return false;

        });
        popupMenu.show();
    }

    public void viewFollowing(View view) {
        //startActivity(new Intent(this,FollowingMainActivity.class));
        /*if(account.type == UserType.REGULAR)
            setContentView(R.layout.activity_following_main);
        else if(account.type == UserType.INFLUENCER)*/
        setContentView(R.layout.activity_following_main_influencer);

        posts = Post.dummyList();


        adapter = new PostAdapter(posts);

        adapter.setOnClickListener( v -> {
        });//posssibly delete
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.petalView);
        lstUsers.setLayoutManager(layoutManager);
        lstUsers.setAdapter(adapter);
        //set up Rosebud and answer Blooming
        if(account != null && account.type == UserType.INFLUENCER){
            //set up post petal
        }
    }

    public void viewFriends(View view) {

    }
}