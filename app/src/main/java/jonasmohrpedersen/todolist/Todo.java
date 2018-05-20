package jonasmohrpedersen.todolist;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Todo {

    private String title;
    private String description;
    private String uid;
    private int color;
    private int textColor;

    public Todo(){
        // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    }

    public Todo(String title){
        this.title = title;
    }

    public Todo(String title, String description){
        this.title = title;
        this.description = description;
    }

    public Todo(String title, String description, int color, int textColor) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.textColor = textColor;
    }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public int getColor() {
        return color;
    }

    public int getTextColor() { return textColor; }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid){this.uid = uid; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("description", description);
        result.put("color", color);

        return result;
    }

}
