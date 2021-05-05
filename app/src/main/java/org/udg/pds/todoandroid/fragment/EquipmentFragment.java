package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Equipment;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.fragment.dummy.DummyContent;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class EquipmentFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private EquipmentRecyclerViewAdapter equipmentRecyclerViewAdapter;
    TodoApi mTodoService;
    private List<Equipment> mValues = new ArrayList<>();
    public View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EquipmentFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static EquipmentFragment newInstance(int columnCount) {
        EquipmentFragment fragment = new EquipmentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    public void gotoUrl(String link){
        Uri uri = Uri.parse(link);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_equipment_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            equipmentRecyclerViewAdapter = new EquipmentRecyclerViewAdapter(context);
            recyclerView.setAdapter(equipmentRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        updateEquipments();
    }

    private void updateEquipments() {
        Call<List<Equipment>> call = mTodoService.getEquipments();

        call.enqueue(new Callback<List<Equipment>>() {
            @Override
            public void onResponse(Call<List<Equipment>> call, Response<List<Equipment>> response) {
                if (response.isSuccessful()) {
                    mValues = response.body();
                    EquipmentFragment.this.showEquipmentList(mValues);
                } else {
                    Toast.makeText(EquipmentFragment.this.getContext(), "Error reading Equipments", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Equipment>> call, Throwable t) {
                Toast.makeText(EquipmentFragment.this.getContext(), "Error making call", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showEquipmentList(List<Equipment> equipments) {
        equipmentRecyclerViewAdapter.setEquipments(equipments);
    }
}
