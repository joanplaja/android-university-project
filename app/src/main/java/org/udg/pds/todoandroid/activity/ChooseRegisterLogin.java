package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.rest.TodoApi;

public class ChooseRegisterLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TodoApi mTodoService;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_register_login);

        mTodoService = ((TodoApp) this.getApplication()).getAPI();

        Button b1 = findViewById(R.id.buttonChooseLogin);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseRegisterLogin.this, Login.class));
            }
        });
    }

    public void onBackPressed() {
        // No fer res
        // Fet per prohibir que després de fer logout puguis tornar enrere i tornar a entrar al compte
        // sense fer login
    }
}
