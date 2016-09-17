package com.kk.makexplore;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.HashMap;
import java.util.Map;


public class UserCloudData  {
    private AuthData userAuthData;
    private Firebase userfirebaseRef;
    private Firebase firebaseUserDataRef;
    private Map<String,String> userInfo = new HashMap<>();


    public UserCloudData(){}

    public void getUserData(){
        firebaseUserDataRef = userfirebaseRef.child("Users");
        Query userDetailsQuery = firebaseUserDataRef.equalTo(userAuthData.getUid());
        userDetailsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, String> userDataFromCloud = (HashMap<String, String>) dataSnapshot.getValue();
                Log.i("User Data From Cloud", "onChildAdded:----------------- " + userDataFromCloud.get("FirstName"));

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
