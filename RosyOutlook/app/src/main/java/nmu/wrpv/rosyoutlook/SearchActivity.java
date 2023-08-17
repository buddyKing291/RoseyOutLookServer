package nmu.wrpv.rosyoutlook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    char from;
    private List<User> users;
    private SearchAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EditText searchBar = findViewById(R.id.menubar);
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
        users = User.dummySearch();


        adapter = new SearchAdapter(users);
        //adapter.setUser(true);

        adapter.setOnClickListener( view -> {
        });//posssibly delete
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView lstUsers;
        lstUsers = findViewById(R.id.sentView);
        lstUsers.setLayoutManager(layoutManager);
        lstUsers.setAdapter(adapter);
        searchBar.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        Intent intent = getIntent();
        if(intent!= null){
            Bundle extra = intent.getExtras();
            if(extra!=null){
                from = extra.getChar("from");
                if(from=='0'){//search for regular users

                }
                else{//search for influencers

                }
            }
        }//remember for SQL exclude admin and account
    }
}