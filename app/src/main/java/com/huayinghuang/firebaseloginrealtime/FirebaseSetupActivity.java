package com.huayinghuang.firebaseloginrealtime;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by miahuang on 2016/11/22.
 */

public class FirebaseSetupActivity extends AppCompatActivity {
    protected static final String TAG = "Test";
    protected static final String DB_ONLINE_USER = "online_user";
    protected static final String DB_NUM = "num";
    private static final String DB_USER_LIST = "users";
    private static final String DB_MAIL = "mail";
    private static final String DB_STATUS = "is_login";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    private Handler mHandler;

    protected void updateNumbers(final TextView textView) {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0) {
                    if (textView != null) {
                        textView.setText(String.format(getString(R.string.online), (int) msg.obj));
                    }
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateNumbers(null);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    Log.d(TAG,"onAuthStateChanged:signed_in: " + mUser.getUid());
                    writeDatabase(true, 1);
                    readDatabase();
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

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public FirebaseUser getFirebaseUser() {
        return mUser;
    }

    public FirebaseDatabase getFirebaseDB() {
        return mDatabase;
    }

    public boolean isFirebaseUserNull() {
        return mUser == null;
    }

    private void writeDatabase(boolean isLogin, int value) {
        if (!isFirebaseUserNull()) {
            Log.d(TAG, "writeDatabase");
            //no need to update when logout
            if (isLogin == true) {
                mDatabase.getReference(DB_USER_LIST)
                        .child(getFirebaseUser().getUid())
                        .child(DB_MAIL)
                        .setValue(UserData.getInstance().getMail());
            }
            mDatabase.getReference(DB_USER_LIST)
                    .child(getFirebaseUser().getUid())
                    .child(DB_STATUS)
                    .setValue(isLogin);
            mDatabase.getReference(DB_ONLINE_USER)
                    .child(DB_NUM)
                    .setValue(value);
        }
    }

    private void readDatabase() {
        if (!isFirebaseUserNull()) {
            Log.d(TAG, "readDatabase");
            DatabaseReference reference = mDatabase.getReference(DB_ONLINE_USER).child(DB_NUM);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final int num = dataSnapshot.getValue(Integer.class);
                    Log.d(TAG, "Realtime DB changed value = " + num);
                    final Message message = mHandler.obtainMessage(0,num);
//                    message.what = 0;
//                    message.arg1 = num;
                    message.sendToTarget();
//                myHandler.sendMessage(message);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value: " + databaseError);
                }
            });
        }
    }

    public void signOutFirebase() {
        writeDatabase(false, -1);
        UserData.getInstance().clearAll();
        mAuth.signOut();
    }
}
