package org.udg.pds.todoandroid.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.SignoutActivity;
import org.udg.pds.todoandroid.activity.EquipmentActivity;
import org.udg.pds.todoandroid.activity.UpdateProfileActivity;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

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
        Button openEquipmentButton, updateProfileButton, signOutButton;
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        //super.onCreate(savedInstanceState);
        signOutButton = v.findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = UserProfileFragmentDirections.actionUserProfileFragmentToSignoutActivity();
                Navigation.findNavController(view).navigate(action);
            }
        });
        openEquipmentButton = v.findViewById(R.id.userProfileButtonEquipment);
        openEquipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEquipmentActivity();
            }
        });
        updateProfileButton = v.findViewById(R.id.userProfileButtonUpdate);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateProfileActivity();
            }
        });
        return v;
    }
    public void openEquipmentActivity(){
        Intent intent = new Intent(getActivity(), EquipmentActivity.class);
        startActivity(intent);
    }
    public void openUpdateProfileActivity(){
        Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
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
                    Toast.makeText(UserProfileFragment.this.getContext(), "Error reading tasks", Toast.LENGTH_LONG).show();
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
