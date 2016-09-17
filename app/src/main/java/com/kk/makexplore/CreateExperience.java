package com.kk.makexplore;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CreateExperience extends Fragment {
    private static Firebase firebaseRef;
    private static Firebase firebaseUserDataRef;
    private static final String Frg_ID = "Fragment ID";
    private View rootView;
    private EditText expStartLocation;
    private EditText expTitle;
    private EditText expEndLocation;
    private EditText expDate;
    private EditText expTime;
    private EditText expAvailableSlots;
    private EditText expDescription;
    private EditText expamount;
    private ImageButton browseExpPictures;
    private ImageButton takePicture;
    private Button createExp;
    private static String UID;
    private String imageURI;
    private String userHostedExperiences;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    String toUploadPhoto;
    private static final String FIREBASEREF = "https://projectexplore.firebaseio.com/";
    private static final String FIREBASE_EXP = "https://projectexplore.firebaseio.com/Experiences";
    final int CAMERA_REQUEST = 13323;
    final int GALLERY_REQUEST = 22131;
    String[] perms = {"android.permission. WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE","android.permission.CAMERA"};
    int permsRequestCode = 200;

    public static CreateExperience newInstance(int id,Firebase dbRef,String UserID){
        CreateExperience fragment = new CreateExperience();
       // firebaseRef = dbRef;
        UID = UserID;
        Bundle args = new Bundle();
        args.putInt(Frg_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public CreateExperience(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.create_experience_layout,container,false);
        firebaseRef = new Firebase(FIREBASEREF);
        Log.i("Firebase Reference", "onCreateView:----------------------------- " + firebaseRef);



               // firebaseRef.child("Users/"+ firebaseRef.getAuth().getUid()).addChildEventListener(new ChildEventListener() {
                    firebaseRef.child("Users/"+ UID).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.getKey().toString().equals("EventsHosted")){
                            userHostedExperiences = (dataSnapshot.getValue()).toString();
                            Log.i("User Data From Cloud", "onChildAdded:----------------- " + userHostedExperiences);
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
            //    Log.i("FIREBASE REF AUTH DATA", "onCreateView: "+firebaseRef.getAuth().getUid());







        final OnCreateExperienceListner mcreateExperienceListner;
        try {
            mcreateExperienceListner = (OnCreateExperienceListner)getContext();
        }
        catch (ClassCastException e){
            throw new ClassCastException("FrontPageActivity did not implement OnCreateExperienceListner!");
        }
        initialize();



    // On click Listners for Buttons.

    createExp.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Map<String,Object> eventUpdate = new HashMap<String, Object>();
            Map<String,String> expDetails;
            String EXPID = UID+"Exp_"+userHostedExperiences;
            expDetails = new HashMap<String,String>();
            expDetails.put("EXPTitle",expTitle.getText().toString());
            expDetails.put("StartLocation",expStartLocation.getText().toString());
            expDetails.put("EndLocation",expEndLocation.getText().toString());
            expDetails.put("EXPDate",expDate.getText().toString());
            expDetails.put("EXPTime",expTime.getText().toString());
            expDetails.put("EXPDescription",expDescription.getText().toString());
            expDetails.put("Amount",expamount.getText().toString());
            expDetails.put("PhotoURL",toUploadPhoto);
            expDetails.put("EXPID",EXPID);

            Firebase fb_exp = new Firebase(FIREBASE_EXP);
            //TODO : Create class for user data to get Experiences Hosted.
            fb_exp.child(EXPID).setValue(expDetails);
            int expCount;
            expCount = Integer.parseInt(userHostedExperiences);
            eventUpdate.put("EventsHosted",""+(expCount+1));


           // firebaseRef.child("Users/"+firebaseRef.getAuth().getUid()).updateChildren(eventUpdate);
            firebaseRef.child("Users/"+UID).updateChildren(eventUpdate);
            // TODO : event animation

        }
    });

        cameraPhoto = new CameraPhoto(getActivity().getApplicationContext());
        galleryPhoto = new GalleryPhoto(getActivity().getApplicationContext());
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1){
                        requestPermissions(perms,permsRequestCode);
                    }
                    startActivityForResult(cameraPhoto.takePhotoIntent(),CAMERA_REQUEST);
                    cameraPhoto.addToGallery();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        browseExpPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1){
                    requestPermissions(perms,permsRequestCode);
                }
                startActivityForResult(galleryPhoto.openGalleryIntent(),GALLERY_REQUEST);
            }
        });



        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200:
                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                String photoPath = cameraPhoto.getPhotoPath();
                Log.i("PhotoPath", "onActivityResult:------------  :" + photoPath);

                //imageURI = FrontPageActivity.imageURI;
                //Log.i("IMAGE URI -----:", "onActivityResult: "+imageURI);
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(1024, 1024).getBitmap();
                    String encodedImage = ImageBase64.encode(bitmap);

                    HashMap<String,String> postData= new HashMap<>();
                    postData.put("image",encodedImage);

                    PostResponseAsyncTask task = new PostResponseAsyncTask(getActivity(), postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {

                            if (s.contains("uploaded_success")){
                                imageURI = s;
                                Log.i("IMAGE URI -----:", "onActivityResult: "+imageURI);
                                String[] imgURI = imageURI.split("/");
                                toUploadPhoto = "http://localhost:80/photoUploads/Upload/"+imgURI[1];
                                Toast.makeText(getActivity().getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG).show();

                            }
                            else{
                                Toast.makeText(getActivity().getApplicationContext(),"Error while uploading",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    task.execute("http://www.kpoliset.com/photoUploads/upload.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Cannot Connect to server",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Unknown URL",Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Bad Protocol",Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Bad Encoding",Toast.LENGTH_LONG).show();

                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
                else if(requestCode == GALLERY_REQUEST) {
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                Log.i("PhotoPath", "onActivityResult:------------  :" + photoPath);
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(1024, 1024).getBitmap();
                    String encodedImage = ImageBase64.encode(bitmap);

                    HashMap<String,String> postData= new HashMap<>();
                    postData.put("image",encodedImage);

                    PostResponseAsyncTask task = new PostResponseAsyncTask(getActivity(), postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {

                            if (s.contains("uploaded_success")){
                                imageURI = s;
                                Log.i("IMAGE URI -----:", "onActivityResult: " + imageURI);
                                String[] imgURI = imageURI.split("/");
                                toUploadPhoto = "http://www.kpoliset.com/photoUploads/Upload/"+imgURI[1];
                                Log.i("IMAGE URI -----:", "onActivityResult: " + toUploadPhoto);
                                Toast.makeText(getActivity().getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG).show();

                            }
                            else{
                                Toast.makeText(getActivity().getApplicationContext(),"Error while uploading",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    task.execute("http://www.kpoliset.com/photoUploads/upload.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Cannot Connect to server",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Unknown URL",Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Bad Protocol",Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Toast.makeText(getActivity().getApplicationContext(),"Bad Encoding",Toast.LENGTH_LONG).show();

                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }



    public void initialize(){
        expTitle = (EditText) rootView.findViewById(R.id.eTitle);
        expStartLocation =(EditText) rootView.findViewById(R.id.elocation);
        expEndLocation = (EditText)rootView.findViewById(R.id.endlocation);
        expDate =(EditText) rootView.findViewById(R.id.edate);
        expTime =(EditText) rootView.findViewById(R.id.etime);
        expAvailableSlots =(EditText) rootView.findViewById(R.id.eslots);
        expDescription =(EditText) rootView.findViewById(R.id.edescription);
        expamount =(EditText) rootView.findViewById(R.id.edollar);
        browseExpPictures = (ImageButton)rootView.findViewById(R.id.ebrowse);
        createExp = (Button)rootView.findViewById(R.id.ecreate);
        takePicture = (ImageButton)rootView.findViewById(R.id.ecamera);
   }

public interface OnCreateExperienceListner{
    void onCreateExperience();

}

    public interface CameraInterface{
        void onTakePicture();

    }

    public interface UploadInterface{
        void uploadPhotoToServer(String photoPath);
    }

    //TODO : DataSnapshot.getUid.equals Query to get user data..



}
