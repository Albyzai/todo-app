package jonasmohrpedersen.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class TodoListActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    //Local view variables
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;

    //Local variables
    private FirebaseHelper mFbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get reference to FirebaseHelper (used for Firebase interactions)
        mFbHelper = FirebaseHelper.getInstance();

        //Sets up the UI Components
        initializeUI();
    }

    /**
     * Configures the toolbar menu
     * @param   menu  passed down from super
     * @return  menu  Menu that will be displayed in TodoActivity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo_list, menu);
        return true;
    }

    /**
     * Handles clicks on items in the toolbar menu, and
     * executes actions accordingly
     * @param   item    The item which is clicked in the menu
     * @return  boolean Executes some logic depending on the item clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_completed_list:
                startActivity(new Intent(TodoListActivity.this, CompletedTodoListActivity.class));
                //showHelp();
                return true;
            case R.id.action_logout:
                mFbHelper.logout(this);
                finish();
                startActivity(new Intent(TodoListActivity.this, LoginActivity.class));

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    /**
     * Sets the logic for swiping a viewholder
     * @param viewHolder    The viewholder which was swiped
     * @param direction     The direction in which the viewholder was swiped
     * @param position      The viewholder's position in the Recyclerview
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        final String uid = mAdapter.getRef(position).getKey();

        switch (direction) {
            case ItemTouchHelper.LEFT:
                Toast.makeText(viewHolder.itemView.getContext(), "Todo is deleted", Toast.LENGTH_SHORT).show();
                mFbHelper.deleteTodo(uid, false);
                return;

            case ItemTouchHelper.RIGHT:
                Toast.makeText(viewHolder.itemView.getContext(), "Todo is completed", Toast.LENGTH_SHORT).show();
                mFbHelper.moveToCompleted(uid);
                return;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        mFbHelper.logout(this);
    }

    /**
     * Initializes the views, sets onClickListeners and adapters
     */
    private void initializeUI() {

        //Set layout for activity
        setContentView(R.layout.activity_todo_list);

        //Initialize toolbarmAdapter.getRef(position).getKey();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize "+" button & set event listener
        ImageButton mAddButton = findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TodoListActivity.this, TodoActivity.class));
            }
        });

        //Initialize RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);

        //Set layout manager for RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback swipeHelper = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(swipeHelper).attachToRecyclerView(mRecyclerView);

        //Create adapter for recyclerview
        mAdapter = mFbHelper.createRecyclerAdapter(false);

        //Connect adapter to recyclerview
        mRecyclerView.setAdapter(mAdapter);
    }


}
