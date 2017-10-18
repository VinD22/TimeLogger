package project.timer.tracker.logger.time.v.app.timelogger;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import model.Project;

/**
 * Activity to add a new project.
 */

public class AddProject extends AppCompatActivity {

    Realm realm;

    private Toolbar mToolbar;
    private EditText mProjectTitle, mClientName, mProjectDescription, mHourlyRates, mAdditionalInformation;
    private static Button mSelectDate;
    private Button mAddProject;

    static Date date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_project);

        realm = Realm.getDefaultInstance();

        setupWidgets();

        mSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment dialogfragment = new DatePickerDialogTheme1();

                dialogfragment.show(getFragmentManager(), "Theme 1");

            }
        });

        mAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String projectTitle = mProjectTitle.getText().toString();
                String clientName = mClientName.getText().toString();
                String projectDescription = mProjectDescription.getText().toString();
                String hourlyRates = mHourlyRates.getText().toString();
                String additionalInformation = mAdditionalInformation.getText().toString();

                if (projectTitle.isEmpty() || clientName.isEmpty() || projectDescription.isEmpty() || hourlyRates.isEmpty()) {
                    Toast.makeText(AddProject.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
                } else if (date == null) {
                    Toast.makeText(AddProject.this, R.string.select_start_date, Toast.LENGTH_SHORT).show();
                } else {

                    double hourlyRatesInDouble = Double.parseDouble(hourlyRates);

                    realm.beginTransaction();

                    Project newLog = realm.createObject(Project.class);
                    int nextKey = getNextKey();
                    newLog.setId(nextKey);
                    newLog.setTitle(projectTitle);
                    newLog.setClientName(clientName);
                    newLog.setDescription(projectDescription);
                    newLog.setHourlyRate(hourlyRatesInDouble);
                    newLog.setDate(date);
                    newLog.setTotalTime(0.0);
                    realm.commitTransaction();

//                  Toast.makeText(AddProject.this, "Log Added!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProject.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }

            }
        });


    }

    private void setupWidgets() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mProjectTitle = (EditText) findViewById(R.id.project_title);
        mClientName = (EditText) findViewById(R.id.client_name);
        mProjectDescription = (EditText) findViewById(R.id.project_description);
        mHourlyRates = (EditText) findViewById(R.id.hourly_rate);
        mAdditionalInformation = (EditText) findViewById(R.id.project_additional_information);

        mAddProject = (Button) findViewById(R.id.add_project);
        mSelectDate = (Button) findViewById(R.id.select_date);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.add_project);
            getSupportActionBar().setTitle(R.string.add_project);
            // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }


    public static class DatePickerDialogTheme1 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, year, month, day);

//            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
//                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,this,year,month,day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            try {
                int correctMonth = month + 1;
                date = fmt.parse(year + "-" + correctMonth + "-" + day);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mSelectDate.setText(day + " - " + (month + 1) + " - " + year);

        }
    }

    public int getNextKey() {
        try {
            return realm.where(Project.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

}
