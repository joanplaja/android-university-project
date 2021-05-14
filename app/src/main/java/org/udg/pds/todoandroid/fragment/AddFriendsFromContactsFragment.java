package org.udg.pds.todoandroid.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.FindPhoneFriends;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendsFromContactsFragment extends Fragment {

    LinearLayout loadingLyt,contactLayout,noContactLayout;
    RecyclerView recyclerView;
    private static final int PERMISSIONS_REQUEST_CONTACT = 1;  //constant for check if user has accepted location permissions

    private ContactAddFriendsRecyclerViewAdapter adapter;
    //recycler view

    private List<User> friends;
    List<String> phoneNumbers;
    Context context;

    TodoApi mTodoService;

    public AddFriendsFromContactsFragment() {

    }

    public static AddFriendsFromContactsFragment newInstance(String param1, String param2) {
        AddFriendsFromContactsFragment fragment = new AddFriendsFromContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_friends_from_contacts, container, false);
        context = rootView.getContext();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        friends = new ArrayList<User>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.contactFriendsRecyclerView);
        loadingLyt =            rootView.findViewById(R.id.contactFriendsLoadingLyt);
        contactLayout =     rootView.findViewById(R.id.contactFriendsToAdd);
        noContactLayout =     rootView.findViewById(R.id.NoContactFriendsToAdd);

        getContactsPermissions();



        return rootView;
    }

    public void loadContactNumbers(){
        Cursor contacts = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        );
        phoneNumbers = new ArrayList<>();
        System.out.println("length:"+contacts.getCount());
        while (contacts.moveToNext())
        {
            String phoneNumber = contacts.getString(
                contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            //de moment ho elimino seria millor fer que a la bd es guardin strings amb els +
            String replace = phoneNumber.replaceAll("\\+[0-9][0-9]","");
            replace = replace.replaceAll("\\+34","");
            replace = replace.replaceAll("[\\+,\\),\\(,\\-]","");
            replace = replace.replaceAll("\\s","");
            System.out.println("r: "+replace);
            if(replace.length()==9)phoneNumbers.add(replace);
        }
        contacts.close();

        String phoneNumbersArray[] = new String[phoneNumbers.size()];
        for(int i=0;i<phoneNumbers.size();i++)phoneNumbersArray[i] = phoneNumbers.get(i);


        FindPhoneFriends fPfriends = new FindPhoneFriends();
            fPfriends.phoneNumbers = phoneNumbersArray;
        Call<List<User>> call = mTodoService.findPhoneFriends(fPfriends);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                friends = response.body();

                if(friends.size()==0) {
                    loadingLyt.setVisibility(View.GONE);
                    noContactLayout.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    System.out.println("quants:" + friends.size());

                    adapter = new ContactAddFriendsRecyclerViewAdapter(context, mTodoService);
                    recyclerView.setAdapter(adapter);
                    adapter.setContactList(friends);

                    loadingLyt.setVisibility(View.GONE);
                    contactLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CONTACT) {
            if (!Arrays.asList(grantResults).contains(PackageManager.PERMISSION_DENIED)) {
                //all permissions have been granted
                loadContactNumbers(); //call your dependent logic
            }
        }
    }

    //Request contact permission, so that we can get the contacts of the device. The result of the permission request is handled by a callback, onRequestPermissionsResult.
    private void getContactsPermissions() {
        Log.d("tag:","getContact");
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
            Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED) {
            loadContactNumbers();
        } else {
            requestPermissions(
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSIONS_REQUEST_CONTACT);
        }
    }

}
