package com.kk.makexplore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends AppCompatActivity implements LoginFragment.OnSignupClicked,UserRegistration.onResiterSuccessful{
   Firebase firebaseRef;

    private static final String FIREBASEREF = "https://projectexplore.firebaseio.com/";
    Fragment mFrag;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        Firebase.setAndroidContext(this);
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        setContentView(R.layout.mainactivity_container_layout);
        setupWindowAnimations();
        firebaseRef = new Firebase(FIREBASEREF);
/*
        if(savedInstanceState != null){
            mFrag = getSupportFragmentManager().getFragment(savedInstanceState,"mFrag");
        }
        else{
            mFrag = LoginFragment.newInstance(R.id.loginFrag,firebaseRef);
        }
*/
        mFrag = LoginFragment.newInstance(R.id.loginFrag,firebaseRef);
        getSupportFragmentManager().beginTransaction().replace(R.id.loginContainer,mFrag).commit();

    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }

    @Override
    public void registerUser() {
        mFrag = UserRegistration.newInstance(R.id.userRegistrationFrag,firebaseRef);
        getSupportFragmentManager().beginTransaction().replace(R.id.loginContainer,mFrag).commit();
    }

    @Override
    public void loginUser(AuthData authData) {
        Intent intent;
        intent = new Intent(LoginActivity.this,FrontPageActivity.class);
        intent.putExtra("USER_ID",authData.getUid());
        startActivity(intent);

    }


    @Override
    public void OnRegisterSuccessful(AuthData authData) {
        Intent intent;
        intent = new Intent(LoginActivity.this,FrontPageActivity.class);
        intent.putExtra("USER_ID",authData.getUid());
        startActivity(intent);

    }
}
