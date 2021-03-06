package com.tobipristupin.simplerun.ui.main;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


public class MainPresenter {

    private MainView view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;

    public MainPresenter(MainView view) {
        this.view = view;
        user = firebaseAuth.getCurrentUser();
    }

    public void onCreateView(){
        authenticate();
    }


    public void authenticate(){
        initAuthStateListener();

        if (!userLoggedIn()){
            view.loadLogIn();
        } else {
            view.initViews();

        }
    }

    public void onResumeView(){
        firebaseAuth.addAuthStateListener(authListener);
    }

    public void onPauseView(){
        firebaseAuth.removeAuthStateListener(authListener);
    }

    private boolean userLoggedIn(){
        return user != null;
    }

    private void initAuthStateListener(){
        authListener = firebaseAuth -> {
            if (user == null){
                view.loadLogIn();
            }
        };
    }

    public void onAboutClicked() {
        view.showAboutDialog();
    }

    public void onRateUsClicked() {
        view.sendPlayStoreRatingIntent();
    }

    public Uri getUserPhotoUrl(){
        Uri uri = null;
        /* Dirty hack to fromString photo url from google provider and not firebase, because firebase stores
         image in low quality*/
        for (UserInfo info : user.getProviderData()){
            if (info.getProviderId().equals("google.com")){
                uri = info.getPhotoUrl();
            }
        }

        return uri;
    }

    public String getUserDisplayName(){
        return user.getDisplayName();
    }

    public String getUserEmail(){
        return user.getEmail();
    }

}
