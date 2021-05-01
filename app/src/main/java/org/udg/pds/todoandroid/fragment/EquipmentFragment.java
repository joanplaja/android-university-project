package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.media.audiofx.DynamicsProcessing;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Equipment;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.fragment.dummy.DummyContent;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

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
            equipmentRecyclerViewAdapter = new EquipmentRecyclerViewAdapter();
            recyclerView.setAdapter(equipmentRecyclerViewAdapter);
        }
        return view;
    }


    private void updateEquipments(){

    }
}
