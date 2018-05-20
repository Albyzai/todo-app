package jonasmohrpedersen.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TutorialActivity extends AppCompatActivity {

    TextView mIGotIt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mIGotIt = findViewById(R.id.iGotIt);
        mIGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(TutorialActivity.this, TodoListActivity.class));
            }
        });
    }
}
