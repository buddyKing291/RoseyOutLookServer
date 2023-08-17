package nmu.wrpv.rosyoutlook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class ViewFollowingActivity extends AppCompatActivity {
    private List<User> users;
    private ViewUserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_following);
        users = User.dummyUsers();


        adapter = new ViewUserAdapter(users);
        adapter.setUser(true);

        adapter.setOnClickListener( view -> {
        });//posssibly delete
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.followingList);
        lstUsers.setLayoutManager(layoutManager);
        lstUsers.setAdapter(adapter);
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
}