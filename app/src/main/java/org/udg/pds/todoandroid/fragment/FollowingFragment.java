package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;

import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingFragment extends Fragment{

    Context context;
    private SFAdapter mAdapter;
    TodoApi mTodoService;
    View rootView;

    RecyclerView mRecyclerView;

    public FollowingFragment(){

    }

    public static FollowingFragment newInstance(String param1, String param2) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

   /* public void update() {
        getTheFollowing();
    }*/

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
       // setRetainInstance(false);
        setRetainInstance(true);
        Log.v("Oncreatexd","On create de ing");
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        mRecyclerView = getView().findViewById(R.id.FollowingRV);
        mAdapter = new SFAdapter(this.getActivity().getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        Log.v("Onstartxd","On start de ing");
        //getTheFollowing();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();
        Log.v("Oncreatevxd","On createv de ing");
        //this.getTheFollowing();
        return rootView;
    }

    public void getTheFollowing() {
        Call<List<User>> call = mTodoService.getOwnFollowing();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    showUserList(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }

        });
    }

    public void showUserList(List<User> ul) {
        mAdapter.clear();
        for (User u : ul) {
            mAdapter.add(u);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Onresumexd","On resume de ing");
        this.getTheFollowing();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.v("Ondestvxd","On destv de ing");
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        View view;
        Button unfollowButton;
        ImageView followingProfileImage;

        UserViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            username = itemView.findViewById(R.id.itemUsername);
            unfollowButton = itemView.findViewById(R.id.buttonUnfollow);
            followingProfileImage = itemView.findViewById(R.id.imageViewFollowing);
        }
    }

    class SFAdapter extends RecyclerView.Adapter<FollowingFragment.UserViewHolder> {

        List<User> list = new ArrayList<>();
        Context context;

        public SFAdapter(Context context) {
            this.context = context;
        }

        @Override
        public FollowingFragment.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_layout, parent, false);
            FollowingFragment.UserViewHolder holder = new FollowingFragment.UserViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(FollowingFragment.UserViewHolder holder, final int position) {
            holder.username.setText(list.get(position).username);
            if(list.get(position).imageUrl !=null) {
                Picasso.get().load(list.get(position).imageUrl).fit().centerCrop().into(holder.followingProfileImage);
            }
            else
                holder.followingProfileImage.setImageResource(R.drawable.profile_photo);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String usrname = list.get(position).username;
                    Bundle bundle = new Bundle();
                    bundle.putString("username", usrname);
                    Navigation.findNavController(rootView).navigate(R.id.action_FollowingFollowersFragment_to_ViewProfileFragment, bundle);
                }
            });

            holder.unfollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Call<Long> call = mTodoService.getIdByUsername(list.get(position).username);

                    call.enqueue(new Callback<Long>() {
                        @Override
                        public void onResponse(Call<Long> call, Response<Long> response) {
                            if (response.isSuccessful()) {

                                Call<String> callUser = mTodoService.unfollowUser(response.body());
                                callUser.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> callUser, Response<String> responseUser) {
                                        if (responseUser.isSuccessful()) {
                                            getTheFollowing();
                                        } else {
                                            //
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> callUser, Throwable t) {

                                    }
                                });
                            } else {
                                //
                            }
                            }
                            @Override
                            public void onFailure(Call<Long> call, Throwable t) {

                            }
                        });
                }
            });


            animate(holder);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {

            super.onAttachedToRecyclerView(recyclerView);
        }

        // Insert a new item to the RecyclerView
        public void insert(int position, User data) {
            list.add(position, data);
            notifyItemInserted(position);
        }

        // Remove a RecyclerView item containing the Data object
        public void remove(User data) {
            int position = list.indexOf(data);
            list.remove(position);
            notifyItemRemoved(position);
        }

        public void animate(RecyclerView.ViewHolder viewHolder) {
            final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
            viewHolder.itemView.setAnimation(animAnticipateOvershoot);
        }

        public void add(User u) {
            list.add(u);
            this.notifyItemInserted(list.size() - 1);
        }

        public void clear() {
            int size = list.size();
            list.clear();
            this.notifyItemRangeRemoved(0, size);
        }
    }
}
