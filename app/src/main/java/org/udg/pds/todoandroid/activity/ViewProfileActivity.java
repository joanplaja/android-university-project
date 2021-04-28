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
import org.udg.pds.todoandroid.fragment.UserProfileFragment;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileActivity extends AppCompatActivity {
    TodoApi mTodoService;

    public interface GetCallbacks{
        void onSucces(Long id,boolean alreadyFollowing);
        void onUserFailed(Throwable error);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();
        setContentView(R.layout.view_profile);
        Bundle b = getIntent().getExtras();
        String username = b.getString("username");

        loadProfile(username, new GetCallbacks() {
            @Override
            public void onSucces(Long id,boolean alreadyFollowing) {
                Button followButton;
                followButton = findViewById(R.id.viewProfileFollowButton);
               if(alreadyFollowing){
                   followButton.setText("Following");
               }
              else {
                   followButton.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Call<String> call = mTodoService.followUser(id);
                           call.enqueue(new Callback<String>() {
                               @Override
                               public void onResponse(Call<String> call, Response<String> response) {
                                   if (response.isSuccessful()) {
                                       Toast.makeText(ViewProfileActivity.this, "Successfuly followed", Toast.LENGTH_SHORT).show();
                                       Intent refresh = new Intent(ViewProfileActivity.this, ViewProfileActivity.class);
                                       refresh.putExtra("username",username);
                                       startActivity(refresh);
                                       ViewProfileActivity.this.finish();
                                   }
                               }

                               @Override
                               public void onFailure(Call<String> call, Throwable t) {

                               }
                           });
                       }
                   });
               }
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
                                Intent refresh = new Intent(ViewProfileActivity.this, ViewProfileActivity.class);
                                refresh.putExtra("username",username);
                                startActivity(refresh);
                                ViewProfileActivity.this.finish();
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

    public void loadProfile(String usernameArg,final GetCallbacks getCallbacks) {
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

                    Call<List<User>> callFollowing = mTodoService.getFollowing(response.body());
                    callFollowing.enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> callFollowing, Response<List<User>> responseFollowing) {
                            TextView viewProfileFollowing = findViewById(R.id.viewProfileFollowingNumber);
                            viewProfileFollowing.setText(String.valueOf(responseFollowing.body().size()));
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {

                        }
                    });

                    Call <User> callGetMe = mTodoService.getUserMe();
                    callGetMe.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> callGetMe, Response<User> responseGetMe) {
                            Call<List<User>> callFollowers = mTodoService.getFollowers(response.body());
                            callFollowers.enqueue(new Callback<List<User>>() {
                                @Override
                                public void onResponse(Call<List<User>> callFollowers, Response<List<User>> responseFollowers) {
                                    TextView viewProfileFollowers = findViewById(R.id.viewProfileFollowersNumber);
                                    viewProfileFollowers.setText(String.valueOf(responseFollowers.body().size()));
                                    boolean found =false;
                                    int i =0;
                                    while(!found && i<responseFollowers.body().size()){
                                        if(responseFollowers.body().get(i).username.equals(responseGetMe.body().username))
                                            found = true;
                                        i++;
                                    }
                                    if (found){
                                        getCallbacks.onSucces(response.body(),true);
                                    }
                                    else{
                                        getCallbacks.onSucces(response.body(),false);
                                    }

                                }

                                @Override
                                public void onFailure(Call<List<User>> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<User> callGetMe, Throwable t) {

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


}
