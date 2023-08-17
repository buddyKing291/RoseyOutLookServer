package nmu.wrpv.rosyoutlook;

import java.util.ArrayList;
import java.util.List;

public class Post {
    public String content;
    public String username;
    public int icon;
    public String id;
    public static List<Post> dummyList(){
        List<Post> posts = new ArrayList<>();
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        return posts;
    }
}
