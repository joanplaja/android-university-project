package org.udg.pds.todoandroid.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.Login;
import org.udg.pds.todoandroid.entity.DictionaryImages;
import org.udg.pds.todoandroid.entity.Point;
import org.udg.pds.todoandroid.entity.Post;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private Context context;
    private List<Post> mValues = new ArrayList<>();
    DictionaryImages dictionaryImages = new DictionaryImages();
    TodoApi mTodoService;

    public PostRecyclerViewAdapter(List<Post> items) {
        mValues = items;
    }

    public PostRecyclerViewAdapter(Context context, TodoApi todoService) {
        this.context = context;
        mTodoService = todoService;
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

        //Recupero la uri de la imatge
        String imageUri = holder.mItem.imageUrl;
        if (imageUri != "") {
            Picasso.get().load(imageUri).fit().centerCrop().into(holder.mImageView);
        }

        //Recupero la uri de l'avatar
        String avatarUri = holder.mItem.workout.user.imageUrl;
        if (avatarUri != null) {
            Picasso.get().load(avatarUri).fit().centerCrop().into(holder.mAvatarView);
        }

        //Recupero la icona del tipus de workout
        Integer icon = dictionaryImages.images.get(holder.mItem.workout.type);
        holder.mWorkoutIconView.setImageResource(icon);

        holder.mWorkoutTypeView.setText(holder.mItem.workout.type);

        DecimalFormat df = new DecimalFormat("#.00");

        //Fem el calcul de la distancia
        double distance = 0.0;
        for (Point p : holder.mItem.workout.route.points) {
            distance = distance + p.distanceDiff;
        }
        String textDistance = "Distance: " + df.format(distance) + " km";
        holder.mDistanceView.setText(textDistance);

        //Fem el calcul del temps
        double time = 0.0;
        for (Point p : holder.mItem.workout.route.points) {
            time = time + p.timeDiff;
        }
        String textTime = "Time: " + df.format(time) + " min";
        holder.mTimeView.setText(textTime);

        //Fem el calcul del averagePace
        double averagePace = 0.0;
        for (Point p : holder.mItem.workout.route.points) {
            averagePace = averagePace + p.velocity;
        }
        averagePace = averagePace / holder.mItem.workout.route.points.size();
        String textAveragePace = "Avg. Pace: " + df.format(averagePace) + " min/km";
        holder.mAveragePaceView.setText(textAveragePace);
        String nomUserLogin = cargarPreferencies();

        if ( nomUserLogin.equals(holder.mItem.workout.user.username) )
        {
            holder.mDelete.setVisibility(View.VISIBLE); //SHOW the button
        }

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(mValues.get(position).id, position);
            }
        });

    }

    private String cargarPreferencies(){
        SharedPreferences prefs = context.getSharedPreferences("credencials", context.MODE_PRIVATE);
        String userName = prefs.getString("name", "No name defined");//"No name defined" is the default value.
        return userName;
    }

    public void onDeleteClick(Long id, int position){
            Call<String> call = mTodoService.deletePost(id.toString());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        removeAt(position);
                        Log.i(TAG, "onResponse: be");
                    } else {
                        Log.i(TAG, "onResponse: malament");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i(TAG, "onFailure: malament");
                }
            });

    }
    public void removeAt(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mValues.size());
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
        public ImageView mDelete;
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
            mDelete = (ImageView) view.findViewById(R.id.icon_delete);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
