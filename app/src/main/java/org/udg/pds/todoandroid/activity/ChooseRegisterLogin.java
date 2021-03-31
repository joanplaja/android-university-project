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
                ChooseRegisterLogin.this.finish();
            }
        });

        Button b2 = findViewById(R.id.buttonChooseRegister);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseRegisterLogin.this, Register.class));
                ChooseRegisterLogin.this.finish();
            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
