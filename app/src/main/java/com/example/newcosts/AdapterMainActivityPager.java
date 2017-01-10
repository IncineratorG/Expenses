package com.example.newcosts;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class AdapterMainActivityPager extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private List<Fragment> fragmentsList;

    public AdapterMainActivityPager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

        fragmentsList = new ArrayList<>(3);
        fragmentsList.add(new FragmentCurrentMonthScreen());
        fragmentsList.add(new FragmentLastEnteredValuesScreen_V2());
        fragmentsList.add(new FragmentStatisticMainScreen());
    }

    @Override
    public Fragment getItem(int position) {

        return fragmentsList.get(position);
//        switch (position) {
//            case 0:
//                FragmentCurrentMonthScreen tab1 = new FragmentCurrentMonthScreen();
//                return tab1;
//            case 1:
//                FragmentLastEnteredValuesScreen_V2 tab2 = new FragmentLastEnteredValuesScreen_V2();
//                return tab2;
//            case 2:
//                FragmentStatisticMainScreen tab3 = new FragmentStatisticMainScreen();
//                return tab3;
//            default:
//                return null;
//        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    // ================= !!Переделать!! ===================
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    // ====================================================

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
