package com.huayinghuang.firebaseloginrealtime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by miahuang on 2016/11/22.
 */

public class FirebaseSetupActivity extends AppCompatActivity {
    protected static final String TAG = "Test";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    Log.d(TAG,"onAuthStateChanged:signed_in: " + mUser.getUid());
                } else {
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signOutFirebase();
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public FirebaseUser getFirebaseUser() {
        return mUser;
    }

    public boolean isFirebaseUserNull() {
        return mUser == null;
    }

    private void writeDatabase(int value) {
        String DB_ONLINE_USER = "online_user/num";
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = mDatabase.getReference(DB_ONLINE_USER);
        reference.setValue(value);
    }

    public void signOutFirebase() {
        writeDatabase(-1);
        UserData.getInstance().clearAll();
        mAuth.signOut();
    }
}
