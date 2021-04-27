package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

public class HikingStatisticsFragment extends Fragment {

    TodoApi mTodoService;
    public View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_graphic, container, false);
//        final TextView textView = root.findViewById(R.id.section_label);
//        textView.setText("Pantalla hiking");
     /*final TextView textView = root.findViewById(R.id.section_label);
     pageViewModel.getText().observe(this, new Observer<String>() {
     @Override
     public void onChanged(@Nullable String s) {
     textView.setText(s);
     }
     });*/
        return root;
    }
}

