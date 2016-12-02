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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDBActivity extends FirebaseSetupActivity implements View.OnClickListener {
    private static final String TAG = "Test";
    private static final String DB_ONLINE_USER = "online_user";
    private static final String DB_NUM = "num";
    private static final String DB_USER_LIST = "users";
    private static final String DB_MAIL = "mail";

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
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
        updateDB();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signout:
                signOutFirebase();
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

        mOnlineUser = (TextView)findViewById(R.id.onlineUsers);
        mOnlineUser.setText(String.format(getString(R.string.online), 1));

        findViewById(R.id.signout).setOnClickListener(this);
    }

    private void updateDB() {
        Log.d(TAG, "updateDB");
        if (!isFirebaseUserNull()) {
            writeDatabase(1);
            readDatabase();
        }
    }

    private void writeDatabase(int value) {
        Log.d(TAG, "writeDatabase");
        Map<String, String> map = new HashMap<>();
        map.put(DB_MAIL, UserData.getInstance().getMail());
        mDatabase.getReference(DB_USER_LIST+"/"+getFirebaseUser().getUid())
                .setValue(map);

//        Map<String, Integer> map1 = new HashMap<>();
//        map1.put(DB_NUM, value);
//        mDatabase.getReference(DB_ONLINE_USER).setValue(map1);
        mDatabase.getReference(DB_ONLINE_USER+"/"+DB_NUM).setValue(value);
    }

    private void readDatabase() {
        Log.d(TAG, "readDatabase");
//        DatabaseReference reference = mDatabase.getReference(DB_ONLINE_USER);
        DatabaseReference reference = mDatabase.getReference(DB_ONLINE_USER+"/"+DB_NUM);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                GenericTypeIndicator<HashMap<String,Integer>> indicator = new GenericTypeIndicator<HashMap<String, Integer>>() {};
//                HashMap map = dataSnapshot.getValue(indicator);
//                final int num1 = (int)map.get(DB_NUM);

                final int num = dataSnapshot.getValue(Integer.class);
                Log.d(TAG,"Realtime DB changed value = " + num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnlineUser.setText(String.format(getString(R.string.online), num));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed to read value: "+databaseError);
            }
        });
    }

}
