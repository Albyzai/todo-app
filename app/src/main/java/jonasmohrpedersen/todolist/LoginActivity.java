package jonasmohrpedersen.todolist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGoogleLoginBtn;
    private TextView mTvGoogleLogout;
    private ProgressBar pBar;
    private FirebaseAuth mAuth;
    private FirebaseHelper mFbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFbHelper = FirebaseHelper.getInstance();
        checkIfLoggedIn(); //New activity here, if user is logged in

        //Initialize & modify the UI
        initializeUI();

        //Set onClickListeners
        setOnCLickListeners();

        //Initialize Google API Client
        mGoogleApiClient = mFbHelper.setupGoogleLogin(this);
    }

    /**
     * Initializes & modifies all Views
     */
    private void initializeUI(){

        //Set contentView & FirebaseAuth for login
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        //Initialize views
        mGoogleLoginBtn = findViewById(R.id.sign_in_button);
        mTvGoogleLogout = findViewById(R.id.googleSignOutLink);
        pBar = findViewById(R.id.pBar);

        //Modify views
        getSupportActionBar().hide();
        mTvGoogleLogout.setVisibility(View.VISIBLE);
        pBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets onClickListeners for LoginActivity
     */
    private void setOnCLickListeners(){

        //Set onClickListener of google login button
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLogin();
            }
        });

        //Set onClickListener of google text to sign out completely
        mTvGoogleLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLogoutCompletely();
            }
        });

    }

    /**
     * Checks if a user is already logged in. If user is already logged in,
     * LoginActivity gets terminated, and TodoListActivity starts
     */
    private void checkIfLoggedIn(){
        if(mFbHelper.isUserLoggedIn()){
            startActivity(new Intent(LoginActivity.this, TodoListActivity.class));
            finish();
        }
    }

    /**
     * Sends signInIntent with Google API Client in StartactivityForResult
     * and hides login buttons from the view
     */
    private void googleLogin(){

        mFbHelper.googleLogin(this);
        pBar.setVisibility(View.VISIBLE);
        mGoogleLoginBtn.setVisibility(View.GONE);
        mTvGoogleLogout.setVisibility(View.GONE);

    }

    /**
     * Logs the google account out completely, so that
     * the user may choose to use the app with another google account
     */
    private void googleLogoutCompletely(){

        mFbHelper.googleLogoutCompletely(mGoogleApiClient);
        mTvGoogleLogout.setVisibility(View.GONE);

    }

    /**
     * Triggered when the google intent sent with the startActivityForResult
     * returns a result. Confirms that request codes are identical, and then
     * tries to authenticate the user if there is no ApiException
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == mFbHelper.RC_SIGN_IN) {

            com.google.android.gms.tasks.Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                mGoogleLoginBtn.setVisibility(View.VISIBLE);
                pBar.setVisibility(View.GONE);
                Log.w("signin failed", "Google sign in failed", e);
                // ...
            }
        }
    }

    /**
     * Attempts to sign in the user
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, set user data and start new activity
                            Log.d("success", "signInWithCredential:success");
                            mFbHelper.setUserData();
                            finish();
                            startActivity(new Intent(LoginActivity.this, TodoListActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            mGoogleLoginBtn.setVisibility(View.VISIBLE);
                            pBar.setVisibility(View.GONE);
                            Log.w("failure", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Failed login", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
