package org.udg.pds.todoandroid.fragment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.DictionaryImages;
import org.udg.pds.todoandroid.entity.Post;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private List<Post> mValues = new ArrayList<>();
    DictionaryImages dictionaryImages = new DictionaryImages();

    public PostRecyclerViewAdapter(List<Post> items) {
        mValues = items;
    }

    public PostRecyclerViewAdapter() {

    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUsernameView.setText(holder.mItem.workout.user.username);
        holder.mDescriptionView.setText(holder.mItem.description);
        //mImageView
        //mAvatarView
        Integer icon = dictionaryImages.images.get(holder.mItem.workout.type);
        holder.mWorkoutIconView.setImageResource(icon);
        holder.mWorkoutTypeView.setText(holder.mItem.workout.type);

        //TODO: fer el calcul de la distancia
        double distance = 20.0;
        String textDistance = "Distance: " + distance;
        holder.mDistanceView.setText(textDistance);

        //TODO: fer el calcul del temps
        double time = 20.0;
        String textTime = "Time: " + time;
        holder.mTimeView.setText(textTime);

        //TODO: fer el calcul del l'average pace
        double averagePace = 15.34;
        String textAveragePace = "Avg. Pace: " + averagePace;
        holder.mAveragePaceView.setText(textAveragePace);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setPosts(List<Post> posts) {
        mValues = posts;
        notifyDataSetChanged();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAvatarView;
        public final TextView mUsernameView;
        public final TextView mDescriptionView;
        public final TextView mWorkoutTypeView;
        public final ImageView mWorkoutIconView;
        public final TextView mDistanceView;
        public final TextView mTimeView;
        public final TextView mAveragePaceView;
        public final ImageView mImageView;
        public Post mItem;

        public PostViewHolder(View view) {
            super(view);
            mView = view;
            mUsernameView = (TextView) view.findViewById(R.id.username);
            mAvatarView = (ImageView) view.findViewById(R.id.avatar);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mWorkoutIconView = (ImageView) view.findViewById(R.id.workoutIcon);
            mWorkoutTypeView = (TextView) view.findViewById(R.id.workoutType);
            mDistanceView = (TextView) view.findViewById(R.id.distance);
            mTimeView = (TextView) view.findViewById(R.id.time);
            mAveragePaceView = (TextView) view.findViewById(R.id.averagePace);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
