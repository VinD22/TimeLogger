package adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Task;
import model.TimeLog;
import project.timer.tracker.logger.time.v.app.timelogger.R;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.RecyclerViewHolder> {

    private List<Task> data;
    private Context mContext;
    Realm realm;

    public TasksAdapter(Context context, ArrayList<Task> data) {
        this.mContext = context;
        this.data = data;
        realm = Realm.getDefaultInstance();
        // setHasStableIds(true);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.task_list_item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {

        final Task tempTask = data.get(viewHolder.getAdapterPosition());
        viewHolder.mTaskTitle.setText(capitalizeFirstLetter(tempTask.getTaskName()));
        viewHolder.mTaskStatus.setChecked(tempTask.isStatus());

        final int timeSpent = getTotalTimeSpent(tempTask.getProjectId(), tempTask.getId());

        viewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Todo : Show Dialog with details about the task.

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("" + tempTask.getTaskName() + " | " + timeSpent + " " + mContext.getString(R.string.minutes));
                builder.setMessage("" + tempTask.getTaskDescription());
                builder.setPositiveButton("OK", null);
                builder.show();

            }
        });

        viewHolder.mTaskStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                // Update Realm
                Task toEdit = realm.where(Task.class).equalTo("id", tempTask.getId()).findFirst();
                realm.beginTransaction();
                toEdit.setStatus(isChecked);
                realm.commitTransaction();

            }
        });
    }

    private int getTotalTimeSpent(int projectId, int taskId) {

        int totalMinutes = 0;

        RealmResults<TimeLog> timeLogResults =
                realm.where(TimeLog.class).equalTo("projectId", projectId).equalTo("taskId", taskId).findAll();

        // projectResults = projectResults.sort("name"); // Default Alphabetically Sorting!

        for (TimeLog t : timeLogResults) {

            totalMinutes += t.getTimeSpent();

        }

        return totalMinutes;

    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;
        protected TextView mTaskTitle;
        protected CheckBox mTaskStatus;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.lin);
            mTaskTitle = (TextView) itemView.findViewById(R.id.title);
            mTaskStatus = (CheckBox) itemView.findViewById(R.id.status);
        }

    }


}
