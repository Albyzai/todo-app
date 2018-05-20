package jonasmohrpedersen.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class CompletedTodoListActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{


    //Local view variables
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;

    //Local variables
    private FirebaseHelper mFbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set reference to FirebaseHelper
        mFbHelper = FirebaseHelper.getInstance();

        //Sets up the UI Components
        initializeUI();
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

    private void initializeUI() {

        //Set layout for activity
        setContentView(R.layout.activity_completed_todo_list);


        //Initialize RecyclerView
        mRecyclerView = findViewById(R.id.completedRecyclerView);

        //Set layout manager for RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Initialize adapter for recyclerview
        mAdapter = mFbHelper.createRecyclerAdapter(true);

        ItemTouchHelper.SimpleCallback swipeHelper = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(swipeHelper).attachToRecyclerView(mRecyclerView);



        //Connect adapter to recyclerview
        mRecyclerView.setAdapter(mAdapter);
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
                mFbHelper.deleteTodo(uid, true);
                return;

            default: return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(CompletedTodoListActivity.this, TodoListActivity.class));
    }
}
