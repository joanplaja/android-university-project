package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TodoApi mTodoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signout);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();

        Button b = findViewById(R.id.buttonSignOutActivity);
        // This is teh listener that will be used when the user presses the "Login" button
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SignoutActivity.this.logout();
            }
        });


        Button but = findViewById(R.id.returnFromSignoutButton);
        // This is teh listener that will be used when the user presses the "Login" button
        but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void logout(){
        Call<String> call = mTodoService.logout();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    SignoutActivity.this.startActivity(new Intent(SignoutActivity.this, Login.class));
                    SignoutActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(SignoutActivity.this, "Error logging out", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id == R.id.maps) {
            SignoutActivity.this.startActivity(new Intent(SignoutActivity.this, ActivityMaps.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
