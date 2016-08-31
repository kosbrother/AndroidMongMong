package com.kosbrother.mongmongwoo.pastorders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;

public class OrderStatusLayoutFactory {

    public static View create(Context context, String status) {
        View statusLayout;
        switch (status) {
            case "訂單成立":
                statusLayout = getStep1Layout(context);
                break;
            case "處理中":
                statusLayout = getStep2Layout(context);
                break;
            case "配送中":
                statusLayout = getStep3Layout(context);
                break;
            case "已到店":
                statusLayout = getStep4Layout(context);
                break;
            case "完成取貨":
                statusLayout = getStep5Layout(context);
                break;
            case "未取訂貨":
            case "訂單變更":
            case "訂單取消":
            default:
                statusLayout = getCancelOrderLayout(context, status);
                break;
        }
        return statusLayout;
    }

    private static View getStep1Layout(Context context) {
        View orderStatus = getOrderStatusLayout(context);
        setStepLayout(context, orderStatus, R.id.order_status_step1_iv, R.id.order_status_step1_tv);
        return orderStatus;
    }

    private static View getStep2Layout(Context context) {
        View orderStatus = getOrderStatusLayout(context);
        setStepLayout(context, orderStatus, R.id.order_status_step2_iv, R.id.order_status_step2_tv);
        return orderStatus;
    }

    private static View getStep3Layout(Context context) {
        View orderStatus = getOrderStatusLayout(context);
        setStepLayout(context, orderStatus, R.id.order_status_step3_iv, R.id.order_status_step3_tv);
        return orderStatus;
    }

    private static View getStep4Layout(Context context) {
        View orderStatusLayout = getOrderStatusLayout(context);
        setStepLayout(context, orderStatusLayout, R.id.order_status_step4_iv, R.id.order_status_step4_tv);
        return orderStatusLayout;
    }

    private static View getStep5Layout(Context context) {
        View orderStatus = getOrderStatusLayout(context);
        setStepLayout(context, orderStatus, R.id.order_status_step5_iv, R.id.order_status_step5_tv);
        return orderStatus;
    }

    private static void setStepLayout(Context context, View orderStatus, int stepImageViewId, int stepTextViewId) {
        ImageView imageView = (ImageView) orderStatus.findViewById(stepImageViewId);
        imageView.setImageResource(R.mipmap.img_order_status_on);

        TextView textView = (TextView) orderStatus.findViewById(stepTextViewId);
        textView.setTextColor(ContextCompat.getColor(context, R.color.orange_f5a623));
    }

    @SuppressLint("InflateParams")
    private static View getOrderStatusLayout(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.order_status, null);
    }

    @SuppressLint("InflateParams")
    private static View getCancelOrderLayout(Context context, String status) {
        View cancelOrderLayout = LayoutInflater.from(context).inflate(R.layout.order_status_cancel_order, null);
        TextView textView = (TextView) cancelOrderLayout.findViewById(R.id.order_status_tv);
        textView.setText(status);
        return cancelOrderLayout;
    }
}
