package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.GraphicActivityTabbed;
import org.udg.pds.todoandroid.fragment.SectionsPagerAdapter;

public class FriendsFragment extends Fragment {

    Context context;

    FloatingActionButton search;

    FriendsSugestionsFragment friendsSugestionsFragment;
    AddFriendsFromFacebookFragment addFriendsFromFacebookFragment;
    AddFriendsFromContactsFragment addFriendsFromContactsFragment;

    public FriendsFragment() {
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }

    }

    private void setUpTabs(View rootView){

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(friendsSugestionsFragment, "Sugestions");
        adapter.addFragment(addFriendsFromFacebookFragment, "Facebook");
        adapter.addFragment(addFriendsFromContactsFragment, "Contacts");
        ViewPager viewPager = rootView.findViewById(R.id.view_pager_friends);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        System.out.println("count:"+viewPager.getChildCount());
        TabLayout tabs = rootView.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(R.drawable.ic_baseline_person_24);
        tabs.getTabAt(1).setIcon(R.drawable.facebook);
        tabs.getTabAt(2).setIcon(R.drawable.ic_baseline_perm_contact_calendar_24);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        friendsSugestionsFragment = new FriendsSugestionsFragment();
        addFriendsFromContactsFragment = new AddFriendsFromContactsFragment();
        addFriendsFromFacebookFragment =  new AddFriendsFromFacebookFragment();

        // Inflate the layout for this fragment
        System.out.println("onCreated Friends Fragment");
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();

        search = (FloatingActionButton) rootView.findViewById(R.id.searchButton);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = FriendsFragmentDirections.actionFriendsFragmentToSearchUserFragment();
                Navigation.findNavController(v).navigate(action);
                /*Intent I = new Intent(getActivity(), SearchActivity.class);
                startActivity(I);*/
            }
        });

        setUpTabs(rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.searchfriend) {
           /* NavDirections action = FriendsFragmentDirections.actionFriendsFragmentToSearchUserFragment();
            Navigation.findNavController(v).navigate(action);*/
           /* Intent I = new Intent(getActivity(), SearchActivity.class);
            startActivity(I);
            return true;*/
        }

        return super.onOptionsItemSelected(item);
    }
}
