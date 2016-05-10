package com.kosbrother.mongmongwoo.facebook;

import com.kosbrother.mongmongwoo.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookUtil {
    public static final String PIC_URL_FORMAT =
            "https://graph.facebook.com/%s/picture?width=200&height=200";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String GENDER = "gender";
    public static final String EMAIL = "email";
    public static final String FAKE_EMAIL_APPEND = "@mmwooo.fake.com";

    static User getUser(JSONObject fbObject) {
        String id = "";
        String user_name = "";
        String gender = "";
        try {
            id = fbObject.getString(ID);
            user_name = fbObject.getString(NAME);
            gender = fbObject.getString(GENDER);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String email;
        try {
            email = fbObject.getString(EMAIL);
        } catch (JSONException e) {
            email = id + FAKE_EMAIL_APPEND;
        }
        return new User(user_name, "", gender, "", "", id, String.format(PIC_URL_FORMAT, id), email);
    }
}
