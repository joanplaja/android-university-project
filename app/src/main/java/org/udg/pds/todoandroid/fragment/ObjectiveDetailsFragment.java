package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObjectiveDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObjectiveDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Objective Details: ";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TodoApi mTodoService;
    private Long id;


    public ObjectiveDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ObjectiveDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ObjectiveDetailsFragment newInstance(String param1, String param2) {
        ObjectiveDetailsFragment fragment = new ObjectiveDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart(){
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        id = getArguments().getLong("id");
        Log.i(TAG, id.toString());
        View v = inflater.inflate(R.layout.fragment_objective_details, container, false);
        Button deleteButton;
        deleteButton = v.findViewById(R.id.buttonDeleteObjectiveConfirm);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDelete(id,v);
            }
        });
        Button returnButton;
        returnButton = v.findViewById(R.id.returnFromDeleteObjective);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action =
                    ObjectiveDetailsFragmentDirections.actionObjectiveDetailsFragmentToObjectivesFragment();
                Navigation.findNavController(getView()).navigate(action);
            }
        });

        return v;
    }

    private void handleDelete(Long id, View v) {
        Call<String> call = mTodoService.deleteObjective(id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    NavDirections action =
                        ObjectiveDetailsFragmentDirections.actionObjectiveDetailsFragmentToObjectivesFragment();
                                Navigation.findNavController(getView()).navigate(action);

                } else {
                    Toast.makeText(ObjectiveDetailsFragment.this.getContext(), "Error deleting the objective", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ObjectiveDetailsFragment.this.getContext(), "Error making call to delete the Workout", Toast.LENGTH_LONG).show();
            }
        });
    }
}
