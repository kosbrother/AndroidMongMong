package com.kosbrother.mongmongwoo.shopinfo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.entity.ShopInfoEntity;

import java.util.List;

public class ShopInfoAdapter extends RecyclerView.Adapter<ShopInfoAdapter.ViewHolder> {

    private final List<ShopInfoEntity> shopInfoEntityList;

    public ShopInfoAdapter(List<ShopInfoEntity> shopInfoEntityList) {
        this.shopInfoEntityList = shopInfoEntityList;
    }

    @Override
    public ShopInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_info, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShopInfoEntity shopInfoEntity = shopInfoEntityList.get(position);
        holder.bind(shopInfoEntity);
    }

    @Override
    public int getItemCount() {
        return shopInfoEntityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView titleTextView;
        public final TextView contentTextView;
        private int originalHeight = 0;
        private boolean mIsViewExpanded = false;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            titleTextView = (TextView) v.findViewById(R.id.faq_title_tv);
            contentTextView = (TextView) v.findViewById(R.id.faq_content_tv);
            if (!mIsViewExpanded) {
                contentTextView.setVisibility(View.GONE);
            }
        }

        public void bind(ShopInfoEntity shopInfoEntity) {
            titleTextView.setText(shopInfoEntity.getQuestion());
            contentTextView.setText(shopInfoEntity.getAnswer());
        }

        @Override
        public void onClick(final View view) {
            if (originalHeight == 0) {
                originalHeight = view.getHeight();
            }

            ValueAnimator valueAnimator;
            if (!mIsViewExpanded) {
                contentTextView.setVisibility(View.VISIBLE);
                mIsViewExpanded = true;
                int widthMeasureSpec =
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.AT_MOST);
                view.measure(widthMeasureSpec, View.MeasureSpec.UNSPECIFIED);
                valueAnimator = ValueAnimator.ofInt(originalHeight, view.getMeasuredHeight());
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_arrow_drop_up_grey_36dp, 0);
                    }
                });
            } else {
                mIsViewExpanded = false;
                valueAnimator = ValueAnimator.ofInt(view.getMeasuredHeight(), originalHeight);
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_arrow_drop_down_grey_36dp, 0);
                        contentTextView.setVisibility(View.GONE);
                    }
                });
            }
            valueAnimator.setDuration(200);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    view.requestLayout();
                }
            });
            valueAnimator.start();
        }
    }
}
