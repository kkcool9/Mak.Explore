package com.kk.makexplore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


public class MyActivitiesFragment extends Fragment {
    private View mRootView;
    public static String UserID;
    private ViewPager vp;
    public static final String EXP_ARG = "USER ID";



    public static MyActivitiesFragment newInstance(String uID){
        MyActivitiesFragment fragment = new MyActivitiesFragment();
        //firebaseRef = dbRef;
        Bundle args = new Bundle();

       args.putString(EXP_ARG,uID);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public MyActivitiesFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.UserID = getArguments().getString(EXP_ARG);
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("Activities onCreateView", "onCreateView: "+this.UserID);
        mRootView = inflater.inflate(R.layout.coordinator_layout,container,false);
        vp = (ViewPager) mRootView.findViewById(R.id.viewpagercont);

        this.addPages(vp);
        //Pager Animation
       vp.setPageTransformer(true, new CubeOutTransformer());

        TabLayout tabLayout = (TabLayout)mRootView.findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(vp);
        tabLayout.setOnTabSelectedListener(listner(vp));

        return  mRootView;
    }



    public void addPages(ViewPager pager){
        FragPagerAdapter fragPagerAdapter = new FragPagerAdapter(getFragmentManager());
        fragPagerAdapter.addPage(HostedExpFragment.newInstance(UserID));
        fragPagerAdapter.addPage(InterestedExpFragment.newInstance(UserID));
        pager.setAdapter(fragPagerAdapter);

    }


    private TabLayout.OnTabSelectedListener listner(final ViewPager pager){

        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }


}
