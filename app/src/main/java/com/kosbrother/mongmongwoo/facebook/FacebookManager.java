package com.kosbrother.mongmongwoo.facebook;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class FacebookManager {

    public static final String TAG = "FacebookManager";
    public static final String PIC_URL_FORMAT =
            "https://graph.facebook.com/%s/picture?width=200&height=200";

    private static FacebookManager instance;
    private FacebookListener listener;

    private FacebookManager(final Context context) {
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                if (oldAccessToken == null) {
                    Log.i(TAG, "login");
                    handleLoginResult(context, newAccessToken);
                } else if (newAccessToken == null) {
                    Log.i(TAG, "logout");
                    Settings.clearAllUserData(context);
                    FacebookManager.this.listener.onFbLogout();
                }
            }
        };
    }

    public static FacebookManager getInstance(Context context) {
        if (instance == null) {
            instance = new FacebookManager(context);
        }
        return instance;
    }

    public void setListener(FacebookListener listener) {
        this.listener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackManager.Factory.create().onActivityResult(requestCode, resultCode, data);
    }

    private void handleLoginResult(final Context context, AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        handleFbRequestResult(context, object);
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

    private void handleFbRequestResult(Context context, JSONObject object) {
        User user = getUser(object);
        Settings.saveUserFBData(context, user);
        new PostUserTask(user.getJsonString()).execute();
        listener.onFbRequestCompleted(user.getFb_uid(), user.getUser_name(), user.getFb_pic());
    }

    private User getUser(JSONObject object) {
        String id = "";
        String user_name = "";
        String gender = "";
        String picUrl = "";
        try {
            id = object.getString("id");
            user_name = object.getString("name");
            gender = object.getString("gender");
            picUrl = new URL(String.format(PIC_URL_FORMAT, id)).toString();
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }

        String email = id + "@mmwooo.fake.com";
        try {
            email = object.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new User(user_name, "", gender, "", "", id, picUrl, email);
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
