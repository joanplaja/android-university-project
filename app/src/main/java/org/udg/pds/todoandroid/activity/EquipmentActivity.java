package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.fragment.FavoritesFragmentDirections;
import org.udg.pds.todoandroid.fragment.UserProfileFragment;

public class EquipmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        UserProfileFragment fragment = new UserProfileFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.userEquipmentId, fragment).commit();

    }
}
