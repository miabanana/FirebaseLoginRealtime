package com.huayinghuang.firebaseloginrealtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RealtimeDBActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Test";

    private long mFirstClick = 0;
    private boolean mFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_db);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG,"FirebaseUser is null? "+ (firebaseUser == null? "null": "not null"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mFirstClick > 2000) {
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            mFirstClick = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void initView() {
        ImageView photo = (ImageView)findViewById(R.id.photo);
        photo.setImageURI(UserData.getInstance().getPhotoUri());

        TextView user = (TextView)findViewById(R.id.user);
        user.setText(String.format(getString(R.string.user), UserData.getInstance().getDisplayName()));

        TextView online = (TextView)findViewById(R.id.onlineUsers);
        online.setText(String.format(getString(R.string.online), 1));

        findViewById(R.id.signout).setOnClickListener(this);
    }

}
