package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUserFragment extends Fragment {
    Context context;
    TodoApi mTodoService;
    public View rootView;

    RecyclerView mRecyclerView;
    private SUAdapter mAdapter;

    public static SearchUserFragment newInstance(String param1, String param2) {
        SearchUserFragment fragment = new SearchUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        mRecyclerView = getView().findViewById(R.id.users_recyclerview);
        mAdapter = new  SUAdapter(this.getActivity().getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        context = this.getContext();
        Button b = rootView.findViewById(R.id.search_button);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText u = getView().findViewById(R.id.search_username);
                if(u.length() == 0){
                    u.setError("Username can't be empty");
                }
                else {
                    String query = "username:" + u.getText().toString();
                    executeSearch(query);
                }
            }
        });
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.search_user);

      /*  mTodoService = ((TodoApp) this.getApplication()).getAPI();

        mRecyclerView = SearchActivity.this.findViewById(R.id.users_recyclerview);
        mAdapter = new SearchActivity.SUAdapter(this.getApplication());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));*/


    }


    public void executeSearch(String username) {
        Call<List<User>> call = mTodoService.searchUser(username);
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

        UserViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            username = itemView.findViewById(R.id.itemUsername);
        }
    }

    class SUAdapter extends RecyclerView.Adapter<SearchUserFragment.UserViewHolder> {

        List<User> list = new ArrayList<>();
        Context context;

        public SUAdapter(Context context) {
            this.context = context;
        }

        @Override
        public SearchUserFragment.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
            SearchUserFragment.UserViewHolder holder = new SearchUserFragment.UserViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, final int position) {
            holder.username.setText(list.get(position).username);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String usrname = list.get(position).username;
                    Bundle bundle = new Bundle();
                    bundle.putString("username", usrname);
                    Navigation.findNavController(view).navigate(R.id.action_SearchUserFragment_to_ViewProfileFragment, bundle);
                  /*  Intent i = new Intent(SearchActivity.this, ViewProfileActivity.class);
                    i.putExtra("username",list.get(position).username);
                    SearchActivity.this.startActivity(i);
                    SearchActivity.this.finish();*/
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
