package com.huayinghuang.firebaseloginrealtime;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RealtimeDBActivity extends FirebaseSetupActivity {
    private long mFirstClick = 0;
    private TextView mOnlineUser;

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

}
