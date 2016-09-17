package com.kk.makexplore;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class PlacesNearByFragment extends Fragment  implements OnConnectionFailedListener,LocationListener{

    private GoogleApiClient mGoogleAPIClient;
    private View mrootView;
    private RecyclerView mRecyclerView;
    public static final String EXP_ARG = "Places NearBy";
    PlaceAutocompleteFragment autocompleteFragment;
    PlacesNearByRecyclerAdapter mRecyclerAdapter;
    private final String consumerKey = "2Bfpp4Zv5ElBjQdI-DISqA";
    private final String consumerSecret = "-Cl__SzSRKV8OWVOE83ABOZCvcY";
    private final String token = "W5TPokDNn9-FUaxhAnrS2IcnraYN2jHy";
    private final String tokenSecret = "8WMTKBvxvfsQgPC4RqYVrF6FNdE";
    private  YelpAPI yelpAPI;
    private Call<SearchResponse> call;
    private Map<String, String> params;
    private double latitude;
    private boolean landScape;
    private double longitude;
    StaggeredGridLayoutManager lManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    public static PlacesNearByFragment newInstance(int id){
        PlacesNearByFragment fragment = new PlacesNearByFragment();
        //firebaseRef = dbRef;
        Bundle args = new Bundle();

        args.putInt(EXP_ARG, id);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public PlacesNearByFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleAPIClient != null)
             mGoogleAPIClient = new GoogleApiClient
                .Builder(this.getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        setRetainInstance(true);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            latitude = savedInstanceState.getDouble("lat");
            longitude = savedInstanceState.getDouble("lon");
        }
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("AGAIN CREATED=======", "onCreateView: ");

        lManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrootView = inflater.inflate(R.layout.places_nearby_fragment, container, false);

        if (!checkPlayServices()) {
            getActivity().finish();
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 126);
            return mrootView;
        }




//TODO : ORIENTATION CHANGES

        mRecyclerView = (RecyclerView) mrootView.findViewById(R.id.placesNearByRecycler);
        Log.i("IS_PHONE", "onCreateView: "+getResources().getBoolean(R.bool.is_phone));
        landScape = getResources().getBoolean(R.bool.is_phone);
        Log.i("AGAIN CREATED========", "onCreateView: "+landScape);
        if(landScape == false){
            Log.i("LANDSCAPE FALSE", "onCreateView: ");

            lManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(lManager);
        }
        else {
            mRecyclerView.setLayoutManager(lManager);
        }

        YelpAPIFactory apiFactory = new YelpAPIFactory(consumerKey, consumerSecret, token, tokenSecret);
        yelpAPI = apiFactory.createAPI();

        autocompleteFragment= (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        if((latitude != 0.0d) && (longitude != 0.0d)){
            YelpAsyncTask yelpAsyncTask = new YelpAsyncTask(latitude,longitude);
            yelpAsyncTask.execute();
        }
        else {
            YelpAsyncTask yelpAsyncTask = new YelpAsyncTask(37.7867703362929,-122.399958372115);
            yelpAsyncTask.execute();
        }



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Place Name ======== :", "Place: " + place.getName());
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                YelpAsyncTask yelpAsyncTask = new YelpAsyncTask(place.getLatLng().latitude,place.getLatLng().longitude);
               yelpAsyncTask.execute();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR", "An error occurred: " + status);
            }
        });



        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 2000, 0, this);

// general params


        return mrootView;
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getContext());
        if(result != ConnectionResult.SUCCESS) {
            return false;
        }

        return true;
    }
    public void itemAnimation() {
        FadeInDownAnimator animator = new FadeInDownAnimator();
        //OvershootInRightAnimator animator = new OvershootInRightAnimator();
        animator.setInterpolator((new OvershootInterpolator()));
        animator.setAddDuration(400);
        animator.setRemoveDuration(400);
        mRecyclerView.setItemAnimator(animator);
    }

    private void adapteranimation() {
        SlideInBottomAnimationAdapter animationAdapter1 = new SlideInBottomAnimationAdapter(mRecyclerAdapter);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(animationAdapter1);
        scaleInAnimationAdapter.setDuration(400);

        mRecyclerView.setAdapter(scaleInAnimationAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleAPIClient != null)
            mGoogleAPIClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleAPIClient != null && mGoogleAPIClient.isConnected()) {
            mGoogleAPIClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 126:
                if ((grantResults.length > 0) && (grantResults[0]== PackageManager.PERMISSION_GRANTED) ){
                    Log.i("PERMISSOIN GRANTED", "onRequestPermissionsResult: ");
                }
        }
        return;
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlaceAutocompleteFragment acF = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        if(acF !=null)
            getActivity().getFragmentManager().beginTransaction().remove(acF).commit();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class YelpAsyncTask extends AsyncTask<Void,Void,SearchResponse>{
        CoordinateOptions coordinate;


        public YelpAsyncTask(double latitude, double longitude) {
            params = new HashMap<>();
            params.put("term", "places to visit");
            Log.i("LATITUDE", "YelpAsyncTask: "+latitude);
            coordinate  = CoordinateOptions.builder()
                    .latitude(latitude)
                    .longitude(longitude).build();

        }




        @Override
        protected SearchResponse doInBackground(Void... param) {
            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    SearchResponse searchResponse = response.body();
                    // Update UI text with the searchResponse.
                }
                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                }
            };

            call = yelpAPI.search(coordinate, params);
            call.clone().enqueue(callback);
            SearchResponse response;

            try {
               response = call.execute().body();

                Log.i("YELP RESPONSE ======:", "onPlaceSelected: "+response);
                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }
           return null;
        }

        @Override
        protected void onPostExecute(SearchResponse searchResponse) {
            super.onPostExecute(searchResponse);
            final ArrayList<Business> businesses = searchResponse.businesses();
            Log.i("Business Size", "onPostExecute: "+businesses.size());
            mRecyclerAdapter = new PlacesNearByRecyclerAdapter(getActivity(),businesses);
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mRecyclerAdapter.setOnItemClickListner(new PlacesNearByRecyclerAdapter.OnItemClickListner() {
                                                       @Override
                                                       public void onItemClick(View view, int position, ImageView imgView) {

                                                          PlacesMapFragment placesMapFragment = PlacesMapFragment.newInstance(businesses.get(position));
                                                           placesMapFragment.setSharedElementEnterTransition(new DetailsTransition());
                                                           placesMapFragment.setEnterTransition(new Fade());
                                                           placesMapFragment.setExitTransition(new Fade());
                                                           placesMapFragment.setSharedElementReturnTransition(new DetailsTransition() );
                                                           getFragmentManager().beginTransaction().replace(R.id.mainactivity_fragmentcontainer,placesMapFragment).addSharedElement(imgView,imgView.getTransitionName()).addToBackStack(null).commit();
                                                       }
                                                   });
            mRecyclerAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(0);
            itemAnimation();
            adapteranimation();

        }
    }

    public class DetailsTransition extends TransitionSet{
        public DetailsTransition(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setOrdering(ORDERING_TOGETHER);
                addTransition(new ChangeBounds())
                        .addTransition(new ChangeBounds())
                        .addTransition(new ChangeBounds());
            }
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("Lat",latitude);
        outState.putDouble("lon",longitude);
    }


}
