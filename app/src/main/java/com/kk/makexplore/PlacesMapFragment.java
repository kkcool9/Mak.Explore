package com.kk.makexplore;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;


public class PlacesMapFragment extends Fragment implements OnMapReadyCallback {
    private View mRootView;
    private GoogleMap mMap;
    private MapFragment mMapFragment;
    Business selectedBusiness;
    private static ImageView locRating;
    private FloatingActionButton msgBtn;
    private TextView locTitle;
    private TextView locDesc;
    private String address="";
    private TextView addressView;
    public static final String EXP_ARG = "MAP ID";

    public static PlacesMapFragment newInstance(Business business){
        PlacesMapFragment fragment = new PlacesMapFragment();
        //firebaseRef = dbRef;
        Bundle args = new Bundle();
        args.putSerializable(EXP_ARG,business);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public PlacesMapFragment(){}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     mRootView = inflater.inflate(R.layout.placesnearby_maps,null);
        selectedBusiness = (Business) getArguments().getSerializable(EXP_ARG);
        locTitle = (TextView)mRootView.findViewById(R.id.locName);
        locDesc = (TextView)mRootView.findViewById(R.id.locDesc);
        locRating = (ImageView)mRootView.findViewById(R.id.locRatingImg);
        addressView = (TextView)mRootView.findViewById(R.id.locAddress);
        msgBtn = (FloatingActionButton)mRootView.findViewById(R.id.fab);
        Uri url = (Uri.parse(selectedBusiness.ratingImgUrl()));
        Picasso.with(getContext()).load(url).into(PlacesMapFragment.locRating);

        locTitle.setText(selectedBusiness.name());
        locDesc.setText(selectedBusiness.snippetText());

        msgBtn.setTransitionName(selectedBusiness.name());
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent  = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = address;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Location");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));


            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.placeMap);
        mapFragment.getMapAsync(this );

      return mRootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(" LAT LONG", "onMapReady: "+selectedBusiness.location().coordinate().latitude());
        double lat;
        double lon;
        address="";
        final LatLng mB = new LatLng(-37.81319,144.96298);
        lat = selectedBusiness.location().coordinate().latitude();
        lon = selectedBusiness.location().coordinate().longitude();
        LatLng posit = new LatLng(lat,lon);
        ArrayList<String> locationAddress = selectedBusiness.location().displayAddress();
        for (String loc : locationAddress){
            address = address+loc+"\n";
        }
        addressView.setText(address);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posit,15));
        // final LatLng pos = new LatLng(lat,lon);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lon))
                .snippet(address)
                .title(selectedBusiness.name()));


    }
}
