package com.kk.makexplore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;


public class UserRegistration extends Fragment {

    private View rootView;
    private EditText firstName;
    private EditText lastName;
    private EditText contactNumber;
    private EditText gender;
    private EditText userEmail;
    private EditText passCode;
    private Button createNewUserButton;
    private static Firebase firebaseRef;


    private static final String Frg_ID = "Fragment ID";

    public static UserRegistration newInstance(int id,Firebase dbRef){
        UserRegistration fragment = new UserRegistration();
        firebaseRef = dbRef;
        firebaseRef.unauth();
        Bundle args = new Bundle();
        args.putInt(Frg_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public UserRegistration(){}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_registration_layout,container,false);


        initialize();


        createNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createNewUser();

            }
        });



        return rootView;
    }

    public void initialize(){
        firstName = (EditText)rootView.findViewById(R.id.firstName);
        lastName = (EditText)rootView.findViewById(R.id.lastName);
        contactNumber = (EditText)rootView.findViewById(R.id.contactNumber);
        gender = (EditText)rootView.findViewById(R.id.gender);
        userEmail = (EditText)rootView.findViewById(R.id.userEmail);
        passCode = (EditText)rootView.findViewById(R.id.userPass);
        createNewUserButton = (Button)rootView.findViewById(R.id.userRegister);


    }


    // create a new user in Firebase
    public void createNewUser() {
        if(userEmail.getText() == null || ! LoginFragment.isEmailValid(userEmail.getText().toString())) {
            return;
        }
        final onResiterSuccessful mRegisterListner;
        try {
            mRegisterListner = (onResiterSuccessful)getContext();
        }catch (ClassCastException e){
            throw new ClassCastException("FrontPageActivity did not implement OnRegisterSuccessful Interface..");
        }


        firebaseRef.createUser(userEmail.getText().toString(), passCode.getText().toString(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {


                    final Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                        Map<String,String> userDetails;

                        @Override
                        public void onAuthenticated(AuthData authData) {
                            userDetails = new HashMap<>();
                            userDetails.put("FirstName",firstName.getText().toString());
                            userDetails.put("LastName",lastName.getText().toString());
                            userDetails.put("Email",userEmail.getText().toString());
                            userDetails.put("Contact",contactNumber.getText().toString());
                            userDetails.put("Gender",gender.getText().toString());
                            userDetails.put("Uid",authData.getUid());
                            userDetails.put("EventsHosted","0");
                            userDetails.put("LikedEvents","0");

                            // Add User Data to Firebase..
                            firebaseRef.child("Users").child(authData.getUid()).setValue(userDetails);
                            mRegisterListner.OnRegisterSuccessful(authData);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            Log.i("Login Error", "onAuthenticationError:------------------- Login Failed ");
                        }
                    };



                    @Override
                    public void onSuccess(Map<String, Object> result) {

                        Snackbar snackbar = Snackbar.make(userEmail, LoginFragment.USER_CREATION_SUCCESS, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        firebaseRef.authWithPassword(userEmail.getText().toString(), passCode.getText().toString(), authResultHandler);


                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Snackbar snackbar = Snackbar.make(userEmail, LoginFragment.USER_CREATION_ERROR, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }



                });
    }


    public interface onResiterSuccessful{
        void OnRegisterSuccessful(AuthData authData);
    }

    public interface PopulateUserData{
        void podpulateData(AuthData authData,Firebase firebaseRef);
    }


}
