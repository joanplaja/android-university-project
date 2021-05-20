package org.udg.pds.todoandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Objective;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.fragment.ObjectivesDialogFragment;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjectiveCreateActivity extends AppCompatActivity implements ObjectivesDialogFragment.OnSetTitleListener{
    TodoApi mTodoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_objective);
        mTodoService = ((TodoApp) this.getApplication()).getAPI();
        //registerForContextMenu(findViewById(R.id.viewMenu));

//        findViewById(R.id.createEquipmentImageButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
//            }
//        });


        Button objectiveType = (Button) findViewById(R.id.objectiveTypeButton);
        String defaultTextButton = objectiveType.getText().toString().toLowerCase();
        objectiveType.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        EditText goalEt = ObjectiveCreateActivity.this.findViewById(R.id.editTextGoal);
        Button addObjectiveButton = (Button) findViewById(R.id.createObjectiveButton);
        addObjectiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double goal = Double.parseDouble(goalEt.getText().toString());
                String objectiveTypeString = objectiveType.getText().toString().toLowerCase();
                System.out.println("eeeeeeeee: " + defaultTextButton);
                System.out.println("rrererere: " + objectiveTypeString);
                if(!objectiveTypeString.equals(defaultTextButton)) {
                    createObjective(objectiveTypeString, goal);
                }
                else{
                    Toast toast = Toast.makeText(ObjectiveCreateActivity.this, "Select a type", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    public void openDialog(){
        ObjectivesDialogFragment dialog = new ObjectivesDialogFragment();
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void setTitle(String title) {
        Button objectiveType = (Button) findViewById(R.id.objectiveTypeButton);
        objectiveType.setText(title);
    }

    public void createObjective(String type, double goal){
        Objective o = new Objective();
        o.type = type;
        o.goal = goal;


        Call<IdObject> call = mTodoService.addObjective(o);
        call.enqueue(new Callback<IdObject>() {
            @Override
            public void onResponse(Call<IdObject> call, Response<IdObject> response) {

                if (response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ObjectiveCreateActivity.this);
                    builder.setMessage("Objective Created!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ObjectiveCreateActivity.this.finish();
                            }
                        });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    Toast toast = Toast.makeText(ObjectiveCreateActivity.this, "Something is wrong onResponse", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<IdObject> call, Throwable t) {
                Toast toast = Toast.makeText(ObjectiveCreateActivity.this, "Error in the register", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}

