package com.kk.makexplore;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginFragment extends Fragment{
    private CallbackManager mCallbackManager;
    private static Firebase firebaseRef;
    private static EditText userName;
    private View rootView;
    private EditText userPass;
    private Button loginBtn;
    private Button newUserBtn;
    private ImageView titleImgView;
    private ProfileTracker profileTracker;
    private AccessTokenTracker mFacebookAccessTokenTracker;
    private Profile userFBProfile;
    private HashMap<String,String> userFBData = new HashMap<>();
    String mName;
     ProgressDialog progressDialog;

    /* String Constants */

    private static final String FIREBASE_ERROR = "Firebase Error";
    private static final String USER_ERROR = "User Error";
    private static final String LOGIN_SUCCESS = "Login Success";
    public static final String USER_CREATION_SUCCESS =  "Successfully created user";
    public static final String USER_CREATION_ERROR =  "User creation error";
    private static final String EMAIL_INVALID =  "email is invalid :";
    private static final String Frg_ID = "Fragment ID";

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            String accessToken = loginResult.getAccessToken().getToken();
            Log.i("FB ACCESS TOKEN ------:", "onSuccess: "+accessToken);
            if(Profile.getCurrentProfile() == null){
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        userFBProfile = currentProfile;
                        userFBData.put("FirstName",userFBProfile.getFirstName());
                        userFBData.put("LastName",userFBProfile.getLastName());
                        Log.i(" FB NAME  NEW PROFILE: ", "onCurrentProfileChanged: "+userFBProfile.getFirstName());

                    }
                };

            }
            else {
                Profile profile = Profile.getCurrentProfile();
                userFBProfile = profile;
                userFBData.put("FirstName",userFBProfile.getFirstName());
                userFBData.put("LastName",userFBProfile.getLastName());
                Log.i(" FB NAME  OLD PROFILE: ", "onCurrentProfileChanged: "+profile.getFirstName());
            }
        }


        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException error) {

        }
    };



    // Fragment : New Instance
    public static LoginFragment newInstance(int id,Firebase dbRef){
        LoginFragment fragment = new LoginFragment();
        firebaseRef = dbRef;
        Bundle args = new Bundle();
        args.putInt(Frg_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public LoginFragment(){}




    // Set Firebase Context.
    @Nullable
    @Override
    public void onCreate( @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.login_layout,container,false);
        initialize();
        final OnSignupClicked signupClicked;
        try {
            signupClicked = (OnSignupClicked) getContext();
        }catch (ClassCastException e){
            throw  new ClassCastException("Login Activity did not implement the fragment OnSignupClicked Interface!");
        }


        final Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                if (authData != null){
                    Log.i("UserID", "onAuthenticated:-------- :"+authData.getUid());
                    progressDialog.dismiss();
                    signupClicked.loginUser(authData);

                }
                else{
                    Log.i("Login Error", "onAuthenticated: Login User "+authData.getUid());
                }
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.i("Login Error", "onAuthenticationError:------------------- Login Failed ");
                loginBtn.setEnabled(true);
                progressDialog.dismiss();

            }
        };



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setEnabled(false);
                progressDialog = new ProgressDialog(getActivity(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();


                Log.i("Auth User", "onClick:--------------- : User is being Authenticated..");

                if (userName.getText().toString() == null || !isEmailValid(userName.getText().toString())) {
                    Log.i("Login Error", "onClick:--------------- : Invalid Email.");
                    progressDialog.dismiss();
                    loginBtn.setEnabled(true);

                } else {
                    Log.i("Auth User", "onClick:--------------- : User is being Authenticated..");
                    // Login with an email/password combination
                    firebaseRef.authWithPassword(userName.getText().toString(), userPass.getText().toString(), authResultHandler);

                }


            }
        });

        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupClicked.registerUser();
            }
        });

        // Login Animation
        LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.loginLinearLayout);
        Animation loginAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.loginlayout_rect_animation);
        linearLayout.startAnimation(loginAnimation);
        titleImgView.startAnimation(loginAnimation);


        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LoginButton loginButton = (LoginButton)view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "user_friends","user_birthday","user_about_me","email");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);
        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                LoginFragment.this.onFacebookAccessTokenChange(currentAccessToken);
            }
        };
    }

    // Validate email address for new accounts.
    public static boolean isEmailValid(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            userName.setError(EMAIL_INVALID + email);
            return false;
        }
        return true;
    }



    public void initialize(){
        loginBtn = (Button)rootView.findViewById(R.id.loginButton);
        newUserBtn = (Button)rootView.findViewById(R.id.createUserButton);
        userName = (EditText)rootView.findViewById(R.id.userEmail);
        userPass = (EditText)rootView.findViewById(R.id.userPassCode);
        titleImgView = (ImageView)rootView.findViewById(R.id.titleImg);
    }


    public interface OnSignupClicked{
        void registerUser();
        void loginUser(AuthData authData);
    }


    private void onFacebookAccessTokenChange(AccessToken token) {


        if (AccessToken.getCurrentAccessToken() != null) {

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {

                            if (AccessToken.getCurrentAccessToken() != null) {

                                if (me != null) {
                                    userFBData = new HashMap<>();
                                    try {
                                        String email = me.getString("email");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    try {
                                        userFBData.put("Gender",me.getString("gender"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    userFBData.put("EventsHosted","0");



                                }
                            }
                        }
                    });
            GraphRequest.executeBatchAsync(request);
        }





        final OnSignupClicked signupClicked;
        try {
            signupClicked = (OnSignupClicked) getContext();
        }catch (ClassCastException e){
            throw  new ClassCastException("Login Activity did not implement the fragment OnSignupClicked Interface!");
        }


        if (token != null) {
            firebaseRef.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {

                @Override
                public void onAuthenticated(AuthData authData) {
                    if (authData != null){
                        userFBData.put("Uid",authData.getUid());
                        userFBData.put("EventsHosted","0");
                        // Add User Data to Firebase..
                        firebaseRef.child("Users").child(authData.getUid()).setValue(userFBData);
                        Log.i("UID ----------------", "onAuthenticated: "+authData.getUid());
                        signupClicked.loginUser(authData);

                    }
                    else{
                        Log.i("Login Error", "onAuthenticated: Login User "+authData.getUid());
                    }
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                }
            });
        } else {
        /* Logged out of Facebook so do a logout from the Firebase app */
            firebaseRef.unauth();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }




}
