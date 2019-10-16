package com.cokimutai.med_manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentCategoryList = new ArrayList<>();

    //private int tabCount;


    public TabPagerAdapter(FragmentManager fm){
        super(fm);
        //this.tabCount = tabNumber;

    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);

/*
        switch (position){
            case 0:
                CurrentMedActivity currentMed = new CurrentMedActivity();
                return currentMed;

            case 1:
                allMedication allMedication = new allMedication();
                return allMedication;

            default:
                return null;
        }
*/
    }

    @Override
    public int getCount() {
        //return tabCount;
        return mFragmentList.size();
    }
    public void addFragment(Fragment fg, String category){
        mFragmentList.add(fg);
        mFragmentCategoryList.add(category);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentCategoryList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}
