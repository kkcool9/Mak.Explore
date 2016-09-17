package com.kk.makexplore;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExperienceCloudData {
    List<Map<String,String>> experienceList;
    UEFireBaseAdapter mFirebaseAdapter;
    Context mContext;
    Firebase xRef;
    Firebase mRef;
    public List<Map<String,String>> getExperienceList(){return experienceList;}
    public static String FIREBASEREF =  "https://projectexplore.firebaseio.com/";


    public void setAdapter(UEFireBaseAdapter mAdapter){mFirebaseAdapter = mAdapter;}
    public void setContext(Context context){mContext = context;}
    public int getSize(){return experienceList.size();}

    public HashMap getItem(int i){
        if(i>=0 && i<experienceList.size()){
            return (HashMap)experienceList.get(i);
        }
        else
            return null;
    }

    public ExperienceCloudData(){
        experienceList = new ArrayList<Map<String, String>>();
        mContext= null;
        mFirebaseAdapter = null;
        xRef = new Firebase(FIREBASEREF);
        mRef = xRef.child("Experiences");

    }

    public void initData(){
        experienceList.clear();
        mRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, String> experience = (HashMap<String, String>) dataSnapshot.getValue();
                String ExpTitle = (experience.get("EXPTitle")).toString();
                Log.i("TITLE -----:", "onChildAdded: "+experience);

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

        for(int i=0;i<experienceList.size();i++)
        {
            HashMap exp=(HashMap)experienceList.get(i);
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

        experienceList.add(position,experience);
        if(mFirebaseAdapter != null){
            mFirebaseAdapter.notifyItemInserted(position);
        }
    }

}
