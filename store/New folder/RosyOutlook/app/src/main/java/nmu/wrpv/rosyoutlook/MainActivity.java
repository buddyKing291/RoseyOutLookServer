package nmu.wrpv.rosyoutlook;

import androidx.appcompat.app.AppCompatActivity;
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
        }
        setContentView(R.layout.activity_main);
        posts = Post.dummyList();


        adapter = new PostAdapter(posts);

        adapter.setOnClickListener( view -> {
        });//posssibly delete
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.bloomingView);
        lstUsers.setLayoutManager(layoutManager);
        lstUsers.setAdapter(adapter);
    }
    public void logout(){
        account = null;
        //call log in
    }

    public void startFriends(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }
}