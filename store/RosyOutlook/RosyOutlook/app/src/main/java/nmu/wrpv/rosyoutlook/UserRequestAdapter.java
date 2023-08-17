package nmu.wrpv.rosyoutlook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.RequestUserViewHolder>{
    private View.OnClickListener listener;
    private boolean regUser;
    public static class  RequestUserViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView username;
        public User user;
        public RequestUserViewHolder(@NonNull View view){
            super(view);
            name = view.findViewById(R.id.nameFR);
            username = view.findViewById(R.id.usernameFR);
        }
        public void setUser(User user){
            this.user = user;
            name.setText(user.name);
            username.setText(user.username);

        }
    }
    private final List<User> users;
    public UserRequestAdapter(List<User> users, View.OnClickListener listener){
        this.users = users;
        this.listener = listener;
    }
    public UserRequestAdapter(List<User> users){this.users = users;}
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
    public RequestUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.friend_request_card,
                        //.inflate(R.layout.recyclerview_person_simple_details,
                        parent, false);

        RequestUserViewHolder cvh = new RequestUserViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestUserViewHolder holder, int position) {
        User user = users.get(position);
        holder.setUser(user);
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
