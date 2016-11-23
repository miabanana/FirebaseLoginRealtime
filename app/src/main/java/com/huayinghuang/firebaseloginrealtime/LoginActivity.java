package com.huayinghuang.firebaseloginrealtime;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends FirebaseSetupActivity implements View.OnClickListener {
    private static final int GOOGLE = 100;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        connectGoogle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                if (isFirebaseUserNull()) {
                    signInMail("test@gmail.com", "testtest");
                } else {
                    signOutFirebase();
                }
                break;
            case R.id.googleSignIn:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GOOGLE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d(TAG, "handleSignInResult:" + result.isSuccess());
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);

                    String name = account != null ? account.getDisplayName() : null;
                    String id = account != null ? account.getId() : null;
                    String idToken = account != null ? account.getIdToken() : null;
                    String mail = account != null ? account.getEmail() : null;
                    String givenName = account != null ? account.getGivenName() : null;
                    String familyName = account != null ? account.getFamilyName() : null;
                    Uri photoUri = account != null ? account.getPhotoUrl() : null;

                    UserData.getInstance().setId(id).setIdToken(idToken).setMail(mail)
                            .setDisplayName(name).setGivenName(givenName).setFamilyName(familyName)
                            .setPhotoUri(photoUri);
                    Log.d(TAG, "userId = "+id+"\n userIdToken = "+idToken
                            +"\nuserMail = "+mail +"\nuserGivenName = "+givenName
                            +"\nuserFamilyName = "+familyName +"\nphotoUri = "+photoUri);

                    startActivity(new Intent(getApplicationContext(), RealtimeDBActivity.class));
                } else {
                    Toast.makeText(this, R.string.signin_fail, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initView() {
        findViewById(R.id.googleSignIn).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
    }

    private void connectGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .requestScopes(
                        new Scope(Scopes.PLUS_LOGIN), //所屬年齡層，使用語言
                        new Scope(Scopes.APP_STATE), //查看及管理此應用程式的資料
                        new Scope(Scopes.CLOUD_SAVE), //查看及管理Google雲端資料儲存庫行動版的資料
                        new Scope(Scopes.DRIVE_APPFOLDER), //查看及管理此API在Google雲端硬碟中的設定資料
                        new Scope(Scopes.DRIVE_FILE), //查看及管理您透過這個應用程式開啟或建立的Google雲端硬碟檔案和資料夾
                        new Scope(Scopes.FITNESS_ACTIVITY_READ), //查看您的Google Fit活動資訊
                        new Scope(Scopes.FITNESS_LOCATION_READ_WRITE), //查看及儲存您的Google Fit位置資料
                        new Scope(Scopes.GAMES), //分享您的Google+個人資料訊息，以及查看和管理遊戲活動
                        new Scope(Scopes.PLUS_ME), //不需權限
                        new Scope(Scopes.PLUS_MOMENTS), //不需
                        new Scope(Scopes.PROFILE), //不需
                        new Scope(Scopes.EMAIL) //不需
                )
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), R.string.connect_google_fail, Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "sign in with Google credential onComplete: " + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "sign in with Google credential: ", task.getException());
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.signin_fail),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void signInMail(final String mailAddr, String pwd) {
        //sign up new user
        getFirebaseAuth().createUserWithEmailAndPassword(mailAddr, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Sign up succeed? " + task.isSuccessful());
                        String msg = task.isSuccessful() ?
                                getString(R.string.signup_success) : getString(R.string.signup_fail);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

        //sign in old user
        getFirebaseAuth().signInWithEmailAndPassword(mailAddr, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Sign in succeed? " + task.isSuccessful());
                        String msg = task.isSuccessful() ?
                                getString(R.string.signin_success) : getString(R.string.signin_fail);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful()) {
                            UserData.getInstance().setDisplayName(mailAddr);
                            startActivity(new Intent(getApplicationContext(), RealtimeDBActivity.class));
                        }
                    }
                });
    }

}
