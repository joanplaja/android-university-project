package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import org.apache.commons.io.IOUtils;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.entity.UserUpdate;
import org.udg.pds.todoandroid.fragment.ImageFragment;
import org.udg.pds.todoandroid.fragment.UserProfileFragment;
import org.udg.pds.todoandroid.fragment.UserProfileFragmentDirections;
import org.udg.pds.todoandroid.rest.TodoApi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "";
    EditText userNameET, userDescriptionET, userPhoneET;
    TextInputLayout userNameIL, userDescriptionIL, userPhoneIL;
    ImageView userImageTV;
    Uri selectedImageUri = null;
    String newUserPhotoUri;

    TodoApi mTodoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();

        findViewById(R.id.updateProfileImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        userNameET = findViewById(R.id.updateProfileName);
        userDescriptionET = findViewById(R.id.updateProfileDescription);
        userPhoneET = findViewById(R.id.updateProfilePhoneNumber);

        userNameIL = findViewById(R.id.updateProfileNameIL);
        userDescriptionIL = findViewById(R.id.updateProfileDescriptionIL);
        userPhoneIL = findViewById(R.id.updateProfilePhoneNumberIL);
        userImageTV = findViewById(R.id.updateProfileImage);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String dataUserNameET = extras.getString("dataUserNameUPF");
        String dataUserDescriptionET = extras.getString("dataUserDescriptionUPF");
        String dataUserPhoneET = extras.getString("dataUserPhoneUPF");
        String dataUserImageUrl = extras.getString("dataUserImageUrlUPF");

        userNameET.setText(dataUserNameET);
        userDescriptionET.setText(dataUserDescriptionET);
        userPhoneET.setText(dataUserPhoneET);
        if(dataUserImageUrl!=null) {
            Picasso.get().load(dataUserImageUrl).fit().centerCrop().into(userImageTV);
        }
        else
            userImageTV.setImageResource(R.drawable.profile_photo);

        Button eb = findViewById(R.id.updateProfileNewEquipmentButton);
        eb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(getApplicationContext(), EquipmentCreateActivity.class);
                startActivity(I);
            }
        });

        Button b = findViewById(R.id.updateProfileSaveButton);
        // This is the listener that will be used when the user presses the "Save" button
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                if (selectedImageUri != null) {
                    try {
                        InputStream is = getContentResolver().openInputStream(selectedImageUri);
                        String extension = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(selectedImageUri));
                        File tempFile = File.createTempFile("upload", extension, getCacheDir());
                        FileOutputStream outs = new FileOutputStream(tempFile);
                        IOUtils.copy(is, outs);
                        // create RequestBody instance from file
                        RequestBody requestFile =
                            RequestBody.create(
                                MediaType.parse(getContentResolver().getType(selectedImageUri)),
                                tempFile
                            );

                        // MultipartBody.Part is used to send also the actual file name
                        MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

                        Call<String> call = mTodoService.uploadImage(body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    ImageView userImageSelected = findViewById(R.id.updateProfileImage);
                                    //Toast.makeText(UpdateProfileActivity.this, "Image uploaded OK !!"+response.body(), Toast.LENGTH_SHORT).show();
                                    Picasso.get().load(response.body()).fit().centerCrop().into(userImageSelected);
                                    newUserPhotoUri = response.body();

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
                                        updateProfile(userNameET.getText().toString(), userDescriptionET.getText().toString(), userPhoneET.getText().toString(), newUserPhotoUri);
                                    }
                                } else
                                    Toast.makeText(UpdateProfileActivity.this, "Response error !", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(UpdateProfileActivity.this, "Failure !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        /*userImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragTran = getSupportFragmentManager().beginTransaction();
                fragTran.replace(R.id.updateProfileId, new ImageFragment()).commit();
            }
        });*/
    }

    public void updateProfile(String username, String description, String phone, String imageUrl) {
        UserUpdate ur = new UserUpdate();
        ur.username = username;
        ur.description = description;
        ur.phoneNumber = Integer.parseInt(phone);
        ur.imageUrl = imageUrl;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView userImageSelected = findViewById(R.id.updateProfileImage);
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 1) {
            selectedImageUri = data.getData();
            userImageSelected.setImageURI(selectedImageUri);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }


}
