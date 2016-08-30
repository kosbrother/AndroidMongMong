package com.kosbrother.mongmongwoo.myshoppingpoints.detail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;

import java.util.List;

public class ShoppingPointsDetailAdapter extends RecyclerView.Adapter<ShoppingPointsDetailAdapter.ViewHolder> {

    private final List<ShoppingPointsViewModel> viewModels;
    private Context context;

    public ShoppingPointsDetailAdapter(List<ShoppingPointsViewModel> viewModels) {
        this.viewModels = viewModels;
    }

    @Override
    public ShoppingPointsDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_my_shopping_points_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingPointsViewModel viewModel = viewModels.get(position);
        holder.bind(context, viewModel);
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageView;
        private final TextView pointTypeTextView;
        private final TextView validUntilTextView;
        private final TextView descriptionTextView;
        private final TextView amountTextView;
        private final LinearLayout recordsLinearLayout;

        private int originalHeight = 0;
        private boolean mIsViewExpanded = false;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            imageView = (ImageView) v.findViewById(R.id.item_my_shopping_points_detail_iv);
            pointTypeTextView = (TextView) v.findViewById(R.id.item_my_shopping_points_detail_point_type_tv);
            validUntilTextView = (TextView) v.findViewById(R.id.item_my_shopping_points_detail_valid_until_tv);
            descriptionTextView = (TextView) v.findViewById(R.id.item_my_shopping_points_detail_description_tv);
            amountTextView = (TextView) v.findViewById(R.id.item_my_shopping_points_detail_amount_tv);

            recordsLinearLayout = (LinearLayout) v.findViewById(R.id.item_my_shopping_points_detail_records_ll);
            if (!mIsViewExpanded) {
                recordsLinearLayout.setVisibility(View.GONE);
            }
        }

        public void bind(Context context, ShoppingPointsViewModel viewModel) {
            boolean valid = viewModel.isValid();
            imageView.setImageResource(valid ? R.mipmap.ic_money_valid : R.mipmap.ic_money_invalid);

            pointTypeTextView.setText(viewModel.getPointTypeText());

            String validUntilText = viewModel.getValidUntilText();
            if (validUntilText == null || validUntilText.isEmpty()) {
                validUntilTextView.setVisibility(View.GONE);
            } else {
                validUntilTextView.setVisibility(View.VISIBLE);
                validUntilTextView.setText(validUntilText);
            }

            String descriptionText = viewModel.getDescriptionText();
            if (descriptionText == null || descriptionText.isEmpty()) {
                descriptionTextView.setVisibility(View.GONE);
            } else {
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(descriptionText);
            }

            amountTextView.setText(viewModel.getAmountText());

            addRecordsItemView(context, viewModel.getShoppingPointsRecordsViewModels());
        }

        @Override
        public void onClick(final View view) {
            if (originalHeight == 0) {
                originalHeight = view.getHeight();
            }

            ValueAnimator valueAnimator;
            if (!mIsViewExpanded) {
                recordsLinearLayout.setVisibility(View.VISIBLE);
                mIsViewExpanded = true;
                int widthMeasureSpec =
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.AT_MOST);
                view.measure(widthMeasureSpec, View.MeasureSpec.UNSPECIFIED);

                valueAnimator = ValueAnimator.ofInt(originalHeight, view.getMeasuredHeight());
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        amountTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_arrow_drop_up_grey_36dp, 0);
                    }
                });
            } else {
                mIsViewExpanded = false;
                valueAnimator = ValueAnimator.ofInt(view.getMeasuredHeight(), originalHeight);
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        amountTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_arrow_drop_down_grey_36dp, 0);
                        recordsLinearLayout.setVisibility(View.GONE);
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

        private void addRecordsItemView(Context context, List<ShoppingPointsRecordsViewModel> recordsViewModels) {
            for (ShoppingPointsRecordsViewModel recordsViewModel : recordsViewModels) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_shopping_points_record, null);

                TextView orderIdTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_record_order_id_tv);
                orderIdTextView.setText(recordsViewModel.getOrderIdText());
                TextView amountTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_record_amount_tv);
                amountTextView.setText(recordsViewModel.getAmountText());
                if (recordsViewModel.isAmountPositive()) {
                    amountTextView.setTextColor(ContextCompat.getColor(context, R.color.green_text));
                } else {
                    amountTextView.setTextColor(ContextCompat.getColor(context, R.color.red_text));
                }
                TextView recordBalanceTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_record_balance_tv);
                recordBalanceTextView.setText(recordsViewModel.getBalanceText());
                TextView createAtTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_record_create_at_tv);
                createAtTextView.setText(recordsViewModel.getCreateAtText());

                recordsLinearLayout.addView(itemView);
            }
        }
    }
}
