package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kosbrother.mongmongwoo.api.UserApi;
import com.kosbrother.mongmongwoo.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public abstract class FbLoginActivity extends AppCompatActivity {

    public static final String TAG = "FbLoginActivity";
    public static final String PIC_URL_FORMAT =
            "https://graph.facebook.com/%s/picture?width=200&height=200";

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                Log.d(TAG, "onCurrentAccessTokenChanged()");
                if (oldAccessToken == null) {
                    Log.i(TAG, "Facebook login");
                } else if (newAccessToken == null) {
                    Log.i(TAG, "Facebook logout");
                    Settings.clearAllUserData(FbLoginActivity.this);
                    onFbLogout();
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    protected void setLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleLoginResult(loginResult);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "cancel login");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i(TAG, "error login");
            }
        });

    }

    private void handleLoginResult(LoginResult loginResult) {
        Log.i(TAG, "success login" + " id: " + loginResult.getAccessToken().getUserId());
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        handleFbRequestResult(object, response);
                    }
                });

        request.setParameters(getRequestParameters());
        request.executeAsync();
    }

    private void handleFbRequestResult(JSONObject object, GraphResponse response) {
        Log.i(TAG, response.toString());
        // Get facebook data from login
        User user = getUser(object);
        if (user != null) {
            Settings.saveUserFBData(getApplicationContext(), user);
            new PostUserTask(user.getJsonString()).execute();
            onFbRequestCompleted(user.getFb_uid(), user.getUser_name(), user.getFb_pic());
        }
    }

    private User getUser(JSONObject object) {
        User user = null;
        try {
            String id = object.getString("id");
            String user_name = object.getString("name");
            String gender = object.getString("gender");
            String email = object.getString("email");
            String picUrl = getPicUrlString(id);
            user = new User(user_name, "", gender, "", "", id, picUrl, email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    private String getPicUrlString(String id) {
        String picUrlString = "";
        try {
            picUrlString = new URL(String.format(PIC_URL_FORMAT, id)).toString();
            Log.i("profile_pic", picUrlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return picUrlString;
    }

    protected abstract void onFbRequestCompleted(String fb_uid, String user_name, String picUrl);

    protected abstract void onFbLogout();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private Bundle getRequestParameters() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        return parameters;
    }

    private class PostUserTask extends AsyncTask<String, Void, Boolean> {
        private final String userJsonString;

        public PostUserTask(String userJsonString) {
            this.userJsonString = userJsonString;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return UserApi.postUser(userJsonString);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Log.i(TAG, "成功上傳");
            } else {
                Log.i(TAG, "上傳失敗");
            }
        }
    }
}
