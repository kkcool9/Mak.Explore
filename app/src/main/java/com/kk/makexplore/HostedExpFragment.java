package com.kk.makexplore;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HostedExpFragment  extends Fragment {
    private View mRootView;
    private  Firebase firebaseRef = new Firebase("https://projectexplore.firebaseio.com/");
    private Firebase expFirebaseRef;
    private Firebase firebaseUserDataRef;
    private RecyclerView mRecyclerView;
    List<Map<String,String>> experiences = new ArrayList<>();
    public static final String EXP_ARG = "Experience List";
    private String userID;
    private String userHostedExperiences;
    List<Map<String,String>> userExperiences = new ArrayList<>();


    public static HostedExpFragment newInstance(String userID){
        HostedExpFragment fragment = new HostedExpFragment();
        //firebaseRef = dbRef;
        Bundle args = new Bundle();

        args.putString(EXP_ARG, userID);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public HostedExpFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.userID =  getArguments().getString(EXP_ARG);
            Log.i("Hosted user id", "onCreate: "+this.userID);
        }
        setRetainInstance(true);
    }
/*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("UserExpId",this.userID);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            this.userID = savedInstanceState.getString("UserExpId");
            Log.i("Saved instance State", "onActivityCreated: "+this.userID);
        }
    }
*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.hosted_experiences_recycler_layout,null);
        //this.userID = MyActivitiesFragment.UserID;
        Log.i("Hosted user id", "onCreate: "+this.userID);
        firebaseUserDataRef = firebaseRef.child("Users");
        Query userDetailsQuery = firebaseUserDataRef.orderByKey().equalTo(this.userID);
        userDetailsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, ?> userDataFromCloud = (HashMap<String, ?>) dataSnapshot.getValue();
                userHostedExperiences = (userDataFromCloud.get("EventsHosted")).toString();
                Log.i("Hosted Experiences", "onChildAdded:----------------- " + userDataFromCloud.get("FirstName"));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Get Experiences.
        initData();



        //Recycler View.
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.hostedExperiencesRecycler);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
       // mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(new PagerRecyclerAdapter(experiences, this.getActivity()));



        return mRootView;
    }

    @Override
    public String toString() {
        return "Hosted";
    }

    public void initData(){
        if (experiences != null)
             experiences.clear();
        expFirebaseRef = new Firebase("https://projectexplore.firebaseio.com/Experiences");
        expFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, String> experience = (HashMap<String, String>) dataSnapshot.getValue();
                String[] experienceID = experience.get("EXPID").split("Exp_");
                String ExpTitle = (experience.get("EXPTitle"));
                Log.i("Hosted Child :", "onChildAdded: " + ExpTitle);
                Log.i("Experience ID ======:", "onChildAdded: "+experienceID[0]);
                Log.i("USER ID =========:", "onChildAdded: "+userID);
                if(experienceID[0].equals(userID))
                    onItemAddedToCloud(experience);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }
    public void onItemAddedToCloud(HashMap experience){
        int position=0;
        String expID = (String) experience.get("EXPID");

        for(int i=0;i<experiences.size();i++)
        {
            HashMap exp=(HashMap)experiences.get(i);
            String mid=(String)exp.get("EXPID");
            if(mid.equals(expID))
            {
                return;
            }
            if(mid.compareTo(expID)<0)
            {
                position=i+1;
            }
            else
                break;
        }

        experiences.add(position, experience);
        if(mRecyclerView.getAdapter() != null){
            mRecyclerView.getAdapter().notifyItemInserted(position);
        }
    }




}

