package org.udg.pds.todoandroid.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteOutOfMemoryException;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.SignoutActivity;
import org.udg.pds.todoandroid.activity.EquipmentActivity;
import org.udg.pds.todoandroid.activity.UpdateProfileActivity;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TodoApi mTodoService;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Button openEquipmentButton, updateProfileButton, signOutButton, workoutButton;
        Button graficButton;

        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        //super.onCreate(savedInstanceState);

        createGraphic(v);

        signOutButton = v.findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignOutActivity();
            }
        });
        openEquipmentButton = v.findViewById(R.id.userProfileButtonEquipment);
        openEquipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEquipmentActivity();
            }
        });
        updateProfileButton = v.findViewById(R.id.userProfileButtonUpdate);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass data to updateProfileActivity
                Bundle extras = new Bundle();
                TextView userNameUPF = v.findViewById(R.id.userProfileName);
                TextView userDescriptionUPF = v.findViewById(R.id.userProfileDescription);
                TextView userPhoneUPF = v.findViewById(R.id.userProfilePhone);

                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);

                extras.putString("dataUserNameUPF", userNameUPF.getText().toString());
                extras.putString("dataUserDescriptionUPF", userDescriptionUPF.getText().toString());
                extras.putString("dataUserPhoneUPF", userPhoneUPF.getText().toString());
                intent.putExtras(extras);

                startActivity(intent);
            }
        });
        workoutButton = v.findViewById(R.id.buttonWorkouts);
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action =
                    UserProfileFragmentDirections
                        .actionUserProfileFragmentToWorkoutList();
                Navigation.findNavController(v).navigate(action);
            }
        });

//        graficButton = v.findViewById(R.id.graficButton);
//        graficButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent I = new Intent(getActivity(), GraphicActivity.class);
//                startActivity(I);
//            }
//        });

        return v;
    }

    public void createGraphic(View v){
        //View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        BarChart chart = v.findViewById(R.id.barchart);

        ArrayList NoOfEmp = new ArrayList();

        NoOfEmp.add(new BarEntry(945f, 0));
        NoOfEmp.add(new BarEntry(1040f, 1));
        NoOfEmp.add(new BarEntry(1133f, 2));
        NoOfEmp.add(new BarEntry(1240f, 3));
        NoOfEmp.add(new BarEntry(1369f, 4));
        NoOfEmp.add(new BarEntry(1487f, 5));
        NoOfEmp.add(new BarEntry(1501f, 6));
        NoOfEmp.add(new BarEntry(1645f, 7));
        NoOfEmp.add(new BarEntry(1578f, 8));
        NoOfEmp.add(new BarEntry(1695f, 9));

        ArrayList year = new ArrayList();

        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "No Of Employee");
        chart.animateY(2000);
        BarData data = new BarData(bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);

        chart.setDrawGridBackground(true);
        chart.setDrawBorders(true);
        chart.setTouchEnabled(true);
        //chart.setPinchZoom(true);
        chart.setScaleYEnabled(false);
    }

    public void openEquipmentActivity(){
        Intent intent = new Intent(getActivity(), EquipmentActivity.class);
        startActivity(intent);
    }
    public void openUpdateProfileActivity(){
        Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
        startActivity(intent);
    }
    public void openSignOutActivity(){
        Intent intent = new Intent(getActivity(), SignoutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart(){
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
    }
    public void loadProfile() {
        //android todoApi (retrofit) -> Spring controller (retorna resposta http) -> onResponse i la processem.
        //response.body() es tipo user
        Call<User> call = mTodoService.getUserMe();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    TextView userProfileName = UserProfileFragment.this.getView().findViewById(R.id.userProfileName);
                    userProfileName.setText(response.body().username);
                    TextView userProfileSubName = UserProfileFragment.this.getView().findViewById(R.id.userProfileSubName);
                    userProfileSubName.setText(response.body().username);
                    TextView userProfileEmail = UserProfileFragment.this.getView().findViewById(R.id.userProfileEmail);
                    userProfileEmail.setText(response.body().email);
                    TextView userProfilePhone = UserProfileFragment.this.getView().findViewById(R.id.userProfilePhone);
                    userProfilePhone.setText(response.body().phoneNumber.toString());
                    TextView userProfileId = UserProfileFragment.this.getView().findViewById(R.id.userProfileId);
                    userProfileId.setText(response.body().id.toString());
                    TextView userProfileDescription = UserProfileFragment.this.getView().findViewById(R.id.userProfileDescription);
                    userProfileDescription.setText(response.body().description);
                } else {
                    Toast.makeText(UserProfileFragment.this.getContext(), "Error loading profile", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        this.loadProfile();
    }
}
