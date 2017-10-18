package project.timer.tracker.logger.time.v.app.timelogger;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import adapter.ProjectsAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Project;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAnalytics mFirebaseAnalytics;
    Realm realm;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddProject;

    private ProjectsAdapter mAdapter;

    ArrayList<Project> listOfProjects = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: started main activity!");

        // For Push Notification!
        if (getIntent().getExtras() == null) {
            // Toast.makeText(this, "Error in intents! (Contact Developer)", Toast.LENGTH_SHORT).show();
        } else {

            Bundle extras = getIntent().getExtras();
            String url = extras.getString("url");
            // Toast.makeText(this, "Url : " + url, Toast.LENGTH_SHORT).show();

            if (url != null) {

                if (!url.isEmpty()) {

                    try {

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                    } catch (ActivityNotFoundException e) {

                    }


                }

            }

        }

        // Initialized Realm!
        realm = Realm.getDefaultInstance();

        // Setup Widgets!
        setupWidgets();

        // Setup Firebase Analytics
        setupFirebaseAnalytics();

        mAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddProject.class);
                startActivity(intent);

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mAddProject.hide();
                else if (dy < 0)
                    mAddProject.show();
            }
        });

    }

    private void setupWidgets() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.projects);
            getSupportActionBar().setTitle(R.string.projects);
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.projects_list_recycler_view);
        mAddProject = (FloatingActionButton) findViewById(R.id.fab_add_project);

    }

    private void setupFirebaseAnalytics() {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Projects Screen!");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "All the projects appear on this page!");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getProjectsList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                // Toast.makeText(this, "Show Settings!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }

    public void getProjectsList() {

        listOfProjects.clear();
        RealmResults<Project> projectResults =
                realm.where(Project.class).findAll();

        // projectResults = projectResults.sort("name"); // Default Alphabetically Sorting!

        for (Project t : projectResults) {

            final Project tempProject = new Project();
            tempProject.setId(t.getId());
            tempProject.setTitle(t.getTitle());
            tempProject.setAdditionalDetails(t.getAdditionalDetails());
            tempProject.setHourlyRate(t.getHourlyRate());
            tempProject.setDescription(t.getDescription());
            tempProject.setClientName(t.getClientName());
            tempProject.setTotalTime(t.getTotalTime());
            tempProject.setDate(t.getDate());

            listOfProjects.add(tempProject);

        }

        mAdapter = new ProjectsAdapter(MainActivity.this, listOfProjects);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        Log.i("totalProjects", " " + listOfProjects.size());

    }


}
