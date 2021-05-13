package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Objective;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

public class ObjectivesFragment extends Fragment implements ObjectivesRecyclerViewAdapter.OnObjectiveListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ObjectivesRecyclerViewAdapter adapter;
    TodoApi mTodoService;
    private List<Objective> mValues = new ArrayList<>();
    public View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ObjectivesFragment() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ObjectivesFragment newInstance(int columnCount) {
        ObjectivesFragment fragment = new ObjectivesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        //updateObjectives();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_objectives_list, container, false);  //fragment_workout_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new ObjectivesRecyclerViewAdapter(this);
            recyclerView.setAdapter(adapter);
        }
        FloatingActionButton botoObjectius = view.findViewById(R.id.addObjective);
        botoObjectius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment addObjectiveFragment = new AddObjectiveFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.objectivesFragment, addObjectiveFragment);
                //ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }

    /*private void updateObjectives() {
        Call<List<Objective>> call = mTodoService.getObjectives();

        call.enqueue(new Callback<List<Workout>>() {
            @Override
            public void onResponse(Call<List<Workout>> call, Response<List<Workout>> response) {
                if (response.isSuccessful()) {
                    mValues = response.body();
                    WorkoutFragment.this.showWorkoutList(mValues);
                    //
                } else {
                    Toast.makeText(WorkoutFragment.this.getContext(), "Error reading Workouts", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Workout>> call, Throwable t) {
                Toast.makeText(WorkoutFragment.this.getContext(), "Error making call", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showWorkoutList(List<Objective> objectives) {
        adapter.setObjectives(objectives);
    }
*/
    @Override
    public void onObjectiveClick(int position) {
//        Long id = mValues.get(position).id;
//        Bundle bundle = new Bundle();
//        bundle.putLong("id", id);
//        Navigation.findNavController(view).navigate(R.id.action_actionWorkoutList_to_workoutDetailsFragment, bundle);
    }
}
