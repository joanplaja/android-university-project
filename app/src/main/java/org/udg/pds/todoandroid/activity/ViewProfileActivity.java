package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    public interface GetCallbacks{
        void onSucces(Long id);
        void onUserFailed(Throwable error);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();
        setContentView(R.layout.view_profile);
        Bundle b = getIntent().getExtras();
        String username = b.getString("username");

        Long userId = loadProfile(username, new GetCallbacks() {
            @Override
            public void onSucces(Long id) {
                Button followButton;
                followButton = findViewById(R.id.viewProfileFollowButton);
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Call <String> call = mTodoService.followUser(id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(ViewProfileActivity.this, "Successfuly followed", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }
                });

                Button unfollowButton;
                unfollowButton = findViewById(R.id.viewProfileUnfollowButton);
                unfollowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Call <String> call = mTodoService.unfollowUser(id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(ViewProfileActivity.this, "Successfuly unfollowed", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }

                        });
                    }
                });
            }

            @Override
            public void onUserFailed(Throwable error) {

            }

        });

    }

    public Long loadProfile(String usernameArg,final GetCallbacks getCallbacks) {
        //android todoApi (retrofit) -> Spring controller (retorna resposta http) -> onResponse i la processem.
        //response.body() es tipo user
        Long ret = Long.valueOf(0);
        Call<Long> call = mTodoService.getIdByUsername(usernameArg);

        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    getCallbacks.onSucces(response.body());
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

        return ret;
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}
