package com.kosbrother.mongmongwoo.checkout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep2EnterEvent;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.utils.KeyboardUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckoutStep2Fragment extends Fragment {

    public static final Pattern VALID_CELL_PHONE_REGEX =
            Pattern.compile("^(\\(?\\+?886\\)?(\\s|-)?9\\d{2}|09\\d{2})(\\s|-)?\\d{3}(\\s|-)?\\d{3}$");
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    protected OnStep2ButtonClickListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStep2ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnStep2ButtonClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GAManager.sendEvent(new CheckoutStep2EnterEvent());
    }

    protected String validateUserInfo(String shipName, String shipPhone, String shipEmail) {
        if (shipName.isEmpty() || shipPhone.isEmpty() || shipEmail.isEmpty()) {
            return "收件人名稱、手機電話跟email不可空白";
        }

        Matcher cellPhoneMatcher = VALID_CELL_PHONE_REGEX.matcher(shipPhone);
        if (!cellPhoneMatcher.matches()) {
            return "請輸入正確的手機電話";
        }

        Matcher emailMatcher = VALID_EMAIL_ADDRESS_REGEX.matcher(shipEmail);
        if (!emailMatcher.matches()) {
            return "請輸入正確的email";
        }

        return "";
    }

    protected void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            KeyboardUtil.hide(getContext(), view);
        }
    }

    public interface OnStep2ButtonClickListener {

        void onStep2NextButtonClick(Store store, String shipAddress, String shipName, String shipPhone, String shipEmail);
    }
}
