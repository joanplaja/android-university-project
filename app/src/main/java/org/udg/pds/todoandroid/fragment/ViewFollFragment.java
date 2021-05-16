package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class ViewFollFragment extends Fragment {
    Context context;
    private SFAdapter mAdapter;
    TodoApi mTodoService;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private Long mParam2;
    View rootView;

    RecyclerView mRecyclerView;

    public ViewFollFragment(){

    }

    public ViewFollFragment(String param1,Long param2){
        mParam1 = param1;
        mParam2 = param2;
    }

    public static ViewFollFragment newInstance(String param1, Long param2) {
        ViewFollFragment fragment = new ViewFollFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2,param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        mRecyclerView = getView().findViewById(R.id.FollowingRV);
        mAdapter = new SFAdapter(this.getActivity().getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        Log.v("ALVIEWFOLLXD", String.valueOf(mParam2));
        if(mParam1.equals("ers"))
            getTheFollowers(mParam2);
        else
            getTheFollowing(mParam2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();
        return rootView;
    }

    public void getTheFollowers(Long id) {
        //Log.v("Ongettheers", String.valueOf(mParam2));
        Call<List<User>> call = mTodoService.getFollowers(mParam2);
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

    public void getTheFollowing(Long id) {
        Log.v("Ongettheing", String.valueOf(mParam2));
        Call<List<User>> call = mTodoService.getFollowing(Long.valueOf(mParam2));
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

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        View view;
        ImageView followerProfileImage;

        UserViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            username = itemView.findViewById(R.id.itemUsername);
            followerProfileImage = itemView.findViewById(R.id.imageViewFollower);
        }
    }

    class SFAdapter extends RecyclerView.Adapter<ViewFollFragment.UserViewHolder> {

        List<User> list = new ArrayList<>();
        Context context;

        public SFAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewFollFragment.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_layout, parent, false);
            ViewFollFragment.UserViewHolder holder = new ViewFollFragment.UserViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewFollFragment.UserViewHolder holder, final int position) {
            holder.username.setText(list.get(position).username);
            if(list.get(position).imageUrl !=null) {
                Picasso.get().load(list.get(position).imageUrl).fit().centerCrop().into(holder.followerProfileImage);
            }
            else
                holder.followerProfileImage.setImageResource(R.drawable.profile_photo);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String usrname = list.get(position).username;
                            Bundle bundle = new Bundle();
                            bundle.putString("username", usrname);
                            Navigation.findNavController(rootView).navigate(R.id.action_ViewFollowingFollowersFragment_to_ViewProfileFragment, bundle);
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
