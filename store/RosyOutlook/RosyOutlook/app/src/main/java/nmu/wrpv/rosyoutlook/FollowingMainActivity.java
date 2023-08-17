package nmu.wrpv.rosyoutlook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class FollowingMainActivity extends AppCompatActivity {
    //think about just changing content in the main and deleting this page entirely

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_main);
    }

    public void viewFriends(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }
}