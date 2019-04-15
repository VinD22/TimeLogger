package project.timer.tracker.logger.time.v.app.timelogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import model.Task;

public class AddTask extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mTaskName, mTaskDescription;
    private Button mAddTask;

    int projectId = -1;
    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        // Initialize realm
        realm = Realm.getDefaultInstance();

        // get project id passed through intents.
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            projectId = extras.getInt("id");
            Toast.makeText(this, "Task id is " + projectId, Toast.LENGTH_SHORT).show();
        }

        // Fields : id, taskName, taskDescription, status, projectId;
        setupWidgets();

        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String taskName = mTaskName.getText().toString();
                String taskDescription = mTaskDescription.getText().toString();

                if (taskName.isEmpty() || taskDescription.isEmpty()) {
                    Toast.makeText(AddTask.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
                } else {
                    
                    realm.beginTransaction();

                    Task newTask = realm.createObject(Task.class);

                    int nextKey = getNextKey();
                    newTask.setId(nextKey);
                    newTask.setProjectId(projectId);
                    newTask.setStatus(false);
                    newTask.setTaskName(taskName);
                    newTask.setTaskDescription(taskDescription);

                    realm.commitTransaction();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", true);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();

                }

            }
        });

        
    }

    private void setupWidgets() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mTaskName = (EditText) findViewById(R.id.task_name);
        mTaskDescription = (EditText) findViewById(R.id.task_description);
        mAddTask = (Button) findViewById(R.id.add_task);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.add_task);
            getSupportActionBar().setTitle(R.string.add_task);
            // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    public int getNextKey() {
        try {
            return realm.where(Task.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();

    }
}
