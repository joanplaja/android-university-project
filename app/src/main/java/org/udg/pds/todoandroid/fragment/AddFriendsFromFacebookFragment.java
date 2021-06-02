package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.ChooseRegisterLogin;
import org.udg.pds.todoandroid.activity.Register;
import org.udg.pds.todoandroid.entity.FindFacebookFriends;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddFriendsFromFacebookFragment extends Fragment {

    LinearLayout loadingLyt, loggedFacebookLyt, notLoggedFacebookLyt;
    RecyclerView recyclerView;

    private UserAddFriendsRecyclerViewAdapter adapter;

    private List<User> candidatesFirends;
    Context context;

    TodoApi mTodoService;

    public AddFriendsFromFacebookFragment() {
    }

    public static AddFriendsFromFacebookFragment newInstance(String param1, String param2) {
        AddFriendsFromFacebookFragment fragment = new AddFriendsFromFacebookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        System.out.println("onCreate facebook");
        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("on Start facebook");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("on resume facebook");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreated add friends from facebook");
        View rootView = inflater.inflate(R.layout.fragment_add_friends_from_facebook, container, false);
        context = rootView.getContext();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        candidatesFirends = new ArrayList<User>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.facebookFriendsRecyclerView);
        loadingLyt =            rootView.findViewById(R.id.loadingLyt);
        loggedFacebookLyt =     rootView.findViewById(R.id.loggedWithFacebook);
        notLoggedFacebookLyt =  rootView.findViewById(R.id.notLoggedWithFacebook);

        if(AccessToken.getCurrentAccessToken() == null){
            loadingLyt.setVisibility(View.GONE);
            notLoggedFacebookLyt.setVisibility(View.VISIBLE);

        }
        else{

            GraphRequestAsyncTask request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                @Override
                public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                    System.out.println("resposta facebook friends:"+jsonArray.toString()+"//"+graphResponse.toString());
                    //PASSAR UN ARRAY D'IDS I QUE RETORNI UNA ARRAY D'USUARIS AMICS DE FACEBOOK ELS QUALS ESTIGUIN A LA BD I NO SIGUIN JA AMICS
                    String facebookIds[] = new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        try {
                            facebookIds[i] = jsonArray.getJSONObject(i).getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    FindFacebookFriends fFF = new FindFacebookFriends();
                    fFF.facebookIds = facebookIds;

                    Call<List<User>> call = mTodoService.findFacebookFriends(fFF);
                    call.enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                            candidatesFirends = response.body();

                            if(candidatesFirends.size()==0)recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            else recyclerView.setLayoutManager(new GridLayoutManager(context,jsonArray.length()));

                            System.out.println("quants:"+candidatesFirends.size());

                            adapter = new UserAddFriendsRecyclerViewAdapter(context,mTodoService);
                            recyclerView.setAdapter(adapter);
                            adapter.setFirendsList(candidatesFirends);

                            loadingLyt.setVisibility(View.GONE);
                            loggedFacebookLyt.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {

                        }
                    });

                }
            }).executeAsync();
        }


        return  rootView;
    }
}
