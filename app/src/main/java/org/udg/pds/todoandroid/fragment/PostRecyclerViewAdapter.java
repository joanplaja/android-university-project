package org.udg.pds.todoandroid.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    TodoApi mTodoService;
    private Context context;
    private List<Post> mValues = new ArrayList<>();
    DictionaryImages dictionaryImages = new DictionaryImages();

    public PostRecyclerViewAdapter(List<Post> items) {
        mValues = items;
    }

    public PostRecyclerViewAdapter(Context context, TodoApi todoService) {
        this.context = context;
        mTodoService = todoService;
    }

    public PostRecyclerViewAdapter(FragmentActivity c) {
        mTodoService = ((TodoApp) c.getApplication()).getAPI();
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


        Call<User> callGetMe = mTodoService.getUserMe();
        callGetMe.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> callGetMe, Response<User> responseGetMe) {
                Call<List<User>> callLikes = mTodoService.getLikes(holder.mItem.id);
                callLikes.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> callLike, Response<List<User>> responseLikes) {
                        holder.mLikes.setText(String.valueOf(responseLikes.body().size()));
                        holder.mLiked = false;
                        int i = 0;
                        while (!holder.mLiked && i < responseLikes.body().size()) {
                            if (responseLikes.body().get(i).username.equals(responseGetMe.body().username))
                                holder.mLiked = true;
                            i++;
                        }
                        if (holder.mLiked) {
                            holder.mLikeButton.setBackgroundColor(0xFF85B668);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> callLikes, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<User> callGetMe, Throwable t) {

            }
        });




        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.mLiked){
                    Call <String> call = mTodoService.likePost(holder.mItem.id);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            holder.mLikeButton.setBackgroundColor(0xFF85B668);
                            Integer nLikes = Integer.valueOf(holder.mLikes.getText().toString());
                            nLikes = nLikes+1;
                            holder.mLikes.setText(String.valueOf(nLikes));
                            holder.mLiked=true;
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
                else if (holder.mLiked){
                    Call <String> call = mTodoService.removeLikePost(holder.mItem.id);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            holder.mLikeButton.setBackgroundColor(0xFFCCCACA);
                            Integer nLikes = Integer.valueOf(holder.mLikes.getText().toString());
                            nLikes = nLikes-1;
                            holder.mLikes.setText(String.valueOf(nLikes));
                            holder.mLiked=false;
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }

            }
        });


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
        public final TextView mLikes;
        public final Button mLikeButton;
        public ImageView mDelete;
        public Post mItem;
        public boolean mLiked;

        public PostViewHolder(View view) {
            super(view);
            mView = view;
            mLiked = false;
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
            mLikes = (TextView) view.findViewById(R.id.textViewNLikes);
            mLikeButton = (Button) view.findViewById(R.id.buttonLike);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
