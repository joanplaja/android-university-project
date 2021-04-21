package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileActivity extends AppCompatActivity {
    TodoApi mTodoService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();
        setContentView(R.layout.view_profile);
        Bundle b = getIntent().getExtras();
        String username = b.getString("username");
        loadProfile(username);
        Button followButton, unfollowButton;
        followButton = findViewById(R.id.viewProfileFollowButton);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        unfollowButton = findViewById(R.id.viewProfileUnfollowButton);
        unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    public void loadProfile(String usernameArg) {
        //android todoApi (retrofit) -> Spring controller (retorna resposta http) -> onResponse i la processem.
        //response.body() es tipo user

        Call<Long> call = mTodoService.getIdByUsername(usernameArg);

        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Call<User> callUser = mTodoService.getUser(response.body());
                    callUser.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> callUser, Response<User> responseUser) {
                            if (responseUser.isSuccessful()) {
                                TextView viewProfileName = findViewById(R.id.viewProfileName);
                                viewProfileName.setText(responseUser.body().username);
                                TextView viewProfileDescription = findViewById(R.id.viewProfileDescription);
                                viewProfileDescription.setText(responseUser.body().description);
                            } else {
                                //Toast.makeText(ViewProfileFragment.this.getContext(), "Error reading tasks", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> callUser, Throwable t) {

                        }
                    });
                } else {
                    //Toast.makeText(ViewProfileFragment.this.getContext(), "Error reading tasks", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

  /*
    public static ViewProfileFragment newInstance(String username) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Button openEquipmentButton, updateProfileButton, signOutButton, workoutButton;
        View v = inflater.inflate(R.layout.fragment_view_profile, container, false);
        //super.onCreate(savedInstanceState);
        signOutButton = v.findViewById(R.id.viewProfileFollowButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        openEquipmentButton = v.findViewById(R.id.viewProfileUnfollowButton);
        openEquipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
    }
    public void loadProfile() {
        //android todoApi (retrofit) -> Spring controller (retorna resposta http) -> onResponse i la processem.
        //response.body() es tipo user
        String usernameArg = getArguments().getString("username");
        Call<Long> call = mTodoService.getIdByUsername(usernameArg);

        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Call<User> callUser = mTodoService.getUser(response.body());
                    callUser.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> callUser, Response<User> responseUser) {
                            if (responseUser.isSuccessful()) {
                                TextView viewProfileName = ViewProfileFragment.this.getView().findViewById(R.id.viewProfileName);
                                viewProfileName.setText(responseUser.body().username);
                                TextView viewProfileDescription = ViewProfileFragment.this.getView().findViewById(R.id.viewProfileDescription);
                                viewProfileDescription.setText(responseUser.body().description);
                            } else {
                                //Toast.makeText(ViewProfileFragment.this.getContext(), "Error reading tasks", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<User> callUser, Throwable t) {

                        }
                    });
                } else {
                    //Toast.makeText(ViewProfileFragment.this.getContext(), "Error reading tasks", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });
/*
        Call<User> callUser = mTodoService.getUser(response);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<user> response) {
                if (response.isSuccessful()) {
                    TextView viewProfileName = ViewProfileFragment.this.getView().findViewById(R.id.viewProfileName);
                    viewProfileName.setText(response.body().username);
                    TextView viewProfileDescription = ViewProfileFragment.this.getView().findViewById(R.id.viewProfileDescription);
                    viewProfileDescription.setText(response.body().description);
                } else {
                    //Toast.makeText(ViewProfileFragment.this.getContext(), "Error reading tasks", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        this.loadProfile();
    }
    */
}
