package com.kk.makexplore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;


public class FragPagerAdapter extends SmartFragmentStatePagerAdapter {
    ArrayList<Fragment> pages = new ArrayList<>();
    public FragPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return pages.get(position);
            case 1:return pages.get(position);
            default:return null;
        }
    }

    @Override
    public int getCount() {
        return pages.size();
    }
    public void addPage(Fragment f){
        pages.add(f);

    }
    @Override
    public CharSequence getPageTitle(int position) {
        return pages.get(position).toString();
    }
}
