package nmu.wrpv.rosyoutlook;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
                    status.setText(user.type == UserType.REGULAR?"add":"follow");//then set the add or follow sql code
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
