package com.kosbrother.mongmongwoo.checkout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.common.ProductViewModel;
import com.kosbrother.mongmongwoo.databinding.FragmentStep1Binding;
import com.kosbrother.mongmongwoo.databinding.ItemShoppingCartProductBinding;
import com.kosbrother.mongmongwoo.entity.ShipInfoEntity;
import com.kosbrother.mongmongwoo.entity.checkout.CheckoutPostEntity;
import com.kosbrother.mongmongwoo.entity.checkout.CheckoutResultEntity;
import com.kosbrother.mongmongwoo.entity.checkout.OrderPrice;
import com.kosbrother.mongmongwoo.facebookevent.FacebookLogger;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep1ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep1EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.login.LoginActivity;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;
import com.kosbrother.mongmongwoo.utils.TextViewUtil;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

import java.util.List;

import rx.functions.Action1;

public class Step1Fragment extends Fragment {

    private static final int REQUEST_LOGIN = 111;
    public static final int SHIP_FEE = 90;

    private FragmentStep1Binding binding;

    private View loadingView;
    private LinearLayout goodsContainerLinearLayout;
    private TextView deliveryTextView;
    private CenterProgressDialog progressDialog;

    private OrderPrice orderPrice;
    private boolean isSpendShoppingPoint = false;
    private int tempCount;
    private OnStep1ButtonClickListener mCallback;
    private String selectedDelivery = "請選擇";

