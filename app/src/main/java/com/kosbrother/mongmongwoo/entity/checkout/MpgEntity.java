package com.kosbrother.mongmongwoo.entity.checkout;

import com.kosbrother.mongmongwoo.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;

public class MpgEntity {

    private static final String MERCHANT_ID = BuildConfig.DEBUG ?
            "12465125" : "312128390";
    private static final String HASH_KEY = BuildConfig.DEBUG ?
            "wmUZxGOvhnFZjn42LmWaNMarirb5c8MJ" : "XSBetD89lZ0hSTwckwZwWTB9fyCBzMow";
    private static final String HASH_IV = BuildConfig.DEBUG ?
            "vHJ2oyxFVcFfAKhm" : "FZK8DThZShgLaMYv";
    /**
     * JSON or String
     */
    private static final String RESPOND_TYPE = "JSON";
    /**
     * 0 = login Pay2Go not required
     * 1 = login Pay2go required
     */
    private static final int LOGIN_TYPE = 0;
    /**
     * 0 = unable
     * 1 = enable
     */
    private static final int CREDIT = 1;

    private String checkValue;
    private String timeStamp;
    private String version = "1.2";
    private String merchantOrderNo;
    /**
     * order price
     */
    private int amt;
    private String itemDesc;
    private String email;

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMerchantOrderNo(String merchantOrderNo) {
        this.merchantOrderNo = merchantOrderNo;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPostData() {
        createCheckValue();
        HashMap<String, String> map = new HashMap<>();
        map.put("MerchantID", MERCHANT_ID);
        map.put("RespondType", RESPOND_TYPE);
        map.put("CheckValue", checkValue);
        map.put("TimeStamp", timeStamp);
        map.put("Version", version);
        map.put("MerchantOrderNo", merchantOrderNo);
        map.put("Amt", String.valueOf(amt));
        map.put("ItemDesc", itemDesc);
        map.put("Email", email);
        map.put("LoginType", String.valueOf(LOGIN_TYPE));
        map.put("CREDIT", String.valueOf(CREDIT));

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            stringBuilder.append('&').append(key).append('=')
                    .append(map.get(key));
        }
        stringBuilder.delete(0, 1);

        byte[] postData = new byte[0];
        try {
            postData = stringBuilder.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private void createCheckValue() {
        String checkValue =
                "HashKey=" + HASH_KEY +
                        "&Amt=" + amt +
                        "&MerchantID=" + MERCHANT_ID +
                        "&MerchantOrderNo=" + merchantOrderNo +
                        "&TimeStamp=" + timeStamp +
                        "&Version=" + version +
                        "&HashIV=" + HASH_IV;
        this.checkValue = encrypt(checkValue);
    }

    private String encrypt(String s) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(s.getBytes());
            return byte2hex(sha.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String byte2hex(byte[] b) {
        String hs = "";
        for (byte aB : b) {
            String stmp = (Integer.toHexString(aB & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }
}
