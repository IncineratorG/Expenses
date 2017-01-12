package com.example.newcosts;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivityWithFragments extends AppCompatActivity {

    private int PREVIOUS_ACTIVITY_INDEX = -1;
    private int TARGET_TAB = -1;
    private String savedValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_fragments);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setTitle("Мои расходы");
        setSupportActionBar(toolbar);

        final TabLayout mainActivityTabLayout = (TabLayout) findViewById(R.id.activity_main_tab_layout);
        mainActivityTabLayout.addTab(mainActivityTabLayout.newTab().setText("Tab 1"));
        mainActivityTabLayout.addTab(mainActivityTabLayout.newTab().setText("Tab 2"));
        mainActivityTabLayout.addTab(mainActivityTabLayout.newTab().setText("Tab 3"));
        mainActivityTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



        TextView tabTextView = (TextView) LayoutInflater.from(this).inflate(R.layout.activity_main_with_fragments_custom_tab, null);
        tabTextView.setText("Ввод");
        tabTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mode_edit_white_24dp, 0, 0);
        mainActivityTabLayout.getTabAt(0).setCustomView(tabTextView);

        TextView tabTextView_1 = (TextView) LayoutInflater.from(this).inflate(R.layout.activity_main_with_fragments_custom_tab, null);
        tabTextView_1.setText("Недавнее");
        tabTextView_1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_history_white_24dp, 0, 0);
        mainActivityTabLayout.getTabAt(1).setCustomView(tabTextView_1);

        TextView tabTextView_2 = (TextView) LayoutInflater.from(this).inflate(R.layout.activity_main_with_fragments_custom_tab, null);
        tabTextView_2.setText("Статистика");
        tabTextView_2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_pie_chart_white_24dp, 0, 0);
        mainActivityTabLayout.getTabAt(2).setCustomView(tabTextView_2);



        final ViewPager mainActivityViewPager = (ViewPager) findViewById(R.id.activity_main_viewpager);
        final AdapterMainActivityPager mainActivityViewPagerAdapter = new AdapterMainActivityPager
                (getSupportFragmentManager(), mainActivityTabLayout.getTabCount());
        mainActivityViewPager.setAdapter(mainActivityViewPagerAdapter);
        mainActivityViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainActivityTabLayout));
        mainActivityTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainActivityViewPager.setCurrentItem(tab.getPosition());
                mainActivityViewPagerAdapter.notifyDataSetChanged();

//                Fragment f = (Fragment) mainActivityViewPagerAdapter.instantiateItem(null, 0);
//                View view = f.getView();
//                TextView tv = (TextView) view.findViewById(R.id.current_month_screen_overall_value_textview);
//                tv.setText("none");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            PREVIOUS_ACTIVITY_INDEX = bundle.getInt(Constants.PREVIOUS_ACTIVITY_INDEX);
            TARGET_TAB = bundle.getInt(Constants.TARGET_TAB);
            savedValue = bundle.getString(Constants.SAVED_VALUE);

            if (savedValue != null && !"".equals(savedValue)) {
                Snackbar savedValueSnackbar = Snackbar.make(mainActivityViewPager, savedValue + " руб. сохранено", Snackbar.LENGTH_LONG);
                savedValueSnackbar.show();
            }

            TabLayout.Tab selectedTab = null;
            switch (TARGET_TAB) {
                case Constants.FRAGMENT_CURRENT_MONTH_SCREEN:
                    selectedTab = mainActivityTabLayout.getTabAt(0);
                    selectedTab.select();
                    break;
                case Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN:
                    selectedTab = mainActivityTabLayout.getTabAt(1);
                    selectedTab.select();
                    break;
                case Constants.FRAGMENT_STATISTIC_MAIN_SCREEN:
                    selectedTab = mainActivityTabLayout.getTabAt(2);
                    selectedTab.select();
            }
        }
    }
}
