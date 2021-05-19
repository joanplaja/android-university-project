package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.fragment.ObjectivesDialogFragment;
import org.udg.pds.todoandroid.rest.TodoApi;

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
        objectiveType.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDialog();
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

//    @Override
//    public boolean onCreateContextMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_objectives, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//
//        switch(item.getItemId()) {
//            case R.id.objective_duration:
//                return true;
//            case R.id.objective_distance:
//                return true;
//
//            case R.id.objective_workouts:
//                return true;
//
//        }
//        return super.onMenuItemSelected(featureId, item);
//    }

}

