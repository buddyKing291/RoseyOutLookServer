package nmu.wrpv.rosyoutlook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
}