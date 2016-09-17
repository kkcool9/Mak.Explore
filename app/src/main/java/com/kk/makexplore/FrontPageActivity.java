package com.kk.makexplore;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.security.PublicKey;
import java.util.HashMap;


public class FrontPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,CreateExperience.OnCreateExperienceListner,UpcomingExperiencesFragment.onListItemSelectedListner {
    Fragment mFFrag;
    NavigationView navigationView;
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    ActionBar actionBar;
    private String UID;
    Firebase firebaseref;
    private TextView mainName;
    private TextView mid;
    boolean menu_checked = false;

    private static final String FIREBASEREF = "https://projectexplore.firebaseio.com/";
    private static final String IMAGE_SERVER = "http://www.kpoliset.com/";
    public static String imageURI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this.getApplicationContext());
        setContentView(R.layout.frontpage_layout);


        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // actionBar.setIcon(R.drawable.appicon);
        firebaseref = new Firebase(FIREBASEREF);
        UID = getIntent().getStringExtra("USER_ID");
        navigationView = (NavigationView)findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.mainactivity_with_navidrawer);

        View headerLayout = navigationView.getHeaderView(0);

        mainName =(TextView) headerLayout.findViewById(R.id.kkname);
        mid =(TextView) headerLayout.findViewById(R.id.mid);

        if (firebaseref.getAuth() != null){
            Log.i("FIREBASE REF AUTH DATA", "onCreateView: "+firebaseref.getAuth().getUid());
        }

        if(savedInstanceState != null){
            mFFrag = getSupportFragmentManager().getFragment(savedInstanceState,"mFFrag");
        }
        else{
            mFFrag = UpcomingExperiencesFragment.newInstance(1,UID,firebaseref);
        }


        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_closed){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        firebaseref.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {



                    firebaseref.child("Users/"+firebaseref.getAuth().getUid()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            // userDataFromCloud = (HashMap<String, ?>) dataSnapshot.getValue();
                            Log.i("User Data From Cloud", "onChildAdded:----------------- " + dataSnapshot.getKey());
                            switch(dataSnapshot.getKey()){
                                case "Email":
                                    mid.setText(dataSnapshot.getValue().toString());
                                    Log.i("MAIL ID", "onChildAdded: "+dataSnapshot.getValue().toString());
                                    break;

                                case "FirstName":
                                    mainName.setText(dataSnapshot.getValue().toString());
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
                } else {
                    // user is not logged in
                }
            }});



        // Recycler View
        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_fragmentcontainer,mFFrag).commit();






    }


    public Firebase getFirebaseref(){
        return firebaseref;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.createExperience:
                mFFrag = CreateExperience.newInstance(R.id.createExperienceLayout,firebaseref,UID);
              // mFFrag.setEnterTransition(new Slide(Gravity.RIGHT));
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fragenter_anim,R.anim.fragexit_anim);
                fragmentTransaction.replace(R.id.mainactivity_fragmentcontainer,mFFrag).commit();
                break;
            case R.id.myProfile:
                mFFrag = ProfileFragment.newInstance(UID,firebaseref);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_fragmentcontainer,mFFrag).commit();
                break;
            case R.id.activities:
                mFFrag = MyActivitiesFragment.newInstance(UID);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_fragmentcontainer,mFFrag).addToBackStack(null).commit();
                break;
            case R.id.placesNearby:
                mFFrag = PlacesNearByFragment.newInstance(R.id.placesNearby);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_fragmentcontainer,mFFrag).addToBackStack(null).commit();
                break;


            default:
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.refresh:
                finish();
                startActivity(getIntent());
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateExperience() {

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFFrag.isAdded()){
            getSupportFragmentManager().putFragment(outState,"mFFrag",mFFrag);
        }
    }

    @Override
    public void onListItemSelected(int position, HashMap<String, ?> Experiences) {
        //TODO : Experiences Detail View.
        //getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_fragmentcontainer,).commit();
    }




    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.frontpage_activity_refresh,menu);
        return super.onCreateOptionsMenu(menu);
    }




}

