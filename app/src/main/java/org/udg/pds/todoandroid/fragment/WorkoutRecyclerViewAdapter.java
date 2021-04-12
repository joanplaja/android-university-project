package org.udg.pds.todoandroid.fragment;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private OnWorkoutListener mOnWorkoutListener;


    public WorkoutRecyclerViewAdapter(OnWorkoutListener onWorkoutListener) {
        this.mOnWorkoutListener = onWorkoutListener;
    }


    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_workout, parent, false);
        return new WorkoutViewHolder(view, mOnWorkoutListener);
    }

    @Override
    public void onBindViewHolder(final WorkoutViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mId.setText(mValues.get(position).id.toString());
        holder.mType.setText(mValues.get(position).type.toUpperCase());
        String type = mValues.get(position).type;
        switch (type) {
            case "running":
                holder.mIcon.setImageResource(R.drawable.running);
                break;
            case "cycling":
                holder.mIcon.setImageResource(R.drawable.cycling);
                break;
            case "hiking":
                holder.mIcon.setImageResource(R.drawable.hiking);
                break;
            case "walking":
                holder.mIcon.setImageResource(R.drawable.walking);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setWorkouts(List<Workout> workouts) {
        mValues = workouts;
        notifyDataSetChanged();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mId;
        public final TextView mType;
        public final ImageView mIcon;
        public Workout mItem;
        OnWorkoutListener onWorkoutListener;
        public WorkoutViewHolder(View view, OnWorkoutListener onWorkoutListener) {
            super(view);
            mView = view;
            mId = (TextView) view.findViewById(R.id.id);
            mType = (TextView) view.findViewById(R.id.type);
            mIcon = (ImageView) view.findViewById(R.id.icon);
            this.onWorkoutListener = onWorkoutListener;

            view.setOnClickListener(this);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mType.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            onWorkoutListener.onWorkoutClick(getAdapterPosition());
        }
    }

    public interface OnWorkoutListener {
        void onWorkoutClick(int position);
    }
}
