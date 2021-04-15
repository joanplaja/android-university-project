package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.DictionaryImages;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Workout Details: ";

    DictionaryImages dictionaryImages = new DictionaryImages();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TodoApi mTodoService;
    private Long id;
    private Workout workout;
    public WorkoutDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutDetailsFragment newInstance(String param1, String param2) {
        WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        id = getArguments().getLong("id");
        Log.i(TAG, id.toString());
        View v = inflater.inflate(R.layout.fragment_workout_details, container, false);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
    }

    public void loadDetails() {
        Call<Workout> call = mTodoService.getWorkout(id.toString());

        call.enqueue(new Callback<Workout>() {
            @Override
            public void onResponse(Call<Workout> call, Response<Workout> response) {
                if (response.isSuccessful()) {
                    //Emplenem el workout amb el que rebem de la crida.
                    workout = response.body();
                    //Recuperem els camps que els interessen i els guardem a variables.
                    String type = workout.type;
                    Integer icon = dictionaryImages.images.get(type);
                    Double initialLatitude = workout.route.initialLatitude;
                    Double initialLongitude = workout.route.initialLongitude;
                    Double lastLatitude;
                    Double lastLongitude;
                    if(workout.route.points.size() != 0) {
                        lastLatitude = workout.route.points.get(workout.route.points.size()-1).latitude;
                        lastLongitude = workout.route.points.get(workout.route.points.size()-1).longitude;
                    } else {
                        lastLatitude = 0.0;
                        lastLongitude = 0.0;
                    }
                    Double latitudeDiff = lastLatitude - initialLatitude;
                    Double longitudeDiff = lastLongitude - initialLongitude;

                    //Emplenem els valors de les Views
                    TextView typeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.type);
                    typeView.setText(type.toUpperCase());

                    ImageView iconView = WorkoutDetailsFragment.this.getView().findViewById(R.id.icon);
                    iconView.setImageResource(icon);

                    TextView initialLatitudeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.initialLatitude);
                    initialLatitudeView.setText(initialLatitude.toString());

                    TextView initialLongitudeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.initialLongitude);
                    initialLongitudeView.setText(initialLongitude.toString());

                    TextView lastLatitudeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.lastLatitude);
                    lastLatitudeView.setText(lastLatitude.toString());

                    TextView lastLongitudeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.lastLongitude);
                    lastLongitudeView.setText(lastLongitude.toString());

                    TextView latitudeDiffView = WorkoutDetailsFragment.this.getView().findViewById(R.id.latitudeDiff);
                    if(latitudeDiff > 0) {
                        //hem anat cap a l'est
                        latitudeDiffView.setText(latitudeDiff.toString() + " East");
                    } else {
                        //hem anat cap a l'oest
                        latitudeDiff = -latitudeDiff;
                        latitudeDiffView.setText(latitudeDiff.toString() + " West");
                    }

                    TextView longitudeDiffView = WorkoutDetailsFragment.this.getView().findViewById(R.id.longitudeDiff);
                    if(longitudeDiff > 0) {
                        //hem anat cap al nord
                        longitudeDiffView.setText(longitudeDiff.toString() + " North");
                    } else {
                        //hem anat cap al sud
                        longitudeDiff = -longitudeDiff;
                        longitudeDiffView.setText(longitudeDiff.toString() + " South");
                    }

                } else {
                    Toast.makeText(WorkoutDetailsFragment.this.getContext(), "Error reading specific Workout", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Workout> call, Throwable t) {
                Toast.makeText(WorkoutDetailsFragment.this.getContext(), "Error making call", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.loadDetails();
    }
}
