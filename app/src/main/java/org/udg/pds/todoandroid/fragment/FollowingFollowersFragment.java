package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.udg.pds.todoandroid.R;

import org.udg.pds.todoandroid.fragment.SectionsPagerAdapter;

public class FollowingFollowersFragment extends Fragment {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private String selectedTab;
    Context context;

    public FollowingFollowersFragment(){

    }

    public static FollowingFollowersFragment newInstance(String param1, String param2) {
        FollowingFollowersFragment fragment = new FollowingFollowersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        //setRetainInstance(false);
        setRetainInstance(true);
        Log.v("Oncreatexd","On create de FollFoll");

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
      /*  Fragment fragment = (this.getChildFragmentManager().findFragmentById(R.id.tabsFollowingFollowers));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();*/
        Log.v("Ondestvxd","On destv de FollFoll");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v("Onresumexd","On resume de FollFoll");
    }

    private void setUpTabs(View rootView,String selectedTab){

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getParentFragmentManager());
        adapter.addFragment(new FollowersFragment(), "Followers");
        adapter.addFragment(new FollowingFragment(), "Following");

        ViewPager viewPager = rootView.findViewById(R.id.view_pager_following_followers);
        viewPager.setAdapter(adapter);
        TabLayout tabs = rootView.findViewById(R.id.tabsFollowingFollowers);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setText("Followers");
        tabs.getTabAt(1).setText("Following");
        if(selectedTab.equals("following"))
            tabs.getTabAt(1).select();

        Log.v("setupxd","Setup de FollFoll");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_following_followers, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();

        selectedTab = getArguments().getString("type");

        setUpTabs(rootView,selectedTab);
        Log.v("Oncreatevxd","On createv de FollFoll");

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
