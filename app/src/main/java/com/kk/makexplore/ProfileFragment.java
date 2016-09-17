package com.kk.makexplore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.HashMap;


public class ProfileFragment extends Fragment {
    private View rootView;
    private String userID;
    private static Firebase firebaseRef;
    private Firebase firebaseUserDataRef;
    private HashMap<String, ?> userDataFromCloud;
    private ImageView profilePic;
    private TextView email;
    private TextView name;
    private TextView contactNum;
    private TextView eventsHosted;
    private ImageView fromLeft;
    private ImageView fromRight;
    private ImageView bf1;
    private ImageView bf2;
    private TextView eventsInterested;
    private static final String FIREBASEREF = "https://projectexplore.firebaseio.com/";

    // Fragment : New Instance
    public static ProfileFragment newInstance(String UID,Firebase dbRef){
        ProfileFragment fragment = new ProfileFragment();
        firebaseRef = dbRef;
        Bundle args = new Bundle();
         args.putString("USER_ID_PF",UID);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public ProfileFragment(){}



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_layout,container,false);




        if (firebaseRef.getAuth() != null){
            Log.i("FIREBASE PROFILE", "onCreateView: "+firebaseRef.getAuth().getUid());
        }
        else{
            Toast.makeText(this.getContext(),"AUTH ERROR",Toast.LENGTH_LONG);
        }


        userDataFromCloud = new HashMap<>();
        if(getArguments() != null){
            userID = getArguments().getString("USER_ID_PF");
        }


                    firebaseRef.child("Users/"+userID).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                           // userDataFromCloud = (HashMap<String, ?>) dataSnapshot.getValue();
                            Log.i("User Data From Cloud", "onChildAdded:----------------- " + dataSnapshot.getKey());
                            switch(dataSnapshot.getKey()){
                                case "Email":
                                    contactNum.setText(dataSnapshot.getValue().toString());
                                    break;
                                case "EventsHosted":
                                    eventsHosted.setText(dataSnapshot.getValue().toString());
                                    break;
                                case "LikedEvents":
                                    eventsInterested.setText(dataSnapshot.getValue().toString());
                                    break;
                                case "FirstName":
                                    name.setText(dataSnapshot.getValue().toString());
                                    break;
                                default:
                                    break;
                            }
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

                    // user is logged in



        initData();

        return rootView;
    }

    public void bindData(){

    }

    public void initData(){
           // email = (TextView) rootView.findViewById(R.id.email);
        name = (TextView) rootView.findViewById(R.id.profileName);
        contactNum = (TextView) rootView.findViewById(R.id.profileNumber);
        eventsHosted = (TextView)rootView.findViewById(R.id.expHosted);
        eventsInterested = (TextView)rootView.findViewById(R.id.expAttented);
        fromLeft = (ImageView)rootView.findViewById(R.id.imgfromleft);
        fromRight=(ImageView)rootView.findViewById(R.id.imgfromRight);
        bf1 = (ImageView)rootView.findViewById(R.id.imageView2);
        bf2=(ImageView)rootView.findViewById(R.id.imageView4);
        Animation bfAnim = AnimationUtils.loadAnimation(getContext(),R.anim.bf_anim);
        Animation fromRight_anim = AnimationUtils.loadAnimation(getContext(),R.anim.imf_from_right);
        Animation fromLeft_anim = AnimationUtils.loadAnimation(getContext(),R.anim.imgfrom_left);
        fromLeft.startAnimation(fromLeft_anim);
        fromRight.startAnimation(fromRight_anim);
        bf1.startAnimation(bfAnim);
        bf2.startAnimation(bfAnim);




    }
}
