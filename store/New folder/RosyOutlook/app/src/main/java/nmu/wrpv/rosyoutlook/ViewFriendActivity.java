package nmu.wrpv.rosyoutlook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.List;

public class ViewFriendActivity extends AppCompatActivity {
    private List<User> users;
    private ViewUserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        users = User.dummyUsers();


        adapter = new ViewUserAdapter(users);
        adapter.setUser(true);

        adapter.setOnClickListener( view -> {
        });//posssibly delete
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.bloomingView);
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
}