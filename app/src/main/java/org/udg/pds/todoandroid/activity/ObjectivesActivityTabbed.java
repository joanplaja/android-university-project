package org.udg.pds.todoandroid.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.fragment.ObjectivesFragment;
import org.udg.pds.todoandroid.fragment.SectionsPagerAdapter;
import org.udg.pds.todoandroid.fragment.CyclingStatisticsFragment;
import org.udg.pds.todoandroid.fragment.HikingStatisticsFragment;
import org.udg.pds.todoandroid.fragment.RunningStatisticsFragment;
import org.udg.pds.todoandroid.fragment.WalkingStatisticsFragment;

public class ObjectivesActivityTabbed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        setUpTabs();
    }

    private void setUpTabs(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ObjectivesFragment(), "Weekly");
        adapter.addFragment(new ObjectivesFragment(), "Monthly");
        //adapter.addFragment(new CyclingStatisticsFragment(), "Cycling");
        //adapter.addFragment(new HikingStatisticsFragment(), "Hiking");
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabs);
        TextView title = findViewById(R.id.title);
        title.setText("Objectives");
        tabs.setupWithViewPager(viewPager);
//        tabs.getTabAt(0).setIcon(R.drawable.running);
//        tabs.getTabAt(1).setIcon(R.drawable.walking);
//        tabs.getTabAt(2).setIcon(R.drawable.cycling);
//        tabs.getTabAt(3).setIcon(R.drawable.hiking);

    }
}
