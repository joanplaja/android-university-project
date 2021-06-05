package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.GraphicActivityTabbed;
import org.udg.pds.todoandroid.activity.ObjectiveCreateActivity;
import org.udg.pds.todoandroid.entity.Objective;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        updateObjectives();
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
        if (view.findViewById(R.id.listObjectives) instanceof RecyclerView) {
            Context context = view.findViewById(R.id.listObjectives).getContext();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listObjectives);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new ObjectivesRecyclerViewAdapter(this, mTodoService);
            recyclerView.setAdapter(adapter);
        }

        ImageButton botoObjectius = (ImageButton)view.findViewById(R.id.fab_add);
        botoObjectius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(getActivity(), ObjectiveCreateActivity.class);
                startActivity(I);
            }
        });

        return view;
    }

    private void updateObjectives() {
        Call<List<Objective>> call = mTodoService.getObjectives();

        call.enqueue(new Callback<List<Objective>>() {
            @Override
            public void onResponse(Call<List<Objective>> call, Response<List<Objective>> response) {
                if (response.isSuccessful()) {
                    mValues = response.body();
                    ObjectivesFragment.this.showObjectivesList(mValues);
                } else {
                    Toast.makeText(ObjectivesFragment.this.getContext(), "Error reading objectives", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Objective>> call, Throwable t) {
                Toast.makeText(ObjectivesFragment.this.getContext(), "Error making call", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void showObjectivesList(List<Objective> objectives) {
        adapter.setObjectives(objectives);
    }

    public void onBackPressed(){
        NavDirections action =
            ObjectivesFragmentDirections.actionObjectivesFragmentToUserProfileFragment();
        Navigation.findNavController(getView()).navigate(action);
    }

    @Override
    public void onObjectiveClick(int position) {
    }
}
