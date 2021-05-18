package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileFragment extends Fragment {
    TodoApi mTodoService;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    Context context;
    private Long uId;
    View rootView;

    public interface GetCallbacks{
        void onSuccess(Long id, boolean alreadyFollowing);
        void onUserFailed(Throwable error);
    }

    public static ViewProfileFragment newInstance(String param1, String param2){
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        context =this.getContext();
        mParam1 = getArguments().getString("username");
        loadProfile(mParam1,rootView, new GetCallbacks() {
            @Override
            public void onSuccess(Long id, boolean alreadyFollowing) {
                uId = id;
                Button followButton;
                followButton = rootView.findViewById(R.id.viewProfileFollowButton);
                if(alreadyFollowing){
                    followButton.setText("Following");
                }
                else {
                    followButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Call<String> call = mTodoService.followUser(id);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, "Successfully followed", Toast.LENGTH_SHORT).show();
                                        followButton.setText("Following");
                                        updateProfile();

                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(context, "Couldn't Follow", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                Button unfollowButton;
                unfollowButton = rootView.findViewById(R.id.viewProfileUnfollowButton);
                unfollowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Call <String> call = mTodoService.unfollowUser(id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Successfully unfollowed", Toast.LENGTH_SHORT).show();
                                    followButton.setText("Follow");
                                    updateProfile();

                                }
                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(context, "Couldn't unFollow", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                });
                CardView cardFollowers, cardFollowing;
                cardFollowers = rootView.findViewById(R.id.viewProfileCardFollowers);
                cardFollowers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavDirections action =
                            ViewProfileFragmentDirections
                                .actionViewProfileFragmentToSocial("followers",uId);
                        Navigation.findNavController(v).navigate(action);
                    }
                });

                cardFollowing = rootView.findViewById(R.id.viewProfileCardFollowing);
                cardFollowing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavDirections action =
                            ViewProfileFragmentDirections
                                .actionViewProfileFragmentToSocial("following",uId);
                        Navigation.findNavController(v).navigate(action);
                    }
                });
            }

            @Override
            public void onUserFailed(Throwable error) {
                Toast.makeText(context, "Error Loading Profile", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }

    public void updateProfile(){
        Call<List<User>> callFollowing = mTodoService.getFollowing(uId);
        callFollowing.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> callFollowing, Response<List<User>> responseFollowing) {
                TextView viewProfileFollowing = rootView.findViewById(R.id.viewProfileFollowingNumber);
                viewProfileFollowing.setText(String.valueOf(responseFollowing.body().size()));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
        Call<List<User>> callFollowers = mTodoService.getFollowers(uId);
        callFollowers.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> callFollowers, Response<List<User>> responseFollowers) {
                TextView viewProfileFollowers = rootView.findViewById(R.id.viewProfileFollowersNumber);
                viewProfileFollowers.setText(String.valueOf(responseFollowers.body().size()));
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t){

            }
        });
    }

    public void loadProfile(String usernameArg,View rootView,final GetCallbacks getCallbacks) {
        //android todoApi (retrofit) -> Spring controller (retorna resposta http) -> onResponse i la processem.
        //response.body() es tipo user
        Call <User> callMe = mTodoService.getUserMe();
        callMe.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> callMe, Response<User> response) {

            if(usernameArg.equals(response.body().username)){
                NavDirections action =
                    ViewProfileFragmentDirections
                        .actionViewProfileFragmentToUserProfileFragment();
                Navigation.findNavController(rootView).navigate(action);
            }
        else {

            Call<Long> call = mTodoService.getIdByUsername(usernameArg);

            call.enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    if (response.isSuccessful()) {

                        Call<User> callUser = mTodoService.getUser(response.body());
                        callUser.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> callUser, Response<User> responseUser) {
                                if (responseUser.isSuccessful()) {
                                    TextView viewProfileName = rootView.findViewById(R.id.viewProfileName);
                                    viewProfileName.setText(responseUser.body().username);
                                    TextView viewProfileDescription = rootView.findViewById(R.id.viewProfileDescription);
                                    viewProfileDescription.setText(responseUser.body().description);
                                    ImageView viewProfileImage = ViewProfileFragment.this.getView().findViewById(R.id.viewProfileImage);
                                    if (responseUser.body().imageUrl != null) {
                                        //File f = new File(response.body().imageUrl);
                                        Picasso.get().load(responseUser.body().imageUrl).fit().centerCrop().into(viewProfileImage);
                                    } else
                                        viewProfileImage.setImageResource(R.drawable.profile_photo);
                                } else {
                                    //Toast.makeText(ViewProfileFragment.this.getContext(), "Error reading tasks", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> callUser, Throwable t) {

                            }
                        });

                        Call<List<User>> callFollowing = mTodoService.getFollowing(response.body());
                        callFollowing.enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> callFollowing, Response<List<User>> responseFollowing) {
                                TextView viewProfileFollowing = rootView.findViewById(R.id.viewProfileFollowingNumber);
                                viewProfileFollowing.setText(String.valueOf(responseFollowing.body().size()));
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });

                        Call<User> callGetMe = mTodoService.getUserMe();
                        callGetMe.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> callGetMe, Response<User> responseGetMe) {
                                Call<List<User>> callFollowers = mTodoService.getFollowers(response.body());
                                callFollowers.enqueue(new Callback<List<User>>() {
                                    @Override
                                    public void onResponse(Call<List<User>> callFollowers, Response<List<User>> responseFollowers) {
                                        TextView viewProfileFollowers = rootView.findViewById(R.id.viewProfileFollowersNumber);
                                        viewProfileFollowers.setText(String.valueOf(responseFollowers.body().size()));
                                        boolean found = false;
                                        int i = 0;
                                        while (!found && i < responseFollowers.body().size()) {
                                            if (responseFollowers.body().get(i).username.equals(responseGetMe.body().username))
                                                found = true;
                                            i++;
                                        }
                                        if (found) {
                                            getCallbacks.onSuccess(response.body(), true);
                                        } else {
                                            getCallbacks.onSuccess(response.body(), false);
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<List<User>> call, Throwable t) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<User> callGetMe, Throwable t) {

                            }
                        });


                    } else {
                        //Toast.makeText(ViewProfileFragment.this.getContext(), "Error reading tasks", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {

                }
            });
        }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }


        });

    }
}
