package com.kosbrother.mongmongwoo.mynotification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.widget.CenteredImageSpan;

import java.util.List;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.ViewHolder> {

    private List<MyNotification> myNotificationList;
    private Context context;
    private OnMyNotificationClickListener listener;

    MyNotificationAdapter(List<MyNotification> myNotificationList, OnMyNotificationClickListener listener) {
        this.myNotificationList = myNotificationList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_my_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyNotification myNotification = myNotificationList.get(position);

        boolean isNew = myNotification.isNew();
        if (isNew) {
            CenteredImageSpan imageSpan = new CenteredImageSpan(context, R.mipmap.ic_new);
            String displayTitle = myNotification.getTitle() + "    ";
            SpannableString ss = new SpannableString(displayTitle);
            ss.setSpan(imageSpan, displayTitle.length() - 1, displayTitle.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.titleTextView.setText(ss);
        } else {
            holder.titleTextView.setText(myNotification.getTitle());
        }

        holder.dateTextView.setText(myNotification.getMonthAndDay());
        holder.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return myNotificationList.size();
    }

    public void setMyNotificationRead(int adapterPosition) {
        MyNotification myNotification = myNotificationList.get(adapterPosition);
        myNotification.setNew(false);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleTextView;
        private final TextView dateTextView;
        private OnMyNotificationClickListener listener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleTextView = (TextView) itemView.findViewById(R.id.my_notification_title_tv);
            dateTextView = (TextView) itemView.findViewById(R.id.my_notification_date_tv);
        }

        public void setOnClickListener(OnMyNotificationClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onMyNotificationClick(getAdapterPosition());
            }
        }
    }

    public interface OnMyNotificationClickListener {

        void onMyNotificationClick(int adapterPosition);
    }
}
