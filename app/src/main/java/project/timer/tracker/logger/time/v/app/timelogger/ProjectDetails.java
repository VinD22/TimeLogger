package project.timer.tracker.logger.time.v.app.timelogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import model.Task;

/**
 * Activity to view details about the projects.
 */

public class ProjectDetails extends AppCompatActivity {

    private TextView mProjectTitle, mProjectHourlyRate, mProjectTime, mProjectIncome;
    private FloatingActionButton mAddTask, mAddTime;
    private RecyclerView mReclist;

    private ArrayList<Task> listOfTasks = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_details);

        mProjectTitle = (TextView) findViewById(R.id.project_title);
        mProjectHourlyRate = (TextView) findViewById(R.id.hourly_rate);
        mProjectTime = (TextView)  findViewById(R.id.project_time);
        mProjectIncome = (TextView) findViewById(R.id.project_income);

        mAddTask = (FloatingActionButton) findViewById(R.id.add_task);
        mAddTime = (FloatingActionButton) findViewById(R.id.add_time);

        mReclist = (RecyclerView) findViewById(R.id.list_of_tasks);

        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProjectDetails.this, AddTask.class);
                startActivity(intent);

            }
        });

        mAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listOfTasks.size() < 1) {
                    Toast.makeText(ProjectDetails.this, R.string.add_task_first, Toast.LENGTH_SHORT).show();
                } else {
                    // Go to Time Screen
                    Intent intent = new Intent(ProjectDetails.this, AddTimer.class);
                    startActivity(intent);
                }

            }
        });


        getTasks();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getTasks();
    }

    private void getTasks() {

        // Clear lists
        listOfTasks.clear();

        // TODO : get list of tasks.

    }

}