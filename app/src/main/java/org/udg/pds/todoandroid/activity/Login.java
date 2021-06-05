package org.udg.pds.todoandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.fragment.PostFragment;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 28/03/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */

// This is the Login fragment where the user enters the username and password and
// then a RESTResponder_RF is called to check the authentication
public class Login extends AppCompatActivity {

    TodoApi mTodoService;
    EditText campUsuari;
    private String deviceId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();
        campUsuari = (EditText) findViewById(R.id.editTextUsernameLogin);
        deviceId = ((TodoApp) this.getApplication()).getToken();

        Button b = findViewById(R.id.login_button);
        // This is teh listener that will be used when the user presses the "Login" button
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                guardarPreferencies();
//                EditText u = Login.this.findViewById(R.id.login_username);
//                EditText p = Login.this.findViewById(R.id.login_password);

                EditText u = Login.this.findViewById(R.id.editTextUsernameLogin);
                EditText p = Login.this.findViewById(R.id.editTextPasswordLogin);

                TextInputLayout username = Login.this.findViewById(R.id.UsernameLogin);
                TextInputLayout password = Login.this.findViewById(R.id.PasswordLogin);

                username.setError(null);
                password.setError(null);

                if(u.length() == 0){
                    username.setError("Username/email can't be empty");
                }
                else if(p.length() == 0) {
                    password.setError("Password can't be empty");
                }
                else {
                    Login.this.checkCredentials(u.getText().toString(), p.getText().toString());
                }
            }
        });

    }

    // This method is called when the "Login" button is pressed in the Login fragment
    public void checkCredentials(String username, String password) {
        UserLogin ul = new UserLogin();
        ul.username = username;
        ul.password = password;
        ul.deviceId = deviceId;
        Call<User> call = mTodoService.login(ul);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    Login.this.startActivity(new Intent(Login.this, NavigationActivity.class));
                    Login.this.finish();
                } else{
                    TextInputLayout username = Login.this.findViewById(R.id.PasswordLogin);
                    username.setError("Password and username/email didn't match");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast toast = Toast.makeText(Login.this, "Error logging in", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, ChooseRegisterLogin.class);
        startActivity(intent);
        Login.this.finish();
    }


    private void guardarPreferencies(){
        SharedPreferences.Editor editor = getSharedPreferences("credencials", MODE_PRIVATE).edit();
        editor.putString("name", campUsuari.getText().toString());
        editor.apply();
    }

}
