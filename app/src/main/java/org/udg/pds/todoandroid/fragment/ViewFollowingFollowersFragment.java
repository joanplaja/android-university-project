package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.udg.pds.todoandroid.R;

public class ViewFollowingFollowersFragment extends Fragment {
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private Long mParam2;*/
    private String selectedTab;
    Context context;

    public ViewFollowingFollowersFragment(){

    }

    public static ViewFollowingFollowersFragment newInstance(String param1, Long param2) {
        ViewFollowingFollowersFragment fragment = new ViewFollowingFollowersFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putLong(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* //setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getLong(ARG_PARAM2);

        }
        //setRetainInstance(false);
        setRetainInstance(true);*/

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

    }

    @Override
    public void onResume(){
        super.onResume();

    }

    private void setUpTabs(View rootView, String selectedTab,Long userId){

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getParentFragmentManager());
        adapter.addFragment(new ViewFollFragment("ers",userId), "Followers");
        adapter.addFragment(new ViewFollFragment("ing",userId), "Following");

        ViewPager viewPager = rootView.findViewById(R.id.view_pager_following_followers);
        viewPager.setAdapter(adapter);
        TabLayout tabs = rootView.findViewById(R.id.tabsFollowingFollowers);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setText("Followers");
        tabs.getTabAt(1).setText("Following");
        if(selectedTab.equals("following"))
            tabs.getTabAt(1).select();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_following_followers, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();
        //Log.v("Oncreatevxd", String.valueOf(mParam2));
        selectedTab = getArguments().getString("type");
        Long userId = getArguments().getLong("id");

        setUpTabs(rootView,selectedTab,userId);
        Log.v("Oncreatevxd","On createv de FollFoll");

        return rootView;
    }
}
