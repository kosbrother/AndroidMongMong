package com.kosbrother.mongmongwoo.facebook;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.model.User;

import org.json.JSONObject;

import java.util.Arrays;

import rx.functions.Action1;

public class FacebookManager {

    public static final String TAG = "FacebookManager";

    private static FacebookManager instance;
    private FacebookListener listener;
    private final CallbackManager callbackManager;
    private FacebookCallback<LoginResult> facebookCallback;

    private FacebookManager() {
        callbackManager = CallbackManager.Factory.create();
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                if (newAccessToken == null) {
                    Settings.clearAllUserData();
                    listener.onFbLogout();
                }
            }
        };
        facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleLoginResult(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        };
    }

    public static FacebookManager getInstance() {
        if (instance == null) {
            instance = new FacebookManager();
        }
        return instance;
    }

    public void setListener(FacebookListener listener) {
        this.listener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleLoginResult(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        handleFbRequestResult(object);
                    }
                });

        request.setParameters(getRequestParameters());
        request.executeAsync();
    }

    private Bundle getRequestParameters() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        return parameters;
    }

    private void handleFbRequestResult(JSONObject object) {
        if (object == null) {
            LoginManager.getInstance().logOut();
            listener.onFbLogout();
            return;
        }
        User user = FacebookUtil.getUser(object);
        Settings.saveUserFBData(user);
        Webservice.postUser(user.getJsonString(), new Action1<ResponseEntity<String>>() {
            @Override
            public void call(ResponseEntity<String> stringResponseEntity) {
                String data = stringResponseEntity.getData();
                if (data == null) {
                    GAManager.sendError("postUserError", stringResponseEntity.getError());
                }
            }
        });
        listener.onFbRequestCompleted(user.getFb_uid(), user.getUserName(), user.getFb_pic());
    }

    public void setLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, facebookCallback);
    }

    public void startLogin(FbLoginActivity fbLoginActivity) {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(fbLoginActivity,
                Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager, facebookCallback);
    }

    public interface FacebookListener {
        void onFbRequestCompleted(String fb_uid, String user_name, String fb_pic);

        void onFbLogout();
    }
}
