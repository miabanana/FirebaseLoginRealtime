package com.huayinghuang.firebaseloginrealtime;

import android.net.Uri;

/**
 * Created by miahuang on 2016/11/22.
 */

class UserData {
    private String id;
    private String idToken;
    private String mail;
    private String displayName;
    private String givenName;
    private String familyName;
    private Uri photoUri;
    private static final UserData userData = new UserData();

    private UserData() {}

    static UserData getInstance() {
        return userData;
    }

    void clearAll() {
        id = null;
        idToken = null;
        mail = null;
        displayName = null;
        givenName = null;
        familyName = null;
        photoUri = null;
    }

    public String getId() {
        return id;
    }

    public UserData setId(String id) {
        synchronized (userData) {
            this.id = id;
        }

        return userData;
    }

    public String getIdToken() {
        return idToken;
    }

    public UserData setIdToken(String idToken) {
        synchronized (userData) {
            this.idToken = idToken;
        }
        return userData;
    }

    public String getMail() {
        return mail;
    }

    public UserData setMail(String mail) {
        synchronized (userData) {
            this.mail = mail;
        }
        return userData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserData setDisplayName(String displayName) {
        synchronized (userData) {
            this.displayName = displayName;
        }
        return userData;
    }

    public String getGivenName() {
        return givenName;
    }

    public UserData setGivenName(String givenName) {
        synchronized (userData) {
            this.givenName = givenName;
        }
        return userData;
    }

    public String getFamilyName() {
        return familyName;
    }

    public UserData setFamilyName(String familyName) {
        synchronized (userData) {
            this.familyName = familyName;
        }
        return userData;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public UserData setPhotoUri(Uri photoUri) {
        synchronized (userData) {
            this.photoUri = photoUri;
        }
        return userData;
    }
}
