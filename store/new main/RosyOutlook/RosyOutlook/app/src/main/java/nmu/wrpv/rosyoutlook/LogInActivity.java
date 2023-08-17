package nmu.wrpv.rosyoutlook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.method.BaseKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import static nmu.wrpv.rosyoutlook.MainActivity.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {
    TextView error;
    EditText username;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        error = findViewById(R.id.logInError);
        username = findViewById(R.id.logInUsername);
        password = findViewById(R.id.logInPassword);
        /*password.setOnEditorActionListener((v,id,event)->{
            if(event.getKeyCode()== KeyEvent.KEYCODE_ENTER)
                logIN();
            return false;
        });*/

        /*password.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                logIN();
            }
            return true;
        });*/
    }


    public void logIn(View view) {
        logIN();
    }
    public void logIN(){
        //query database to check
        //error.setText(username.getText()+"  "+password.getText());
        if(username.getText() == null && password.getText() == null) return;
        boolean hasPermission =//come back to delet
                checkSelfPermission(Manifest.permission.INTERNET) ==
                        PackageManager.PERMISSION_GRANTED;
        System.out.println("Internet permission = " + hasPermission);
        String usernameS = "o";
        String passwordS = "o";
        String type = "o";
        Thread login = new Thread(()->{
            Socket client;
            OutputStream OS;
            ObjectOutputStream OOS;
            InputStream IS;
            ObjectInputStream OIS;

            //ActivityCompat.requestPermissions(this, "Manifest.permission.",1);
            try {
                // Step 1: Create a Socket to make connection.
                //client = new Socket("10.202.36.38", 500);//campus stuff
                client = new Socket("192.168.0.196", 500);//junaid home
                //client = new Socket("10.202.36.38", 500);

                // Step 2: Get the input and output streams.
                OS = new DataOutputStream(client.getOutputStream());
                OOS = new ObjectOutputStream(OS);
                Map<String,Map<String,String>> map = new HashMap<>();
                Map<String,String> details = new HashMap<>();
                details.put("username",username.getText().toString());
                details.put("password",password.getText().toString());
                map.put("login",details);
                OOS.writeObject(map);
                IS = new DataInputStream(client.getInputStream());
                OIS = new ObjectInputStream(IS);
                Map<String, String> received = (Map) OIS.readObject();
                client.close();
                /*if(received == null) {
                    password.setText("");
                    error.setText("Internal Error please try again");
                    error.setTextColor(Color.RED);
                    return;
                }*/
                if(received.get("error") != null ){
                    if(received.get("error").equals("match")) {
                        error.setText("Username and password do not match");
                        error.setTextColor(Color.RED);
                        LogInActivity.this.runOnUiThread(() ->password.getText().clear());
                    }
                    else if(received.get("error").equals("user")){
                        error.setText("Username does not exist");
                        error.setTextColor(Color.RED);
                        LogInActivity.this.runOnUiThread(() ->password.getText().clear());
                    }
                    return;
                }
                String img = received.get("image");
                account = new User(received.get("name"),received.get("username"),received.get("type").equals("ADMIN")? UserType.ADMIN:received.get("type").equals("PUBLIC")?UserType.REGULAR:UserType.INFLUENCER);
                if (img != null) account.setImage(textToDrawable(img));


            } catch (IOException e) {
                error.setText("Internal Error please try again");
                Log.e("error", e.getMessage());
            } catch (ClassNotFoundException e) {
                error.setText("Internal Error please try again");
            }
            if(account!=null) {
                getFriendAndFollowing();
                finish();
            }
        });
        login.start();

        //if(usernameS==null && passwordS==null) return;


        /*//else{
        password.setText("");
        error.setText("Username and password do not match");
        error.setTextColor(Color.RED);*/
        //}
    }
    public void getFriendAndFollowing(){
        Thread friends = new Thread(()->{
            Socket client;
            OutputStream OS;
            ObjectOutputStream OOS;
            InputStream IS;
            ObjectInputStream OIS;

            try {
                // Step 1: Create a Socket to make connection.
                client = new Socket("192.168.0.196", 500);

                // Step 2: Get the input and output streams.
                IS = new DataInputStream(client.getInputStream());
                OIS = new ObjectInputStream(IS);
                OS = new DataOutputStream(client.getOutputStream());
                OOS = new ObjectOutputStream(OS);
                Map<String,Map<String,String>> map = new HashMap<>();
                map.put("friend",null);
                OOS.writeObject(map);
                Map<String, String> received = (Map) OIS.readObject();
                List<User> friendsList = new ArrayList<>();
                for(int i = 0; i<received.size()/3;i++){
                    friendsList.add(new User(received.get("username"+i),received.get("name"+i),UserRelationship.FRIEND));
                    //friendsList.add(new User(received.get("username"+i),received.get("name"+i),UserRelationship.FRIEND,received.get("date"+i)));
                }
                account.setFriends(friendsList);
                client.close();
            } catch (IOException e) {
                error.setText("Internal Error please try again");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        //friends.start();
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
                IS = new DataInputStream(client.getInputStream());
                OIS = new ObjectInputStream(IS);
                OS = new DataOutputStream(client.getOutputStream());
                OOS = new ObjectOutputStream(OS);
                Map<String,Map<String,String>> map = new HashMap<>();
                map.put("following",null);
                OOS.writeObject(map);
                Map<String, String> received = (Map) OIS.readObject();
                List<User> followingList = new ArrayList<>();
                for(int i = 0; i<received.size()/3;i++){
                    followingList.add(new User(received.get("username"+i),received.get("name"+i),UserRelationship.FRIEND,received.get("date"+i)));
                }
                account.setFollowing(followingList);
                client.close();
            } catch (IOException e) {
                error.setText("Internal Error please try again");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        //following.start();            //set account
    }
    public BitmapDrawable textToDrawable(String text) {
        Context context = this; // Obtain the context, for example: getContext() in an Activity or Fragment
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(Color.BLACK); // Set the text color

        // Set layout parameters for the TextView (adjust as needed)
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);

        // Measure the TextView and layout its content
        textView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());

        // Create a Bitmap and a Canvas to draw the TextView onto
        Bitmap bitmap = Bitmap.createBitmap(
                textView.getMeasuredWidth(),
                textView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);

        // Draw the TextView on the Canvas
        textView.draw(canvas);

        // Create a BitmapDrawable from the Bitmap and return it
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public void registerAccount(View view) {
        //start registerAccount activity
        finish();
    }
}