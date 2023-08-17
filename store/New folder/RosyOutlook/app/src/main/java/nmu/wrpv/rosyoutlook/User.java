package nmu.wrpv.rosyoutlook;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String name;
    public String ID;
    public String username;
    public int icon;
    public UserRelationship relation;
    public UserType type;
    public User(String name, String username){
        this.name = name;
        this.username = username;
    }
    public User(String name, String username,UserRelationship relation){
        this.name = name;
        this.username = username;
        this.relation = relation;
    }
    public static List<User> dummyUsers(){
        List<User> users = new ArrayList<>();
        users.add(new User("JJ","jpg"));
        users.add(new User("Jacks","bagels"));
        users.add(new User("donut","rings"));
        users.add(new User("drum","sticks"));
        users.add(new User("ice","cream"));
        users.add(new User("roller","blades"));
        users.add(new User("low","low low"));
        users.add(new User("thats","what"));
        users.add(new User("she","said"));
        users.add(new User("im","running"));
        users.add(new User("out","of"));
        users.add(new User("random","words"));
        users.add(new User("to","type"));
        return users;
    }
    public static List<User> dummySearch(){
        List<User> users = new ArrayList<>();
        users.add(new User("JJ","jpg",UserRelationship.NONE));
        users.add(new User("Jacks","bagels",UserRelationship.FRIEND));
        users.add(new User("donut","rings",UserRelationship.PENDING));
        users.add(new User("drum","sticks",UserRelationship.NONE));
        users.add(new User("ice","cream",UserRelationship.FRIEND));
        users.add(new User("roller","blades",UserRelationship.FRIEND));
        users.add(new User("low","low low",UserRelationship.FOLLOWING));
        users.add(new User("thats","what",UserRelationship.FRIEND));
        users.add(new User("she","said",UserRelationship.NONE));
        users.add(new User("im","running",UserRelationship.NONE));
        users.add(new User("out","of",UserRelationship.FOLLOWING));
        users.add(new User("random","words",UserRelationship.NONE));
        users.add(new User("to","type",UserRelationship.NONE));
        return users;
    }
}
