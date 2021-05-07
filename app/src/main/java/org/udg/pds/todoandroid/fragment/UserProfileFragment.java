package org.udg.pds.todoandroid.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.GraphicActivityTabbed;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.activity.ObjectivesActivityTabbed;
import org.udg.pds.todoandroid.activity.SignoutActivity;
import org.udg.pds.todoandroid.activity.EquipmentActivity;
import org.udg.pds.todoandroid.activity.UpdateProfileActivity;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

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
        Button graficButton, botoObjectius;

        CardView cardFollowers, cardFollowing;

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
        updateProfileButton = v.findViewById(R.id.userProfileButtonUpdate);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<User> call = mTodoService.getUserMe();

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            String userImageUrlUPF = response.body().imageUrl;

                            //Pass data to updateProfileActivity
                            Bundle extras = new Bundle();
                            TextView userNameUPF = v.findViewById(R.id.userProfileName);
                            TextView userDescriptionUPF = v.findViewById(R.id.userProfileDescription);
                            TextView userPhoneUPF = v.findViewById(R.id.userProfilePhone);

                            Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);

                            extras.putString("dataUserNameUPF", userNameUPF.getText().toString());
                            extras.putString("dataUserDescriptionUPF", userDescriptionUPF.getText().toString());
                            extras.putString("dataUserPhoneUPF", userPhoneUPF.getText().toString());
                            extras.putString("dataUserImageUrlUPF", userImageUrlUPF);
                            intent.putExtras(extras);

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

            }
        });
        workoutButton = v.findViewById(R.id.buttonWorkouts);
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = UserProfileFragmentDirections.actionUserProfileFragmentToWorkoutList();
                Navigation.findNavController(v).navigate(action);
            }
        });

        openEquipmentButton = v.findViewById(R.id.userProfileButtonEquipment);
        openEquipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action =
                    UserProfileFragmentDirections
                        .actionUserProfileFragmentToEquipmentList();
                Navigation.findNavController(v).navigate(action);
            }
        });

        graficButton = v.findViewById(R.id.buttonMoreStatistics);
        graficButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(getActivity(), GraphicActivityTabbed.class);
                startActivity(I);
            }
        });


        botoObjectius = v.findViewById(R.id.buttonObjectives);
        botoObjectius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NavDirections action = UserProfileFragmentDirections.actionUserProfileFragmentToObjectivesFragment();
//                Navigation.findNavController(v).navigate(action);
                Intent I = new Intent(getActivity(), ObjectivesActivityTabbed.class);
                startActivity(I);
            }
        });

        cardFollowers = v.findViewById(R.id.userProfileCardFollowers);
        cardFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action =
                    UserProfileFragmentDirections
                        .actionUserProfileFragmentToSocial("followers");
                Navigation.findNavController(v).navigate(action);
            }
        });

        cardFollowing = v.findViewById(R.id.userProfileCardFollowing);
        cardFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action =
                    UserProfileFragmentDirections
                        .actionUserProfileFragmentToSocial("following");
                Navigation.findNavController(v).navigate(action);
            }
        });



            /*setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action =
                    UserProfileFragmentDirections
                        .actionUserProfileFragmentToWorkoutList();
                Navigation.findNavController(v).navigate(action);
            }
        }); */


        return v;
    }

    public void createGraphic(View v){
        //View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        BarChart chart = v.findViewById(R.id.barchart);

        //Per crear el gràfic segons les dades de la bdd, haurem de buscar l'usuari, mirar tots els seus workouts, i fer un doble bucle?
        //per tal de que miri setmana per setmana quants workouts ha fet i que vagi sumant la distancia recorreguda i després aquest valor posar-lo al gràfic.
        // Per tant també serà necessari guardar la data de cada workout.

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        // gap of 2f
        entries.add(new BarEntry(5f, 70f));
        entries.add(new BarEntry(6f, 60f));

        BarDataSet set = new BarDataSet(entries, "Total distance");
        chart.animateY(2000);
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        set.setColor(Color.rgb(133,182,104));
        set.setDrawValues(false);
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh

        chart.setDrawGridBackground(true);
        chart.setDrawBorders(false);
        chart.setTouchEnabled(true);
        //chart.setPinchZoom(true);
        chart.setScaleYEnabled(false);
        //chart.setDrawValuesForWholeStack(true);
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
                    ImageView userProfileImage = UserProfileFragment.this.getView().findViewById(R.id.userProfileImage);
                    if(response.body().imageUrl!=null) {
                        //File f = new File(response.body().imageUrl);
                        Picasso.get().load(response.body().imageUrl).fit().centerCrop().into(userProfileImage);
                    }
                    else
                        userProfileImage.setImageResource(R.drawable.profile_photo);
                } else {
                    Toast.makeText(UserProfileFragment.this.getContext(), "Error loading profile", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        Call<List<User>> callFollowing = mTodoService.getOwnFollowing();
        callFollowing.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> callFollowing, Response<List<User>> responseFollowing) {
                TextView userProfileFollowing = UserProfileFragment.this.getView().findViewById(R.id.userProfileFollowingNumber);
                  userProfileFollowing.setText(String.valueOf(responseFollowing.body().size()));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        Call<List<User>> callFollowers = mTodoService.getOwnFollowers();
        callFollowers.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> callFollowers, Response<List<User>> responseFollowers) {
                TextView userProfileFollowers = UserProfileFragment.this.getView().findViewById(R.id.userProfileFollowersNumber);
                if(responseFollowers != null)userProfileFollowers.setText(String.valueOf(responseFollowers.body().size()));
                else userProfileFollowers.setText("0");
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        this.loadProfile();
    }
}
