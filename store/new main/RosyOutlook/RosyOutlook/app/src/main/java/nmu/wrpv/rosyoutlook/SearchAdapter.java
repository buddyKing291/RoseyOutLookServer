package nmu.wrpv.rosyoutlook;


import static nmu.wrpv.rosyoutlook.MainActivity.account;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
    private View.OnClickListener listener;
    private boolean regUser;
    public static class  SearchViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView username;
        public User user;
        public Button status;
        public SearchViewHolder(@NonNull View view){
            super(view);
            name = view.findViewById(R.id.nameS);
            username = view.findViewById(R.id.usernameS);
            status = view.findViewById(R.id.doBtnSC);
        }
        public void setUser(User user){
            this.user = user;
            name.setText(user.name);
            username.setText(user.username);
            switch(user.relation){
                case NONE:
                    boolean u = user.type == UserType.REGULAR;
                    String us = user.username;
                    UserType ut = user.type;
                    status.setText(user.type == UserType.REGULAR?"add":"follow");//then set the add or follow sql code
                    status.setOnClickListener(v->{
                        if(!(status.getText().toString().equals("add")||status.getText().toString().equals("follow")))return;
                        if(user.type == UserType.REGULAR){
                            Thread addFriend = new Thread(()-> {
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
                                    Map<String, Map<String, String>> map = new HashMap<>();
                                    Map<String, String> details = new HashMap<>();
                                    details.put("username1", account.username);
                                    details.put("username2", user.username);
                                    map.put("addfriend", details);
                                    OOS.writeObject(map);
                                    IS = new DataInputStream(client.getInputStream());
                                    OIS = new ObjectInputStream(IS);
                                    Map<String, String> received = (Map) OIS.readObject();
                                    client.close();
                                    if (received.containsKey("error")) {
                                        SearchActivity.toast("Internal error. Try again later");
                                        return;
                                    }

                                    status.setBackgroundResource(R.drawable.gray_btn);
                                    status.setText("pending");
                                    user.relation = UserRelationship.PENDING;
                                    SearchActivity.toast("Friend request sent");
                                    //status.setEnabled(false);
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            });
                            addFriend.start();

                        }
                        else if(user.type == UserType.INFLUENCER){
                            Thread following = new Thread(()->{
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
                                    Map<String, Map<String, String>> map = new HashMap<>();
                                    Map<String, String> details = new HashMap<>();
                                    details.put("username1", account.username);
                                    details.put("username2", user.username);
                                    map.put("follow", details);
                                    OOS.writeObject(map);
                                    IS = new DataInputStream(client.getInputStream());
                                    OIS = new ObjectInputStream(IS);
                                    Map<String, String> received = (Map) OIS.readObject();
                                    if (received.containsKey("error")) {
                                        //Toast.makeText(this.itemView.getContext(), "Internal error. Try again later", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    status.setText("following");
                                    status.setBackgroundResource(R.drawable.gray_btn);
                                    //status.setEnabled(false);
                                    user.relation = UserRelationship.FOLLOWING;
                                    SearchActivity.toast("Following");
                                    client.close();
                                } catch (Exception e) {
                                    SearchActivity.toast("Internal Error");
                                }
                            });
                            following.start();
                        }
                    });
                    break;
                case FRIEND:
                    status.setText("friend");
                    status.setBackgroundResource(R.drawable.gray_btn);
                    break;
                case PENDING:
                    status.setText("pending");
                    status.setBackgroundResource(R.drawable.gray_btn);
                    break;
                case FOLLOWING:
                    status.setText("following");
                    status.setBackgroundResource(R.drawable.gray_btn);
                    break;
            }
        }
       /* public void setMenu(View.OnClickListener listener){
            menu.setOnClickListener(listener);
        }*/
    }
    private final List<User> users;
    public SearchAdapter(List<User> users, View.OnClickListener listener){
        this.users = users;
        this.listener = listener;
    }
    public SearchAdapter(List<User> users){this.users = users;}
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    public int getPos(User user){return users.indexOf(user);}
    public void updateUser(int pos,User user){
        users.get(pos).name = user.name;
        users.get(pos).username = user.username;
        notifyItemChanged(pos);
    }
    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.search_card,
                        //.inflate(R.layout.recyclerview_person_simple_details,
                        parent, false);

        SearchAdapter.SearchViewHolder cvh = new SearchAdapter.SearchViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        User user = users.get(position);
        holder.setUser(user);
        /*holder.setMenu(v->{
            if(regUser){

            }
            else{

            }
        });*/
    }
    public void setUser(boolean regUser){this.regUser = regUser;}
    @Override
    public int getItemCount() {
        return users.size();
    }
    public void add(User user){
        users.add(user);
        notifyItemChanged(users.size()-1);
    }
    public void remove(int position){
        users.remove(position);
        notifyItemRemoved(position);
    }
}
