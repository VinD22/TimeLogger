package project.timer.tracker.logger.time.v.app.timelogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import adapter.TasksAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Project;
import model.Task;
import model.TimeLog;

/**
 * Activity to view details about the projects.
 */

public class ProjectDetails extends AppCompatActivity {

    private TextView mProjectTitle, mProjectHourlyRate, mProjectTime, mProjectIncome;
    private FloatingActionButton mAddTask, mAddTime;
    private RecyclerView mReclist;

    private TasksAdapter mAdapter;

    private ArrayList<Task> listOfTasks = new ArrayList<>();

    Realm realm;

    int projectId = -1;

    long totalMinutes = 0;
    double hourlyRate = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_details);

        setupWidgets();

        // Initialize realm
        realm = Realm.getDefaultInstance();

        // get project id passed through intents.
        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            Toast.makeText(this, "No id passed! Error! ", Toast.LENGTH_SHORT).show();
        } else {

            projectId = extras.getInt("id");
            // Toast.makeText(this, "Project id is " + projectId , Toast.LENGTH_SHORT).show();

            // Fetch Project Details from realm.
            getProjectDetails(projectId);

            // Fetch Tasks list.
            getTasksList(projectId);
            getTotalTimeSpent(projectId);

        }


        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProjectDetails.this, AddTask.class);
                intent.putExtra("id", projectId);
                startActivityForResult(intent, 1);

            }
        });

        mAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listOfTasks.size() < 1) {
                    Toast.makeText(ProjectDetails.this, R.string.add_task_first, Toast.LENGTH_SHORT).show();
                } else {
                    // Go to Time Screen
                    Intent intent = new Intent(ProjectDetails.this, AddTimer.class);
                    intent.putExtra("id", projectId);
                    startActivityForResult(intent, 2);
                }

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(ProjectDetails.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReclist.setLayoutManager(layoutManager);
        mReclist.setHasFixedSize(true);

        mReclist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mAddTask.hide();
                    mAddTime.hide();
                } else if (dy < 0) {
                    mAddTask.show();
                    mAddTask.show();
                }
            }
        });

    }

    private void setupWidgets() {

        mProjectTitle = (TextView) findViewById(R.id.project_name);
        mProjectHourlyRate = (TextView) findViewById(R.id.project_hourly_rate);
        mProjectTime = (TextView) findViewById(R.id.project_time);
        mProjectIncome = (TextView) findViewById(R.id.project_income);

        mAddTask = (FloatingActionButton) findViewById(R.id.add_task);
        mAddTime = (FloatingActionButton) findViewById(R.id.add_time);

        mReclist = (RecyclerView) findViewById(R.id.list_of_tasks);

    }

    private void getProjectDetails(int projectId) {

        Project project =
                realm.where(Project.class).equalTo("id", projectId).findFirst();
        mProjectTitle.setText(project.getTitle());
        hourlyRate = project.getHourlyRate();
        mProjectHourlyRate.setText(getString(R.string.hourly_rate) + " : " + project.getHourlyRate());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                boolean result = data.getBooleanExtra("result", false);

                if(result) {
                    getTasksList(projectId);
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                boolean result = data.getBooleanExtra("result", false);

                if(result) {
                    getTotalTimeSpent(projectId);
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void getTasksList(int projectId) {

        listOfTasks.clear();
        RealmResults<Task> taskResults =
                realm.where(Task.class).equalTo("projectId", projectId).findAll();

        // projectResults = projectResults.sort("name"); // Default Alphabetically Sorting!

        for (Task t : taskResults) {

            final Task tempTask = new Task();
            tempTask.setId(t.getId());
            tempTask.setTaskName(t.getTaskName());
            tempTask.setTaskDescription(t.getTaskDescription());
            tempTask.setProjectId(t.getProjectId());
            tempTask.setStatus(t.isStatus());
            listOfTasks.add(tempTask);

        }

        mAdapter = new TasksAdapter(ProjectDetails.this, listOfTasks);
        mReclist.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // Toast.makeText(this, "Total Tasks : " + listOfTasks.size(), Toast.LENGTH_SHORT).show();

    }

    private void getTotalTimeSpent(int projectId) {

        totalMinutes = 0;

        RealmResults<TimeLog> timeLogResults =
                realm.where(TimeLog.class).equalTo("projectId", projectId).findAll();

        // projectResults = projectResults.sort("name"); // Default Alphabetically Sorting!

        for (TimeLog t : timeLogResults) {

            totalMinutes += t.getTimeSpent();

        }

        mProjectTime.setText(getString(R.string.time_spent) + " : " + totalMinutes + " " + getString(R.string.minutes));
        double hours = (double) totalMinutes / (double) 60;
        double cost = hours * hourlyRate;
        // Toast.makeText(this, hours + " : " + cost, Toast.LENGTH_SHORT).show();

        mProjectIncome.setText(getString(R.string.earned) + " : " + cost);

    }

}