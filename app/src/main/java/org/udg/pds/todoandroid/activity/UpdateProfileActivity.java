package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.fragment.UserProfileFragment;
import org.udg.pds.todoandroid.rest.TodoApi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText userNameET, userDescriptionET, userPhoneET;
    TextInputLayout userNameIL, userDescriptionIL, userPhoneIL;
    ImageView userImage;
    TodoApi mTodoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();

        userImage = findViewById(R.id.updateProfileImage);
        Button b = findViewById(R.id.updateProfileSaveButton);
        // This is teh listener that will be used when the user presses the "Register" button
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                userNameET = findViewById(R.id.updateProfileName);
                userDescriptionET = findViewById(R.id.updateProfileDescription);
                userPhoneET = findViewById(R.id.updateProfilePhoneNumber);

                userNameIL = findViewById(R.id.updateProfileNameIL);
                userDescriptionIL = findViewById(R.id.updateProfileDescriptionIL);
                userPhoneIL = findViewById(R.id.updateProfilePhoneNumberIL);


                userNameIL.setError(null);
                userDescriptionIL.setError(null);
                userPhoneIL.setError(null);

                if(userNameET.length() == 0){
                    userNameIL.setError("Username can't be empty");
                }
                else if(userPhoneET.length() != 9) {
                    userPhoneIL.setError("Phone number is not in a good format");
                }
                else if(userDescriptionET.length() == 0) {
                    userDescriptionIL.setError("Description can't be empty");
                }
                else{
                    updateProfile(userNameET.getText().toString(), userDescriptionET.getText().toString(), userPhoneET.getText().toString());
                }
            }
        });

        /*
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateProfileActivity.this, "Click", Toast.LENGTH_LONG).show();
            }
        });*/
    }
    public void updateProfile(String username, String description, String phone) {
        User ur = new User();
        ur.username = username;
        ur.description = description;
        ur.phoneNumber = Integer.parseInt(phone);
        Call<IdObject> call = mTodoService.updateUserMe(ur);
        call.enqueue(new Callback<IdObject>() {
            @Override
            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                if (response.isSuccessful()) {
                    startActivity(new Intent(UpdateProfileActivity.this, NavigationActivity.class));
                    finish();

                }
                else{
                    Toast toast = Toast.makeText(UpdateProfileActivity.this, "Something is wrong", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<IdObject> call, Throwable t) {
                Toast toast = Toast.makeText(UpdateProfileActivity.this, "Something is wrong", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }
    public void onBackPressed() {
        super.onBackPressed();
    }


}
