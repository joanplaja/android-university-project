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

public class WalkingStatisticsFragment extends Fragment {

    TodoApi mTodoService;
    public View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_graphic, container, false);
        TextView workoutsSetmana = root.findViewById(R.id.textView12);
        workoutsSetmana.setText("25");
        TextView durationSetmana = root.findViewById(R.id.textView14);
        durationSetmana.setText("12" + "h");
        TextView distanceSetmana = root.findViewById(R.id.textView16);
        distanceSetmana.setText("2000" + "m");

        /*
     pageViewModel.getText().observe(this, new Observer<String>() {
     @Override
     public void onChanged(@Nullable String s) {
     textView.setText(s);
     }
     });*/
        return root;
    }
}
