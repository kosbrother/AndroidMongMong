package com.kosbrother.mongmongwoo.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.entity.user.UserIdEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.model.User;

import org.json.JSONObject;

import java.util.Arrays;

import rx.functions.Action1;

public class FacebookLogInActivity extends BaseLoginActivity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_no_toolbar);
        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleLoginResult(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                resultCancelThenFinish();
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                resultCancelThenFinish(error.getMessage());
            }
        };

        loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager, facebookCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleLoginResult(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object != null) {
                            User user = FacebookUtil.getUser(object);
                            onLoginSuccess(user);
                        } else {
                            loginManager.logOut();
                            resultCancelThenFinish("facebook登入失敗，請稍後再試");
                        }
                    }
                });

        request.setParameters(getRequestParameters());
        request.executeAsync();
    }

    public void onLoginSuccess(final User user) {
        Webservice.postUser(user.getUserJsonString(), new Action1<ResponseEntity<UserIdEntity>>() {
            @Override
            public void call(ResponseEntity<UserIdEntity> stringResponseEntity) {
                UserIdEntity data = stringResponseEntity.getData();
                if (data == null) {
                    loginManager.logOut();
                    ResponseEntity.Error error = stringResponseEntity.getError();
                    GAManager.sendError("postUserError", error);
                    resultCancelThenFinish(error.getMessage());
                } else {
                    user.setUserId(data.getUserId());
                    Settings.saveUserData(user);
                    resultOkThenFinish(user.getEmail());
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                loginManager.logOut();
                resultCancelThenFinish(throwable.getMessage());
            }
        });
    }

    private Bundle getRequestParameters() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        return parameters;
    }

}
