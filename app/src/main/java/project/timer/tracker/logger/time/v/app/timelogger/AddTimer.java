package project.timer.tracker.logger.time.v.app.timelogger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.piotrek.customspinner.CustomSpinner;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Task;
import model.TimeLog;

public class AddTimer extends AppCompatActivity {

    private Chronometer mChronometer;
    private Button mPlay, mStop, mReset, mAddTimeLog;
    private CustomSpinner mTasksSpinner;

    private ArrayList<Task> listOfTasks = new ArrayList<>();
    private ArrayList<String> listOfTaskNames = new ArrayList<>();
    String[] arrayTaskNames = new String[] {};

    Realm realm;

    int projectId = -1;
    boolean isTimerOn = false;

    int minutes = -1;
    int selectedTaskId = -1;

    long timeWhenStopped = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_timer);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        mPlay = (Button) findViewById(R.id.play);
        mStop = (Button) findViewById(R.id.stop);
        mReset = (Button) findViewById(R.id.reset);

        mTasksSpinner = (CustomSpinner) findViewById(R.id.tasks_spinner);
        mAddTimeLog = (Button) findViewById(R.id.add_time_log);

        // Initialize realm
        realm = Realm.getDefaultInstance();

        // get project id passed through intents.
        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            Toast.makeText(this, "No id passed! Error! ", Toast.LENGTH_SHORT).show();
        } else {

            projectId = extras.getInt("id");
            getTasksList(projectId);

        }

        // Play, Pause, Resume handlers.

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isTimerOn = true;

                // mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                mChronometer.start();

                mPlay.setEnabled(false);
                mPlay.setClickable(false);

            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isTimerOn = false;

                timeWhenStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();
                mChronometer.stop();

                mPlay.setText(R.string.resume);
                mPlay.setClickable(true);
                mPlay.setEnabled(true);

                // Get minutes
                long timeElapsed = SystemClock.elapsedRealtime() - mChronometer.getBase();

                int hours = (int) (timeElapsed / 3600000);
                minutes = (int) (timeElapsed - hours * 3600000) / 60000;
                // int seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;

                Toast.makeText(AddTimer.this, "Minutes : " + minutes, Toast.LENGTH_SHORT).show();


            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isTimerOn = false;

                minutes = -1;

                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.stop();
                timeWhenStopped = 0;

                mPlay.setText(R.string.play);
                mPlay.setClickable(true);
                mPlay.setEnabled(true);

            }
        });

        mAddTimeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isTimerOn || selectedTaskId == -1 || minutes < 0) {
                    Toast.makeText(AddTimer.this, R.string.check_time_task, Toast.LENGTH_SHORT).show();
                } else {

                    realm.beginTransaction();

                    TimeLog newTask = realm.createObject(TimeLog.class);

                    int nextKey = getNextKey();
                    newTask.setId(nextKey);
                    newTask.setProjectId(projectId);
                    newTask.setTaskId(selectedTaskId);
                    newTask.setTimeSpent(minutes);

                    realm.commitTransaction();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", true);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();


                }

            }
        });

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
            listOfTaskNames.add(t.getTaskName());
            tempTask.setTaskDescription(t.getTaskDescription());
            tempTask.setProjectId(t.getProjectId());
            tempTask.setStatus(t.isStatus());
            listOfTasks.add(tempTask);

        }

        // String[] stringArray = listOfTaskNames.toArray(new String[listOfTaskNames.size()]);
        arrayTaskNames = listOfTaskNames.toArray(new String[0]);

        mTasksSpinner.initializeStringValues(arrayTaskNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_row, listOfTaskNames);
        mTasksSpinner.setAdapter(adapter);

        mTasksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int i, long l) {

                ((TextView) arg0.getChildAt(0)).setTextColor(Color.WHITE);
                selectedTaskId = listOfTasks.get(i).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTaskId = -1;
            }
        });

         // Toast.makeText(this, "Total Tasks : " + listOfTasks.size(), Toast.LENGTH_SHORT).show();

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
