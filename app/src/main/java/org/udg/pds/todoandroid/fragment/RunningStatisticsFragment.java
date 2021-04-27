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

public class RunningStatisticsFragment extends Fragment {

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
         workoutsSetmana.setText("5");
         TextView durationSetmana = root.findViewById(R.id.textView14);
         durationSetmana.setText("6" + "h");
         TextView distanceSetmana = root.findViewById(R.id.textView16);
         distanceSetmana.setText("2500" + "m");

         TextView workoutsAny = root.findViewById(R.id.textView18);
         workoutsAny.setText("15");
         TextView durationAny = root.findViewById(R.id.textView20);
         durationAny.setText("36" + "h");
         TextView distanceAny = root.findViewById(R.id.textView22);
         distanceAny.setText("45000" + "m");

         TextView workoutsTotal = root.findViewById(R.id.textView24);
         workoutsTotal.setText("15");
         TextView durationTotal = root.findViewById(R.id.textView26);
         durationTotal.setText("36" + "h");
         TextView distanceTotal = root.findViewById(R.id.textView28);
         distanceTotal.setText("45000" + "m");
//         final TextView textView = root.findViewById(R.id.section_label);
//         textView.setText("Pantalla running");
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
