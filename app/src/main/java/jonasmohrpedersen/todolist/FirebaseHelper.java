package jonasmohrpedersen.todolist;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    //Local variables
    private String mUserId;

    //Local misc variables
    private DatabaseReference mRefActive, mRefCompleted, mRefUsers;
    private FirebaseRecyclerAdapter mAdapter;

    //Firebase Login
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    //Public constants
    public static final int RC_SIGN_IN = 1;


    private static final FirebaseHelper ourInstance = new FirebaseHelper();

    public static FirebaseHelper getInstance() {
        return ourInstance;
    }

    /**
     * Singleton class used to provide support for interactions with the Firebase Real Time Database
     * in any class that might need it.
     */
    private FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mRefUsers = FirebaseDatabase.getInstance().getReference().child("users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = mAuth.getCurrentUser();
                if(mUser != null) {
                    setUserData();
                    saveUser();
                }
            }
        };

        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Moves an object from active to completed in the Firebase Database
     * @param uid   A unique ID used to identify the entry in the database
     *
     */
    public void moveToCompleted(final String uid) {
        mRefActive.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mRefCompleted.push().setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                        } else {
                            mRefActive.child(uid).removeValue();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    /**
     * Pushes an object to the Firebase Database, generating a uid which the object
     * can be identified by
     * @param obj   The object which is pushed to the database
     */
    public void saveTodo(Object obj){
        mRefActive.push().setValue(obj);
    }

    /**
     * Updates an entry in the Firebase database with the data provided in the function parameters
     * @param uid               A unique ID used to identify the entry in the database
     * @param title             The title which will overwrite the existing title of the database entry
     * @param description       The description which will overwrite the existing description of the database entry
     * @param backgroundColor   The background color which will overwrite the existing background color of the database entry
     * @param textColor         The text color which will overwrite the existing background of the database entry
     */
    public void updateTodo(String uid, String title, String description, int backgroundColor, int textColor){
        DatabaseReference mTodoRef = mRefActive.child(uid);
        mTodoRef.child("title").setValue(title);
        mTodoRef.child("description").setValue(description);
        mTodoRef.child("color").setValue(backgroundColor);
        mTodoRef.child("textcolor").setValue(textColor);
    }

    /**
     * Deletes an entry in the database, boolean parameter determines from where in the database
     * the entry shall be deleted
     * @param uid           A unique ID used to identify the entry in the database
     * @param isCompleted   A boolean used to figure out if it should delete the entry from the active or completed list
     */
    public void deleteTodo(final String uid, boolean isCompleted){
        if(!isCompleted){
            mRefActive.child(uid).removeValue();
        } else {
            mRefCompleted.child(uid).removeValue();
        }
    }

    /**
     * Saves the currently authenticated user to the Firebase Database
     */
    public void saveUser(){
        FirebaseUser fbUser = mAuth.getCurrentUser();
        String email = fbUser.getEmail();
        String name = fbUser.getDisplayName();
        String provider = fbUser.getProviders().get(0);
        String phone = fbUser.getPhoneNumber();
        String photoURL = fbUser.getPhotoUrl().toString();

        User user = new User(email, name, provider, phone, photoURL);

        mRefUsers.child(mUserId).setValue(user);
    }

    /**
     * Returns an adapter used to populate a RecyclerView with data from a Firebase Real Time Database
     * @return FirebaseRecyclerAdapter which is used to populate a recyclerview
     */
    public FirebaseRecyclerAdapter<Todo, TodoHolder> createRecyclerAdapter(final boolean isCompleted){

        DatabaseReference query;
        if(isCompleted){
            query = mRefCompleted;
        } else {
            query = mRefActive;
        }

        FirebaseRecyclerOptions<Todo> options =
                new FirebaseRecyclerOptions.Builder<Todo>()
                        .setQuery(query, Todo.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<Todo, TodoHolder>(options) {
            @Override
            public TodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                if(isCompleted){
                    return new TodoHolder(view, true);
                } else {
                    return new TodoHolder(view, false);
                }
            }

            @Override
            protected void onBindViewHolder(TodoHolder holder, int position, Todo todo) {
                // Bind the Chat object to the ChatHolder
                String uid = mAdapter.getRef(position).getKey();
                todo.setUid(uid);
                holder.bind(todo);
            }
        };

        return mAdapter;
    }

    /**
     * Logs the user out, closes the current activity and sends the user back to Login activity
     * @param activity  The activity in which the function is called from
     */
    public void logout(Activity activity){
        activity.startActivity(new Intent(activity.getApplicationContext(), LoginActivity.class));
        activity.finish();
        mAuth.signOut();
    }

    /**
     * Sets user data in the FirebaseHelper. The data is used to access the currently authenticated users data
     */
    public void setUserData(){
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();
        mRefActive = FirebaseDatabase.getInstance().getReference().child("todos/" + mUserId + "/active");
        mRefCompleted = FirebaseDatabase.getInstance().getReference().child("todos/" + mUserId + "/completed");
    }

    /**
     * Creates a GoogleApiClient which is used to authenticate an user with a google account
     * @param activity  The activity responsible for logging in the user
     * @return
     */
    public GoogleApiClient setupGoogleLogin(final FragmentActivity activity){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(activity, "You got an error", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return mGoogleApiClient;
    }

    /**
     * Attempts to log in the user with google by sending a Google API sign in intent
     * in a startActivityForResult.
     * @param activity  The activity in which the call is made
     */
    public void googleLogin(Activity activity){

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    /**
     * Logs out the user completely from google, allowing the user to
     * change the google account for the app
     * @param api   GoogleApiClient used for authenticating google accounts
     */
    public void googleLogoutCompletely(GoogleApiClient api){
        Auth.GoogleSignInApi.signOut(api).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }


    /**
     * Checks if any user is currently authenticated. Used to determine if login activity
     * should be skipped
     * @return  true if user is logged in, false if not.
     */
    public boolean isUserLoggedIn(){
        if(mUser != null){
            setUserData();
            return true;
        }
        return false;
    }





}
