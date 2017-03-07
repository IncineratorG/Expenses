package com.example.newcosts;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class AdapterMainActivityPager extends FragmentStatePagerAdapter {
    private int numOfTubs;
    private List<Fragment> fragmentsList;

    public AdapterMainActivityPager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.numOfTubs = NumOfTabs;

        fragmentsList = new ArrayList<>(3);
        fragmentsList.add(new FragmentCurrentMonthScreen());
        fragmentsList.add(new FragmentLastEnteredValuesScreen());
        fragmentsList.add(new FragmentStatisticMainScreen());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return numOfTubs;
    }

    // ================= !!Переделать!! ===================
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
//        return POSITION_UNCHANGED;
    }
    // ====================================================

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
