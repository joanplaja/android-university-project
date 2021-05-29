package org.udg.pds.todoandroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.entity.UserRegisterFacebook;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    TodoApi mTodoService;
    Bundle extras;
    String facebookId = null;
    String facebookToken = null;
    String emailIntent = null;
    public String deviceId;
    private String TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.register);
        super.onCreate(savedInstanceState);

        mTodoService = ((TodoApp) this.getApplication()).getAPI();
        deviceId = ((TodoApp) this.getApplication()).getToken();

        EditText usernameET = Register.this.findViewById(R.id.editTextUsername);
        EditText emailET = Register.this.findViewById(R.id.editTextEmailAddress);
        EditText passwordET = Register.this.findViewById(R.id.editTextPassword);
        EditText confirmPasswordET = Register.this.findViewById(R.id.editTextConfirmPassword);
        EditText phoneET = Register.this.findViewById(R.id.editTextPhone);

        TextInputLayout username = Register.this.findViewById(R.id.Username);
        TextInputLayout email = Register.this.findViewById(R.id.Email);
        TextInputLayout password = Register.this.findViewById(R.id.Password);
        TextInputLayout confirmPassword = Register.this.findViewById(R.id.confirmPassword);
        TextInputLayout phone = Register.this.findViewById(R.id.phone);

        Button b = findViewById(R.id.buttonRegister);

        extras = getIntent().getExtras();
        if(extras != null){
            facebookId = extras.getString("facebookId");
            facebookToken = extras.getString("facebookToken");
            emailIntent = extras.getString("email");
            emailET.setText(emailIntent);
        }

        // This is teh listener that will be used when the user presses the "Register" button
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                username.setError(null);
                email.setError(null);
                password.setError(null);
                confirmPassword.setError(null);
                phone.setError(null);


                if(usernameET.length() == 0){
                    username.setError("Username can't be empty");
                }
                else if(emailET.length() == 0) {
                    email.setError("Email can't be empty");
                }
                else if(!isValidEmail(emailET)){
                    email.setError("Enter a valid email!");
                }
                else if(passwordET.length() == 0) {
                    email.setError("Password can't be empty");
                }
                else if(!(passwordET.getText().toString().equals(confirmPasswordET.getText().toString()))){
                    confirmPassword.setError("Passwords are different");
                }
                else if(phoneET.length() == 0) {
                    phone.setError("Phone number can't be empty");
                }
                else {
                    System.out.println(usernameET.getText());
                    System.out.println(emailET.getText());
                    System.out.println(passwordET.getText());
                    System.out.println(phoneET.getText());
                    Register.this.register(usernameET.getText().toString(), emailET.getText().toString(), passwordET.getText().toString(), phoneET.getText().toString());
                }
            }
        });
    }

    public boolean isValidEmail(EditText text){
        CharSequence email = text.getText().toString();
        return(Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }


    // This method is called when the "Login" button is pressed in the Login fragment
    public void register(String username, String email, String password, String phone) {

        if(extras == null){
            UserRegister ur = new UserRegister();
            ur.username = username;
            ur.email = email;
            ur.password = password;
            ur.phoneNumber = phone;
            ur.firstName = " ";
            ur.lastName = " ";
            ur.age=0;
            ur.deviceId = deviceId;
            Call<String> call = mTodoService.register(ur);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.isSuccessful()) {
                        Register.this.startActivity(new Intent(Register.this, NavigationActivity.class));
                        Register.this.finish();

                    }
                    else{
                        TextInputLayout username = Register.this.findViewById(R.id.Username);
                        username.setError("Username or email already registered");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast toast = Toast.makeText(Register.this, "Error in the register", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        else{
            UserRegisterFacebook ur = new UserRegisterFacebook();
            ur.username = username;
            ur.email = email;
            ur.password = password;
            ur.phoneNumber = phone;
            ur.firstName = " ";
            ur.lastName = " ";
            ur.age=0;
            ur.facebookId = facebookId;
            ur.facebookToken = facebookToken;
            ur.deviceId = deviceId;
            Call<String> call = mTodoService.registerFacebook(ur);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.isSuccessful()) {
                        Register.this.startActivity(new Intent(Register.this, NavigationActivity.class));
                        Register.this.finish();

                    }
                    else{
                        TextInputLayout username = Register.this.findViewById(R.id.Username);
                        username.setError("Username or email already registered");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast toast = Toast.makeText(Register.this, "Error in the register", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });


        }

    }

    public void onBackPressed() {
        Intent intent = new Intent(this, ChooseRegisterLogin.class);
        startActivity(intent);
        Register.this.finish();
    }

}
