package com.kosbrother.mongmongwoo.fcm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.api.Webservice;

import rx.functions.Action1;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Refreshed token: " + refreshedToken);
        }
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        Webservice.postRegistrationId(token, new Action1<String>() {
            @Override
            public void call(String s) {
                if (s != null) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putBoolean(FcmPreferences.UPLOAD_SUCCESS, true);
                    edit.apply();
                }
            }
        });
    }

}