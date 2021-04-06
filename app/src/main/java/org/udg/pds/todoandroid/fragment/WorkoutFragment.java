package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.fragment.WorkoutRecyclerViewAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.fragment.dummy.DummyContent;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class WorkoutFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private WorkoutRecyclerViewAdapter adapter;
    TodoApi mTodoService;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkoutFragment() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static WorkoutFragment newInstance(int columnCount) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        updateWorkouts();
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
        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new WorkoutRecyclerViewAdapter();
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private void updateWorkouts() {
        Call<List<Workout>> call = mTodoService.getWorkouts();

        call.enqueue(new Callback<List<Workout>>() {
            @Override
            public void onResponse(Call<List<Workout>> call, Response<List<Workout>> response) {
                if (response.isSuccessful()) {
                    WorkoutFragment.this.showWorkoutList(response.body());
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

    private void showWorkoutList(List<Workout> workouts) {
        adapter.setWorkouts(workouts);
    }
}
