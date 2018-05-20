package jonasmohrpedersen.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.thebluealliance.spectrum.SpectrumDialog;

public class TodoActivity extends AppCompatActivity {


    //Local variables
    private Boolean mIsExistingTodo = false;
    private Boolean mEditModeEnabled = false;
    private String mUid;
    private String mTitle;
    private String mDescription;
    private int mBackgroundColor = -14575885; //Default background color
    private int mTextColor = -1; // Default text color

    //View variables
    private Button btnSave;
    private ConstraintLayout mTodoLayout;
    private ImageButton textColorBtn, bgColorBtn;
    private EditText etTitle, etDescription;
    private TextView tvTitle, tvDescription;
    private ViewSwitcher vsTitle, vsDescription;
    private RelativeLayout buttonWrapper;

    //Helpers
    private FirebaseHelper fbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        //Initializes UI Components
        initializeUI();

        //Sets onClickListeners
        setOnClickListeners();

        //Check if we opened an existing Todo & retrieves data
        checkIfExistingTodo();

        //Updates all the views
        upDateViews();
    }

    /**
     * Checks whether activity contains an existing Todo or a new one.
     * Retrieves using intent if Todo is NOT new, and updates views
     */
    private void checkIfExistingTodo(){

        //Check if we opened an existing Todo
        if(getIntent().hasExtra("uid")){

            //Retrieve intent data
            mUid = getIntent().getStringExtra("uid");
            mTitle = getIntent().getStringExtra("title");
            mDescription = getIntent().getStringExtra("description");
            mBackgroundColor = getIntent().getIntExtra("color", -14575885);
            mTextColor = getIntent().getIntExtra("textcolor", -1);

            //Hide views meant for EditMode
            buttonWrapper.setVisibility(View.INVISIBLE);
            btnSave.setVisibility(View.INVISIBLE);
            mIsExistingTodo = true;
        } else {
            vsTitle.showNext();
            vsDescription.showNext();
        }
    }

    /**
     * Updates all views regardless of if the Todo is new or not
     */
    private void upDateViews(){
        //Set attributes of views
        mTodoLayout.setBackgroundColor(mBackgroundColor);
        etTitle.setText(mTitle);
        etTitle.setTextColor(mTextColor);
        tvTitle.setText(mTitle);
        tvTitle.setTextColor(mTextColor);
        etDescription.setText(mDescription);
        etDescription.setTextColor(mTextColor);
        if(mDescription.isEmpty()){
            tvDescription.setText(R.string.todo_tv_description);
        }

        tvDescription.setTextColor(mTextColor);

        //Update toolbarmenu to fit options in TodoActivty
        invalidateOptionsMenu();
    }

    /**
     * Sets onClickListeners for anything that might need it in TodoActivity
     */
    private void setOnClickListeners(){

        //Set onClickListeners
        textColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorpaletteText();
            }
        });
        bgColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorpaletteBackground();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTodo(true);
            }
        });

    }

    /**
     * Initializes all Views
     */
    private void initializeUI(){

        //Initializes FirebaseHelper
        fbHelper = FirebaseHelper.getInstance();

        //Initializes views
        etTitle = findViewById(R.id.etTitle);
        btnSave = findViewById(R.id.btnSaveTodo);
        mTodoLayout = findViewById(R.id.todoLayout);
        buttonWrapper = findViewById(R.id.buttonWrapper);
        vsTitle = findViewById(R.id.vsTitle);
        vsDescription = findViewById(R.id.vsDescription);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        textColorBtn = findViewById(R.id.textColorBtn);
        bgColorBtn = findViewById(R.id.bgColorBtn);
        etDescription = findViewById(R.id.etDescription);

    }

    /**
     * Toggles whether the Todo is in EditMode or not.
     * Updates the views depending on the mode
     */
    private void toggleEditMode(){

        //Check if EditMode is enabled && toggle the views
        if (mEditModeEnabled){
            btnSave.setVisibility(View.INVISIBLE);
            buttonWrapper.setVisibility(View.INVISIBLE);
            vsTitle.showNext();
            vsDescription.showNext();
            mEditModeEnabled = false;
        } else {
            btnSave.setVisibility(View.VISIBLE);
            buttonWrapper.setVisibility(View.VISIBLE);
            vsTitle.showPrevious();
            vsDescription.showPrevious();
            mEditModeEnabled = true;
        }

        //Update toolbar according to EditMode
        invalidateOptionsMenu();

    }

    /**
     * Configures the toolbar menu
     * @param   menu  passed down from super
     * @return  menu  Menu that will be displayed in TodoActivity
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //Clear the menu
        menu.clear();

        //Check if an existing todo is opened, and whether it is in EditMode
        //Update menu accordingly
        if(mIsExistingTodo){
            if(mEditModeEnabled){
                getMenuInflater().inflate(R.menu.menu_todo_existing_note_editing, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_todo_existing_note, menu);
            }

        } else {
            getMenuInflater().inflate(R.menu.menu_todo_new_note, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handles clicks on items in the toolbar menu, and
     * executes actions accordingly
     * @param   item    The item which is clicked in the menu
     * @return  boolean Executes some logic depending on the item clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Sets actions for menu item clicks
        switch (item.getItemId()){
            case R.id.action_complete:
                fbHelper.moveToCompleted(mUid);
                finish();
                startActivity(new Intent(TodoActivity.this, TodoListActivity.class));
                return true;

            case R.id.action_edit:
                toggleEditMode();
                return true;

            case R.id.action_delete:
                fbHelper.deleteTodo(mUid, false);
                finish();
                startActivity(new Intent(TodoActivity.this, TodoListActivity.class));
                return true;

            case R.id.action_save:
                if(!etTitle.getText().toString().isEmpty()) {
                    toggleEditMode();
                }
                saveTodo(false);
                return true;

            case R.id.action_logout:
                fbHelper.logout(this);
                finish();
                startActivity(new Intent(TodoActivity.this, LoginActivity.class));
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Displays color palette for background colors, lets user change
     * background color of Todo
     */
    private void showColorpaletteBackground(){
        new SpectrumDialog.Builder(getApplicationContext())
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(false)
                .setOutlineWidth(2)
                .setTitle("Select background color")
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, int color) {
                        if(positiveResult){
                            Toast.makeText(getApplicationContext(), "Color selected #" + color, Toast.LENGTH_SHORT).show();
                           // mColor = (String) color;
                            mBackgroundColor = color;
                            mTodoLayout.setBackgroundColor(color);
                            bgColorBtn.setColorFilter(color);
                        } else {
                            Toast.makeText(getApplicationContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getSupportFragmentManager(), "color pallette bg");
    }

    /**
     * Display color palette for text colors, lets user change
     * text color of Todo
     */
    private void showColorpaletteText(){
        new SpectrumDialog.Builder(getApplicationContext())
                .setColors(R.array.text_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(false)
                .setOutlineWidth(2)
                .setTitle("Select text color")
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, int color) {
                        if(positiveResult){
                            Toast.makeText(getApplicationContext(), "Color selected #" + color, Toast.LENGTH_SHORT).show();
                            mTextColor = color;
                            etTitle.setTextColor(color);
                            tvTitle.setTextColor(color);
                            etDescription.setTextColor(color);
                            tvDescription.setTextColor(color);

                        } else {
                            Toast.makeText(getApplicationContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getSupportFragmentManager(), "color pallette text");
    }

    /**
     * Exits and saves the Todo when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Saves Todo and stops activity
        saveTodo(true);
        finish();
    }

    /**
     * Saves or updates the Todo, depending on whether it is a new or
     * existing Todo. Does not allow user to save or update if title is null
     * @param bigSaveButtonClicked  A boolean which specifies whether the function call comes from the big save button (btnSave)
     */
    private void saveTodo(Boolean bigSaveButtonClicked) {

        //Get values from EditTexts
        mTitle = etTitle.getText().toString();
        mDescription = etDescription.getText().toString();

        //Stops saving if title is empty
        if (mTitle.isEmpty()){
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        //Create the Todo object
        Todo todo = new Todo(mTitle, mDescription, mBackgroundColor, mTextColor);

        //Check if we are updating or creating a Todo, and do it
        if(mIsExistingTodo){
            fbHelper.updateTodo(mUid, mTitle, mDescription, mBackgroundColor, mTextColor);
        } else {
            fbHelper.saveTodo(todo);
        }

        //Check if saveTodo() is called by the bit button, big sends user back to list, small lets user stay in activity
        if(bigSaveButtonClicked) {
            startActivity(new Intent(TodoActivity.this, TodoListActivity.class));
        } else {
            Toast.makeText(this, "Todo saved!", Toast.LENGTH_SHORT).show();
            tvTitle.setText(mTitle);
            tvDescription.setText(mDescription);
            if(mDescription.isEmpty()){
                tvDescription.setText(R.string.todo_tv_description);
            }
        }
    }
}
