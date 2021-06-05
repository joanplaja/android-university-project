package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashScreen extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onResume() {
        super.onResume();

        TodoApi todoApi = ((TodoApp) this.getApplication()).getAPI();

        Call<String> call = todoApi.check();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, NavigationActivity.class));
                    SplashScreen.this.finish();
                } else {
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, ChooseRegisterLogin.class));
                    SplashScreen.this.finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(SplashScreen.this, "Error checking login status", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Call<User> callUser = todoApi.getUserMe();
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> callUser, Response<User> response) {

                if (response.isSuccessful()) {
                    ((TodoApp) SplashScreen.this.getApplication()).setUser(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> callUser, Throwable t) {

            }
        });


    }
}
