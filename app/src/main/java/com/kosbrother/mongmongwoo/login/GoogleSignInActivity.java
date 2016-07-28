package com.kosbrother.mongmongwoo.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.entity.user.UserIdEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.model.User;

import rx.functions.Action1;

public class GoogleSignInActivity extends BaseLoginActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_no_toolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        resultCancelThenFinish(connectionResult.getErrorMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult(resultCode, data);
        }
    }

    private void handleSignInResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount signInAccount = result.getSignInAccount();
                if (signInAccount != null) {
                    User user = getUser(signInAccount);
                    onSignInSuccess(user);
                } else {
                    signOut();
                    resultCancelThenFinish();
                }
            } else {
                resultCancelThenFinish(result.getStatus().getStatusMessage());
            }
        } else {
            resultCancelThenFinish();
        }
    }

    public void onSignInSuccess(final User user) {
        Webservice.postUser(user.getUserJsonString(), new Action1<ResponseEntity<UserIdEntity>>() {
            @Override
            public void call(ResponseEntity<UserIdEntity> stringResponseEntity) {
                UserIdEntity data = stringResponseEntity.getData();
                if (data == null) {
                    signOut();
                    ResponseEntity.Error error = stringResponseEntity.getError();
                    GAManager.sendError("postUserError", error);
                    resultCancelThenFinish(error.getMessage());
                } else {
                    user.setUserId(data.getUserId());
                    Settings.saveUserData(user);
                    String email = user.getEmail();
                    resultOkThenFinish(email);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                signOut();
                resultCancelThenFinish(throwable.getMessage());
            }
        });
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    private User getUser(GoogleSignInAccount acct) {
        String pic;
        if (acct.getPhotoUrl() == null) {
            pic = "";
        } else {
            pic = acct.getPhotoUrl().toString();
        }
        return new User(acct.getDisplayName(),
                "",
                acct.getId(),
                pic,
                acct.getEmail(),
                "google");
    }
}
