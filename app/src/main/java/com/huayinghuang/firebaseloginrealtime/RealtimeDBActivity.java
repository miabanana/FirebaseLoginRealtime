package com.huayinghuang.firebaseloginrealtime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RealtimeDBActivity extends FirebaseSetupActivity {
    private long mFirstClick = 0;
    private TextView mOnlineUser;

//    public Handler mHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 0) {
//                mOnlineUser.setText(String.format(getString(R.string.online), (int)msg.obj));
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_db);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        updateNumbers(mOnlineUser);
//        updateOnlineUserNum();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mFirstClick > 2000) {
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            mFirstClick = System.currentTimeMillis();
        } else {
            signOutFirebase();
            finish();
        }
    }

    private void initView() {
        ImageView photo = (ImageView)findViewById(R.id.photo);
        photo.setImageURI(UserData.getInstance().getPhotoUri());

        TextView user = (TextView)findViewById(R.id.user);
        user.setText(String.format(getString(R.string.user), UserData.getInstance().getDisplayName()));

        mOnlineUser = (TextView)findViewById(R.id.onlineUsers);
        mOnlineUser.setText(String.format(getString(R.string.online), 0));

        findViewById(R.id.signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutFirebase();
                finish();
            }
        });
    }

    private void updateOnlineUserNum() {
        Log.d(TAG, "update online user numbers");
        DatabaseReference reference = getFirebaseDB().getReference(DB_ONLINE_USER).child(DB_NUM);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int num = dataSnapshot.getValue(Integer.class);
                Log.d(TAG,"Realtime DB changed value = " + num);
                mOnlineUser.setText(String.format(getString(R.string.online), num));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed to read value: "+databaseError);
            }
        });
    }

}
