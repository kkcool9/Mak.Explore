package com.kk.makexplore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class InterestedExpFragment extends Fragment {
    private View mRootView;
    private Firebase firebaseRef = new Firebase("https://projectexplore.firebaseio.com/");
    private Firebase expFirebaseRef;
    private Firebase firebaseUserDataRef;
    private RecyclerView mRecyclerView;
    List<Map<String,String>> experiences = new ArrayList<>();
    public static final String EXP_ARG = "USERID";
    private String userID;
    private String userLikedEvents;
    private String eventId;
    private Firebase interestedRef;
    private   ArrayList<String> likedEventsList;
    private boolean flag = false;

    public static InterestedExpFragment newInstance(String userID){
        InterestedExpFragment fragment = new InterestedExpFragment();
        //firebaseRef = dbRef;
        Bundle args = new Bundle();
        args.putString(EXP_ARG, userID);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public InterestedExpFragment(){}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.userID =  getArguments().getString(EXP_ARG);
            Log.i("Hosted user id", "onCreate: " + this.userID);
        }
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.interested_experiences_recycler_layout,null);
        likedEventsList = new  ArrayList<String> ();
        Log.i("Hosted user id", "onCreate: "+this.userID);
        firebaseUserDataRef = firebaseRef.child("Users");
        interestedRef = firebaseUserDataRef.child(userID);
        Query eventsDetailsQuery = interestedRef.orderByKey().equalTo("InterestedEvents");
        eventsDetailsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                likedEventsList = (ArrayList<String>)dataSnapshot.getValue();
                 Log.i(" Interestedevents", "onChildAdded:----------------- " + likedEventsList);
                 initData();
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
        Log.i("Entering------", "onCreateView: ");

        //Recycler View.
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.interestedExperiencesRecycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(new PagerRecyclerAdapter(experiences, this.getActivity()));
        return mRootView;
    }

    @Override
    public String toString() {
        return "Interested";
    }
    public void initData(){
        if (experiences != null)
            experiences.clear();


        for (String event : likedEventsList) {
            expFirebaseRef = new Firebase("https://projectexplore.firebaseio.com/Experiences");
            Query eventsDetailsQuery = expFirebaseRef.orderByKey().equalTo(event);
            eventsDetailsQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    HashMap<String, String> experience = (HashMap<String, String>) dataSnapshot.getValue();
                    String ExpTitle = (experience.get("EXPTitle"));
                    Log.i("Hosted Child :", "onChildAdded: " + ExpTitle);
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