package com.kosbrother.mongmongwoo.facebook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.UserApi;
import com.kosbrother.mongmongwoo.model.User;

import org.json.JSONObject;

public class FacebookManager {

    public static final String TAG = "FacebookManager";

    private static FacebookManager instance;
    private FacebookListener listener;

    private FacebookManager() {
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                if (oldAccessToken == null) {
                    handleLoginResult(newAccessToken);
                } else if (newAccessToken == null) {
                    Settings.clearAllUserData();
                    listener.onFbLogout();
                }
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
        CallbackManager.Factory.create().onActivityResult(requestCode, resultCode, data);
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
        User user = FacebookUtil.getUser(object);
        Settings.saveUserFBData(user);
        new PostUserTask(user.getJsonString()).execute();
        listener.onFbRequestCompleted(user.getFb_uid(), user.getUser_name(), user.getFb_pic());
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

    public interface FacebookListener {
        void onFbRequestCompleted(String fb_uid, String user_name, String fb_pic);

        void onFbLogout();
    }
}
