package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    TodoApi mTodoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.register);
        super.onCreate(savedInstanceState);

        mTodoService = ((TodoApp) this.getApplication()).getAPI();

        Button b = findViewById(R.id.buttonRegister);
        // This is teh listener that will be used when the user presses the "Login" button
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText username = Register.this.findViewById(R.id.editTextUsername);
                EditText email = Register.this.findViewById(R.id.editTextEmailAddress);
                EditText password = Register.this.findViewById(R.id.editTextPassword);
                EditText phone = Register.this.findViewById(R.id.editTextPhone2);
                if(username.length() == 0){
                    username.setError("Username can't be empty");
                }
                else if(email.length() == 0) {
                    email.setError("Email can't be empty");
                }
                else if(!isValidEmail(email)){
                    email.setError("Enter a valid email!");
                }
                else if(password.length() == 0) {
                    email.setError("Password can't be empty");
                }
                else if(phone.length() == 0) {
                    phone.setError("Phone number can't be empty");
                }
                else {
                    Register.this.register(username.getText().toString(), email.getText().toString(), password.getText().toString(), phone.getText().toString());
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
        UserRegister ur = new UserRegister();
        ur.username = username;
        ur.email = email;
        ur.password = password;
        ur.phoneNumber = phone;
        Call<String> call = mTodoService.register(ur);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Register.this.startActivity(new Intent(Register.this, NavigationActivity.class));
                    Register.this.finish();

                }
                else{
                    EditText mEditText  = (EditText ) findViewById(R.id.editTextUsername);
                    mEditText.setError("Username or email already registered");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(Register.this, "Error in the register", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, ChooseRegisterLogin.class);
        startActivity(intent);
        Register.this.finish();
    }

}
