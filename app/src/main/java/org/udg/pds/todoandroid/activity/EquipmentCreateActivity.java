package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Equipment;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.UserUpdate;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipmentCreateActivity extends AppCompatActivity {

    private static final String TAG = "";
    EditText equipmentNameET, equipmentDescriptionET, equipmentShopUrlET;
    TextInputLayout equipmentNameIL, equipmentDescriptionIL, equipmentShopUrlIL;
    ImageView equipmentImageTV;
    Uri selectedImageUri = null;
    String newEquipmentPhotoUri;

    TodoApi mTodoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_create);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();

        findViewById(R.id.createEquipmentImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        equipmentNameET = findViewById(R.id.createEquipmentName);
        equipmentDescriptionET = findViewById(R.id.createEquipmentDescription);
        equipmentShopUrlET = findViewById(R.id.createEquipmentShopUrl);

        equipmentNameIL = findViewById(R.id.createEquipmentNameIL);
        equipmentDescriptionIL = findViewById(R.id.createEquipmentDescriptionIL);
        equipmentShopUrlIL = findViewById(R.id.createEquipmentShopUrlIL);
        equipmentImageTV = findViewById(R.id.createEquipmentImage);

        /*Intent intent = getIntent();
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
            userImageTV.setImageResource(R.drawable.profile_photo);*/


        Button b = findViewById(R.id.createEquipmentSaveButton);
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
                                    ImageView equipmentImageSelected = findViewById(R.id.createEquipmentImage);
                                    //Toast.makeText(UpdateProfileActivity.this, "Image uploaded OK !!"+response.body(), Toast.LENGTH_SHORT).show();
                                    Picasso.get().load(response.body()).fit().centerCrop().into(equipmentImageSelected);
                                    newEquipmentPhotoUri = response.body();

                                    equipmentNameIL.setError(null);
                                    equipmentDescriptionIL.setError(null);
                                    equipmentShopUrlIL.setError(null);

                                    if(equipmentNameET.length() == 0){
                                        equipmentNameIL.setError("Equipment name can't be empty");
                                    }else if(equipmentShopUrlET.length() == 0) {
                                        equipmentShopUrlIL.setError("Shop Url can't be empty");
                                    }else if(!Patterns.WEB_URL.matcher(equipmentShopUrlET.getText().toString()).matches()) {
                                        equipmentShopUrlIL.setError("Shop Url is not correct. Example: https://website.es");
                                    }else if(equipmentDescriptionET.length() == 0) {
                                        equipmentDescriptionIL.setError("Description can't be empty");
                                    }
                                    else{
                                        createEquipment(equipmentNameET.getText().toString(), equipmentDescriptionET.getText().toString(), equipmentShopUrlET.getText().toString(), newEquipmentPhotoUri);
                                    }
                                } else
                                    Toast.makeText(EquipmentCreateActivity.this, "Response error !", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(EquipmentCreateActivity.this, "Failure !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(EquipmentCreateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EquipmentCreateActivity.this, "You need to select an image!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void createEquipment(String equipmentName, String equipmentDescription, String equipmentShopUrl, String imageUrl) {
        Equipment e = new Equipment();
        e.name = equipmentName;
        e.description = equipmentDescription;
        e.shopUrl = equipmentShopUrl;
        e.imageUrl = imageUrl;
        Call<IdObject> call = mTodoService.addEquipment(e);
        call.enqueue(new Callback<IdObject>() {
            @Override
            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                if (response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EquipmentCreateActivity.this);
                    builder.setMessage("Equipment Created!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EquipmentCreateActivity.this.finish();
                            }
                        });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    Toast toast = Toast.makeText(EquipmentCreateActivity.this, "Something is wrong onResponse", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<IdObject> call, Throwable t) {
                Toast toast = Toast.makeText(EquipmentCreateActivity.this, "Something is wrong onFailure", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView equipmentImage = findViewById(R.id.createEquipmentImage);
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 1) {
            selectedImageUri = data.getData();
            equipmentImage.setImageURI(selectedImageUri);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
