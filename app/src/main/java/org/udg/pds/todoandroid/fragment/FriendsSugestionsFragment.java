package org.udg.pds.todoandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.SearchActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsSugestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsSugestionsFragment extends Fragment {

    public static FriendsSugestionsFragment newInstance(String param1, String param2) {
        FriendsSugestionsFragment fragment = new FriendsSugestionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_friends_sugestions, container, false);
    }

}