    private String email = Settings.getEmail();
    private DataManager.ApiCallBack postCheckoutCallBack = new DataManager.ApiCallBack() {
        @Override
        public void onError(String errorMessage) {
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            loadingView.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(Object data) {
            CheckoutResultEntity result = (CheckoutResultEntity) data;
            Step1FragmentViewModel viewModel = new Step1FragmentViewModel(result, Settings.checkIsLogIn());
            binding.setViewModel(viewModel);

            binding.executePendingBindings();
            TextView shipCampaignTextView = (TextView) binding.getRoot()
                    .findViewById(R.id.fragment_step1_ship_campaign_tv);
            shipCampaignTextView.setText(
                    CampaignTextUtil.getCampaignColorSpannable(
                            getActivity(), shipCampaignTextView.getText()));

            View view = binding.getRoot();

            RecyclerView activityCampaignRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_step1_activity_campaign_rv);
            activityCampaignRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            List<ActivityCampaignViewModel> activityCampaignViewModels =
                    result.getOrderPrice().getActivityCampaignViewModels();
            ActivityCampaignAdapter adapter = new ActivityCampaignAdapter(activityCampaignViewModels);
            activityCampaignRecyclerView.setAdapter(adapter);

            RecyclerView shoppingPointCampaignRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_step1_shopping_point_campaign_rv);
            shoppingPointCampaignRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            List<ShoppingPointCampaignViewModel> shoppingPointCampaignViewModels =
                    result.getOrderPrice().getShoppingPointCampaignViewModels();
            ShoppingPointCampaignAdapter shoppingPointCampaignAdapter =
                    new ShoppingPointCampaignAdapter(shoppingPointCampaignViewModels);
            shoppingPointCampaignRecyclerView.setAdapter(shoppingPointCampaignAdapter);

            orderPrice = result.getOrderPrice();
            ShoppingCartManager.getInstance().storeShoppingItems(result.getProducts());

            updateGoodsLinearLayout(result.getProductViewModels());
            loadingView.setVisibility(View.GONE);
        }
    };

    private DataManager.CheckResultCallBack checkPickupRecordCallBack = new DataManager.CheckResultCallBack() {
        @Override
        public void onSuccessWithErrorMessage(String errorMessage) {
            progressDialog.dismiss();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("錯誤");
            alertDialogBuilder.setMessage(errorMessage);
            alertDialogBuilder.setPositiveButton("確認", null);
            alertDialogBuilder.show();
        }

        @Override
        public void onError(String errorMessage) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(Object data) {
            progressDialog.dismiss();
            mCallback.onStep1NextButtonClick(email, selectedDelivery, isSpendShoppingPoint);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStep1ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnStep1ButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
        if (products == null || products.size() == 0) {
            return inflater.inflate(R.layout.view_stub_empty_shopping_car, container, false);
        }

        logInitiatedCheckoutEvent();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_step1, container, false);
        View view = binding.getRoot();
        binding.setStep1Fragment(this);

        loadingView = view.findViewById(R.id.loading_fragment_fl);
        goodsContainerLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_step1_product_container_ll);

        deliveryTextView = (TextView) view.findViewById(R.id.fragment_step1_delivery_tv);
        deliveryTextView.setText(selectedDelivery);

        setShoppingPointsCheckedChangedListener(view);

        postCheckout();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GAManager.sendEvent(new CheckoutStep1EnterEvent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                email = data.getStringExtra(LoginActivity.EXTRA_STRING_EMAIL);
                postCheckout();
            }
        }
    }

    @Override
    public void onDestroy() {
        DataManager.getInstance().unSubscribe(postCheckoutCallBack);
        super.onDestroy();
    }

    private void setShoppingPointsCheckedChangedListener(View view) {
        AppCompatCheckBox shoppingPointsCheckBox =
                (AppCompatCheckBox) view.findViewById(R.id.fragment_step1_shopping_points_cb);
        shoppingPointsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                isSpendShoppingPoint = checked;
                postCheckout();
            }
        });
    }

    public void onProductStyleButtonClick(ProductViewModel viewModel) {
        final int productPosition = viewModel.getProductPosition();
        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
        onSelectStyleButtonClick(productPosition, products.get(productPosition));
    }

    public void onProductQuantityButtonClick(ProductViewModel viewModel) {
        GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.NUMBER_CHANGE));
        this.tempCount = viewModel.getQuantity();
        View dialogView = getDialogView();
        getAlertDialog(viewModel.getProductPosition(), dialogView).show();
    }

    private void logInitiatedCheckoutEvent() {
        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
        for (Product product : products) {
            FacebookLogger.getInstance().logInitiatedCheckoutEvent(
                    String.valueOf(product.getId()),
                    product.getCategoryName(),
                    product.getName(),
                    product.getBuy_count(),
                    true,
                    product.getFinalPrice() * product.getBuy_count()
            );
        }
    }

    private void updateGoodsLinearLayout(List<ProductViewModel> productViewModels) {
        goodsContainerLinearLayout.removeAllViews();
        for (ProductViewModel productViewModel : productViewModels) {
            ItemShoppingCartProductBinding binding1 = DataBindingUtil.inflate(
                    LayoutInflater.from(getActivity()), R.layout.item_shopping_cart_product, null, false);
            binding1.setProductViewModel(productViewModel);
            binding1.setStep1Fragment(this);

            View root = binding1.getRoot();
            TextView originalTextView = (TextView) root.findViewById(R.id.item_shopping_cart_product_original_price_tv);
            TextViewUtil.paintLineThroughTextView(originalTextView);

            goodsContainerLinearLayout.addView(root);
        }
    }

    public void onDeliveryTextViewClick() {
        new DeliveryDialog(getActivity(), selectedDelivery,
                new Action1<String>() {
                    @Override
                    public void call(String selectedDelivery) {
                        Step1Fragment.this.selectedDelivery = selectedDelivery;
                        deliveryTextView.setText(selectedDelivery);
                    }
                }).show();
    }

    public void onConfirmButtonClick() {
        onNextStepButtonClick();
    }

    public void onGuestCheckoutClick() {
        onNextStepButtonClick();
    }

    public void onLoginButtonClick() {
        GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.LOGIN));
        startLoginActivity();
    }

    public void onDeleteImageViewClick(final ProductViewModel viewModel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MmwAlertDialog);
        alertDialogBuilder.setTitle("刪除商品")
                .setMessage("是否確定要刪除商品")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.PRODUCT_DELETE));

                int position = viewModel.getProductPosition();
                List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
                products.remove(position);
                ShoppingCartManager.getInstance().storeShoppingItems(products);
                if (products.size() == 0) {
                    mCallback.onNoShoppingItem();
                } else {
                    postCheckout();
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void onNextStepButtonClick() {
        if (selectedDelivery.equals("請選擇")) {
            Toast.makeText(getActivity(), "請選擇取貨方式", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderPrice.getOriginItemsPrice() < orderPrice.getShipFee()) {
            showPriceAlertDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startNextStep();
                }
            });
        } else {
            startNextStep();
        }
    }

    private void startNextStep() {
        if (Settings.checkIsLogIn()) {
            GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.CONFIRM_FACEBOOK_LOGIN));
            if (selectedDelivery.equals(getString(R.string.dialog_delivery_store))) {
                progressDialog = CenterProgressDialog.show(getActivity(), new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        DataManager.getInstance().unSubscribe(checkPickupRecordCallBack);
                    }
                });
                ShipInfoEntity shipInfoEntity = new ShipInfoEntity(
                        Settings.getSavedUser().getUserId(),
                        "",
                        "");
                DataManager.getInstance().checkPickupRecord(shipInfoEntity, checkPickupRecordCallBack);
            } else {
                mCallback.onStep1NextButtonClick(email, selectedDelivery, isSpendShoppingPoint);
            }
        } else {
            GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.ANONYMOUS_PURCHASE));
            mCallback.onStep1NextButtonClick(email, selectedDelivery, isSpendShoppingPoint);
        }
    }

    private void onSelectStyleButtonClick(final int productPosition, Product product) {
        List<Spec> specs = product.getSpecs();
        if (specs == null || specs.size() == 0) {
            Toast.makeText(getActivity(), "商品樣式讀取錯誤，如要更改樣式，請移除商品，重新加入購物車", Toast.LENGTH_SHORT).show();
            return;
        }
        ProductStyleDialog styleDialog = new ProductStyleDialog(getActivity(), product, new ProductStyleDialog.ProductStyleDialogListener() {
            @Override
            public void onFirstAddShoppingCart() {

            }

            @Override
            public void onConfirmButtonClick(Product product) {
                List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
                products.get(productPosition).setSelectedSpec(product.getSelectedSpec());
                ShoppingCartManager.getInstance().storeShoppingItems(products);
                postCheckout();
            }
        });
        styleDialog.showNoCountStyleDialog(product);
    }


    private void postCheckout() {
        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
        if (products == null || products.size() == 0) {
            return;
        }
        loadingView.setVisibility(View.VISIBLE);

        int userId = Settings.getSavedUser().getUserId();
        CheckoutPostEntity checkoutPostEntity = new CheckoutPostEntity(userId, isSpendShoppingPoint, products);
        DataManager.getInstance().postCheckout(checkoutPostEntity, postCheckoutCallBack);
    }

    @SuppressLint("InflateParams")
    private View getDialogView() {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_select_item_counts, null, false);
        final TextView countText = (TextView) view.findViewById(R.id.count_text_view);
        countText.setText(String.valueOf(tempCount));
        setMinusButton(view, countText);
        setPlusButton(view, countText);
        return view;
    }

    private void setMinusButton(View view, final TextView countText) {
        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1) {
                    tempCount = tempCount - 1;
                    countText.setText(String.valueOf(tempCount));
                }
            }
        });
    }

    private void setPlusButton(View view, final TextView countText) {
        Button plusButton = (Button) view.findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                countText.setText(String.valueOf(tempCount));
            }
        });
    }

    private AlertDialog getAlertDialog(final int product_position, View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MmwAlertDialog);

        alertDialogBuilder.setTitle("選擇商品數量")
                .setView(view)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
                        products.get(product_position).setBuy_count(tempCount);
                        ShoppingCartManager.getInstance().storeShoppingItems(products);

                        postCheckout();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return alertDialogBuilder.create();
    }

    private void showPriceAlertDialog(DialogInterface.OnClickListener onConfirmClickListener) {
        android.app.AlertDialog.Builder alertDialogBuilder =
                new android.app.AlertDialog.Builder(getActivity());

        alertDialogBuilder
                .setTitle("購買金額低於運費")
                .setMessage(String.format("提醒您，您所購買的金額低於運費%s元，是否確認購買", SHIP_FEE))
                .setCancelable(false)
                .setPositiveButton("確認", onConfirmClickListener)
                .setNegativeButton("再逛逛", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    public interface OnStep1ButtonClickListener {

        void onStep1NextButtonClick(String email, String delivery, boolean isSpendShoppingPoint);

        void onNoShoppingItem();
    }
}
