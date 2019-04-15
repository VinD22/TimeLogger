package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Project;
import project.timer.tracker.logger.time.v.app.timelogger.ProjectDetails;
import project.timer.tracker.logger.time.v.app.timelogger.R;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.RecyclerViewHolder> {

    private List<Project> data;
    private Context mContext;

    public ProjectsAdapter(Context context, ArrayList<Project> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.project_list_item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {

        final Project tempProject = data.get(viewHolder.getAdapterPosition());
        viewHolder.mProjectTitle.setText(capitalizeFirstLetter(tempProject.getTitle()));

        viewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, ProjectDetails.class);
                intent.putExtra("id", tempProject.getId());
                mContext.startActivity(intent);

            }
        });

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
        protected TextView mProjectTitle;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.lin);
            mProjectTitle = (TextView) itemView.findViewById(R.id.title);
        }

    }


}
