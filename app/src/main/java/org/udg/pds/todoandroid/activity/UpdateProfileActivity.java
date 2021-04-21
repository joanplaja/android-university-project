package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.entity.UserUpdate;
import org.udg.pds.todoandroid.fragment.UserProfileFragment;
import org.udg.pds.todoandroid.rest.TodoApi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "";
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
        userNameET = findViewById(R.id.updateProfileName);
        userDescriptionET = findViewById(R.id.updateProfileDescription);
        userPhoneET = findViewById(R.id.updateProfilePhoneNumber);

        userNameIL = findViewById(R.id.updateProfileNameIL);
        userDescriptionIL = findViewById(R.id.updateProfileDescriptionIL);
        userPhoneIL = findViewById(R.id.updateProfilePhoneNumberIL);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String dataUserNameET = extras.getString("dataUserNameUPF");
        String dataUserDescriptionET = extras.getString("dataUserDescriptionUPF");
        String dataUserPhoneET = extras.getString("dataUserPhoneUPF");

        userNameET.setText(dataUserNameET);
        userDescriptionET.setText(dataUserDescriptionET);
        userPhoneET.setText(dataUserPhoneET);

        Button b = findViewById(R.id.updateProfileSaveButton);
        // This is the listener that will be used when the user presses the "Save" button
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

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
        UserUpdate ur = new UserUpdate();
        ur.username = username;
        ur.description = description;
        ur.phoneNumber = Integer.parseInt(phone);
        Call<String> call = mTodoService.updateUserMe(ur);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
                    builder.setMessage("Profile Updated!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                UpdateProfileActivity.this.finish();
                            }
                        });
                    AlertDialog alert = builder.create();
                    alert.show();
                    //startActivity(new Intent(UpdateProfileActivity.this, PopupActivity.class));
                }
                else{
                    Toast toast = Toast.makeText(UpdateProfileActivity.this, "Something is wrong onResponse", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(UpdateProfileActivity.this, "Something is wrong onFailure", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }
    public void onBackPressed() {
        super.onBackPressed();
    }


}
