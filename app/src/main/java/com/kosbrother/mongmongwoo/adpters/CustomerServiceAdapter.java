package com.kosbrother.mongmongwoo.adpters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;

public class CustomerServiceAdapter extends RecyclerView.Adapter<CustomerServiceAdapter.ViewHolder> {

    private final OnCustomerServiceItemClickListener listener;

    public CustomerServiceAdapter(OnCustomerServiceItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_service, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomerService customerService = new CustomerService(position).invoke();
        holder.imageView.setImageResource(customerService.getImageRes());
        holder.textView.setText(customerService.getText());
        holder.bind(listener);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View mItemView;
        private final ImageView imageView;
        private final TextView textView;

        private OnCustomerServiceItemClickListener mListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.item_customer_service_ig);
            textView = (TextView) itemView.findViewById(R.id.item_customer_service_tv);
        }

        public void bind(final OnCustomerServiceItemClickListener listener) {
            mListener = listener;
            mItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getLayoutPosition() == 0) {
                mListener.onLineCustomerServiceClick(textView.getText().toString());
            } else {
                mListener.onFacebookCustomerServiceClick(textView.getText().toString());
            }
        }
    }

    private class CustomerService {
        private int position;
        private String text;
        private int imageRes;

        public CustomerService(int position) {
            this.position = position;
        }

        public String getText() {
            return text;
        }

        public int getImageRes() {
            return imageRes;
        }

        public CustomerService invoke() {
            if (position == 0) {
                text = "經由LINE傳送";
                imageRes = R.mipmap.ic_line;
            } else {
                text = "經由粉絲專頁傳送";
                imageRes = R.mipmap.ic_fb;
            }
            return this;
        }
    }

    public interface OnCustomerServiceItemClickListener {
        void onLineCustomerServiceClick(String text);

        void onFacebookCustomerServiceClick(String text);
    }
}
