package org.udg.pds.todoandroid.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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

public class FollowersFragment extends Fragment {

    Context context;
    private SFAdapter mAdapter;
    TodoApi mTodoService;
    View rootView;

    RecyclerView mRecyclerView;


    private BroadcastReceiver mMessageReceiver;

    public FollowersFragment(){

    }

    public static FollowersFragment newInstance(String param1, String param2) {
        FollowersFragment fragment = new FollowersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        //setRetainInstance(false);
        setRetainInstance(true);
        Log.v("Oncreatexd","On create de ers");

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getExtras().getString("title");
                String body = intent.getExtras().getString("body");

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_personalizado,
                    (ViewGroup) getActivity().findViewById(R.id.custom_toast_container));

                TextView messageToast = (TextView) layout.findViewById(R.id.text);

                String text = title + body;

                messageToast.setText(text);

                Toast toast = new Toast(getActivity());

                toast.setGravity(Gravity.TOP, 0, 0);

                toast.setDuration(Toast.LENGTH_LONG);

                toast.setView(layout);

                toast.show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        mRecyclerView = getView().findViewById(R.id.FollowingRV);
        mAdapter = new SFAdapter(this.getActivity().getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        getTheFollowers();
        Log.v("Onstartxd","On start de ers");

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((mMessageReceiver),
            new IntentFilter("Notification Data")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();
        Log.v("Oncreatevxd","On createv de ers");
        return rootView;
    }

    public void getTheFollowers() {
        Call<List<User>> call = mTodoService.getOwnFollowers();
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

    class SFAdapter extends RecyclerView.Adapter<FollowersFragment.UserViewHolder> {

        List<User> list = new ArrayList<>();
        Context context;

        public SFAdapter(Context context) {
            this.context = context;
        }

        @Override
        public FollowersFragment.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_layout, parent, false);
            FollowersFragment.UserViewHolder holder = new FollowersFragment.UserViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(FollowersFragment.UserViewHolder holder, final int position) {
            holder.username.setText(list.get(position).username);
            if(list.get(position).imageUrl !=null) {
                Picasso.get().load(list.get(position).imageUrl).fit().centerCrop().into(holder.followerProfileImage);
            }
            else
                holder.followerProfileImage.setImageResource(R.drawable.profile_photo);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String usrname = list.get(position).username;
                    Bundle bundle = new Bundle();
                    bundle.putString("username", usrname);
                    Navigation.findNavController(rootView).navigate(R.id.action_FollowingFollowersFragment_to_ViewProfileFragment, bundle);
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
