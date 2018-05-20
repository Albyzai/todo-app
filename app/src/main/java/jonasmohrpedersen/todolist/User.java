package jonasmohrpedersen.todolist;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private String email;
    private String name;
    private String provider;
    private String phoneNumber;
    private String photoURL;


    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email){
        this.email = email;
    }

    public User(String email, String name){
        this.email = email;
        this.name = name;
    }

    public User(String email, String name, String provider){
        this.email = email;
        this.name = name;
        this.provider = provider;
    }

    public User(String email, String name, String provider, String phoneNumber){
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.phoneNumber = phoneNumber;
    }

    public User(String email, String name, String provider, String phoneNumber, String photoURL){
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.phoneNumber = phoneNumber;
        this.photoURL = photoURL;
    }

    public String getEmail() { return email; }

    public String getName() { return name; }

    public String getProvider() { return provider; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getPhotoURL() { return photoURL; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("provider", provider);
        result.put("phoneNumber", phoneNumber);
        result.put("profilepicture", photoURL);

        return result;
    }



}
