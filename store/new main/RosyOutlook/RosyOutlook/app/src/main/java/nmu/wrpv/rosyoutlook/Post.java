package nmu.wrpv.rosyoutlook;


import java.util.ArrayList;
import java.util.List;

public class Post {
    public String content;
    public String username;
    public int icon;
    public String id;
    public PostType type;
    public Post(){}
    public Post(String username,String content){
        this.username = username;
        this.content = content;
    }
    public static List<Post> dummyList(){
        List<Post> posts = new ArrayList<>();
        posts.add(new Post("Junaid","PDE's suck so much"));
        posts.add(new Post("Ganeefa","Boots is adorable"));
        posts.add(new Post("MathICool","I am super smart"));
        posts.add(new Post("Wow","Life is so beautiful"));
        posts.add(new Post("Gor","I just ate some good food"));
        posts.add(new Post("Foodlover","Good food is the way to my heart"));
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post());
        posts.add(new Post("tesing","final"));
        return posts;
    }
}
