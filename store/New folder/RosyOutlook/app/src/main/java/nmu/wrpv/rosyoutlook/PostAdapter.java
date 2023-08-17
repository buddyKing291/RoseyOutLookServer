package nmu.wrpv.rosyoutlook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private View.OnClickListener listener;
    private boolean regUser;
    public static class  PostViewHolder extends RecyclerView.ViewHolder{
        public Post post;
        public PostViewHolder(@NonNull View view){
            super(view);
        }
        public void setPost(Post post){
            this.post = post;
        }
    }
    private final List<Post> posts;
    public PostAdapter(List<Post> posts, View.OnClickListener listener){
        this.posts = posts;
        this.listener = listener;
    }
    public PostAdapter(List<Post> posts){this.posts = posts;}
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    public int getPos(Post post){return posts.indexOf(post);}
    public void updateUser(int pos,Post post){
        notifyItemChanged(pos);
    }
    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.post,
                        //.inflate(R.layout.recyclerview_person_simple_details,
                        parent, false);

        PostAdapter.PostViewHolder cvh = new PostAdapter.PostViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.setPost(post);
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
        return posts.size();
    }
    public void add(Post post){
        posts.add(post);
        notifyItemChanged(posts.size()-1);
    }
    public void remove(int position){
        posts.remove(position);
        notifyItemRemoved(position);
    }
}
