package org.udg.pds.todoandroid.fragment;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Workout;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Workout}.
 * TODO: Replace the implementation with code for your data type.
 */
public class WorkoutRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutRecyclerViewAdapter.WorkoutViewHolder> {

    private List<Workout> mValues = new ArrayList<>();
    Context context;
    public WorkoutRecyclerViewAdapter() {

    }


    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WorkoutViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mId.setText(mValues.get(position).id.toString());
        holder.mType.setText(mValues.get(position).type);

        holder.mView.setOnClickListener(view -> {
            Log.i(TAG, "Hello!");
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setWorkouts(List<Workout> workouts) {
        mValues = workouts;
        notifyDataSetChanged();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mId;
        public final TextView mType;
        public Workout mItem;

        public WorkoutViewHolder(View view) {
            super(view);
            mView = view;
            mId = (TextView) view.findViewById(R.id.id);
            mType = (TextView) view.findViewById(R.id.type);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mType.getText() + "'";
        }
    }
}
