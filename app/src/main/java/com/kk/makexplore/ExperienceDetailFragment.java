package com.kk.makexplore;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ExperienceDetailFragment extends Fragment {
    private ImageView mImgView;
    private TextView expTitle;
    private TextView expDesc;
    private TextView expStartLocation;
    private TextView expEndLocation;
    private TextView expAmount;
    private View mrootView;

    private Button addtoInterested;
    public static final String EXP_ARG = "Experience Details";
    private HashMap<String,String > exp;
    private String userID;
    private Firebase userInterestedExperiences;
    private String likedEvents;
    private static final String FIREBASE_USER = "https://projectexplore.firebaseio.com";
    Map<String,Object> eventUpdate = new HashMap<String, Object>();
    Map<String,Object> interestedEvent = new HashMap<String, Object>();

    public static ExperienceDetailFragment newInstance(HashMap<String,String> experience,String UID){
        ExperienceDetailFragment fragment = new ExperienceDetailFragment();
        //firebaseRef = dbRef;
        Bundle args = new Bundle();
        args.putSerializable(EXP_ARG,experience);
        args.putString("USER_ID",UID);
        fragment.setArguments(args);
        return fragment;
    }
    // Default Constructor
    public ExperienceDetailFragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null){
            exp = (HashMap<String,String>)getArguments().getSerializable(EXP_ARG);
            userID = getArguments().getString("USER_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       mrootView = inflater.inflate(R.layout.experience_details_layout,container,false);
        mrootView.setTransitionName(exp.get("EXPTitle"));


        Firebase firebaseRef = new Firebase(FIREBASE_USER);
        userInterestedExperiences = firebaseRef.child("Users");

        Query userDetailsQuery = userInterestedExperiences.orderByKey().equalTo(userID);
        userDetailsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, ?> userDataFromCloud = (HashMap<String, ?>) dataSnapshot.getValue();
                likedEvents = (userDataFromCloud.get("LikedEvents")).toString();
                Log.i("User Data From Cloud", "onChildAdded:----------------- " + likedEvents);

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



        initiliaze();

        expTitle.setText(exp.get("EXPTitle"));
        Log.i("GET TEXT--:", "onCreateView: "+exp.get("EXPTitle"));
        expDesc.setText(exp.get("EXPDescription"));
        expStartLocation.setText(exp.get("StartLocation"));
        expEndLocation.setText(exp.get("EndLocation"));
        expAmount.setText(exp.get("Amount"));
       // mImgView.setTransitionName(exp.get("EXPTitle"));
        Uri url = (Uri.parse((String)exp.get("PhotoURL")));
        Picasso.with(getActivity()).load(url).into(mImgView);

        return mrootView;
    }


    public void initiliaze(){
        mImgView = (ImageView) mrootView.findViewById(R.id.expDImg);
        expTitle = (TextView)mrootView.findViewById(R.id.expDTitle);
        expDesc = (TextView)mrootView.findViewById(R.id.expDDesc);
        expStartLocation = (TextView)mrootView.findViewById(R.id.expSLocation);
        expEndLocation = (TextView)mrootView.findViewById(R.id.expELocation);
        expAmount = (TextView)mrootView.findViewById(R.id.expDAmount);
        addtoInterested = (Button)mrootView.findViewById(R.id.expRegister);
        Animation loginAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.loginlayout_rect_animation);
        mImgView.startAnimation(loginAnimation);
        expTitle.startAnimation(loginAnimation);
        expDesc.startAnimation(loginAnimation);
        addtoInterested.startAnimation(loginAnimation);

        addtoInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                interestedEvent.put(likedEvents,exp.get("EXPID"));





                int expCount;
                expCount = Integer.parseInt(likedEvents);
                eventUpdate.put("LikedEvents",""+(expCount+1));
                userInterestedExperiences.child(userID).updateChildren(eventUpdate);
                userInterestedExperiences.child(userID).child("InterestedEvents").updateChildren(interestedEvent);


            }
        });

    }
}
