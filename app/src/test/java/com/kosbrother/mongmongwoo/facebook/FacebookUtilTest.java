package com.kosbrother.mongmongwoo.facebook;

import android.support.annotation.NonNull;

import com.kosbrother.mongmongwoo.model.User;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static com.kosbrother.mongmongwoo.facebook.FacebookUtil.EMAIL;
import static com.kosbrother.mongmongwoo.facebook.FacebookUtil.FAKE_EMAIL_APPEND;
import static com.kosbrother.mongmongwoo.facebook.FacebookUtil.GENDER;
import static com.kosbrother.mongmongwoo.facebook.FacebookUtil.ID;
import static com.kosbrother.mongmongwoo.facebook.FacebookUtil.NAME;
import static com.kosbrother.mongmongwoo.facebook.FacebookUtil.PIC_URL_FORMAT;

public class FacebookUtilTest {

    public static final String TEST_ID = "testId";
    public static final String TEST_GENDER = "testGender";
    public static final String TEST_NAME = "testName";
    public static final String TEST_EMAIL = "testEmail";
    public static final String TEST_FAKE_EMAIL = TEST_ID + FAKE_EMAIL_APPEND;

    @Test
    public void testGetUser_withEmailField() throws Exception {
        JSONObject fbJsonObject = getFbJsonObjectWithEmail();

        User actual = FacebookUtil.getUser(fbJsonObject);

        Assert.assertEquals(TEST_ID, actual.getFb_uid());
        Assert.assertEquals(TEST_NAME, actual.getUserName());
        Assert.assertEquals(TEST_GENDER, actual.getGender());
        Assert.assertEquals(String.format(PIC_URL_FORMAT, TEST_ID), actual.getFb_pic());
        Assert.assertEquals(TEST_EMAIL, actual.getEmail());
    }

    @Test
    public void testGetUser_noEmailField() throws Exception {
        JSONObject fbJsonObject = getFbJsonObjectWithoutEmail();

        User actual = FacebookUtil.getUser(fbJsonObject);

        Assert.assertEquals(TEST_ID, actual.getFb_uid());
        Assert.assertEquals(TEST_NAME, actual.getUserName());
        Assert.assertEquals(TEST_GENDER, actual.getGender());
        Assert.assertEquals(String.format(PIC_URL_FORMAT, TEST_ID), actual.getFb_pic());
        Assert.assertEquals(TEST_FAKE_EMAIL, actual.getEmail());
    }

    @NonNull
    private JSONObject getFbJsonObjectWithEmail() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, TEST_ID);
        jsonObject.put(GENDER, TEST_GENDER);
        jsonObject.put(NAME, TEST_NAME);
        jsonObject.put(EMAIL, TEST_EMAIL);
        return jsonObject;
    }

    @NonNull
    private JSONObject getFbJsonObjectWithoutEmail() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, TEST_ID);
        jsonObject.put(GENDER, TEST_GENDER);
        jsonObject.put(NAME, TEST_NAME);
        return jsonObject;
    }

}