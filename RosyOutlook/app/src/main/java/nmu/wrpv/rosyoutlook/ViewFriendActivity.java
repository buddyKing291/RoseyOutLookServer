package nmu.wrpv.rosyoutlook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
        users = User.dummyUsers();


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
        popupMenu.getMenuInflater().inflate(R.menu.reg_following_menu, popupMenu.getMenu());
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
        sent = User.dummyUsers();


        sentAdapter = new UserSentAdapter(sent);
        sentAdapter.setUser(true);

        sentAdapter.setOnClickListener( view -> {
        });//posssibly delete
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView sentView = findViewById(R.id.sentView);
        sentView.setLayoutManager(layoutManager);
        sentView.setAdapter(sentAdapter);

        requests = User.dummyUsers();
        requestAdapter = new UserRequestAdapter(requests);
        requestAdapter.setUser(true);
        RecyclerView.LayoutManager rlayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView requestView = findViewById(R.id.requestsView);
        requestView.setLayoutManager(rlayoutManager);
        requestView.setAdapter(requestAdapter);
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