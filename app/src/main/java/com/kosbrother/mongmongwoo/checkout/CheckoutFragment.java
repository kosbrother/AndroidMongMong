package com.kosbrother.mongmongwoo.checkout;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.common.DeliveryUserInfoViewModel;
import com.kosbrother.mongmongwoo.common.OrderPriceViewModel;
import com.kosbrother.mongmongwoo.common.ProductViewModel;
import com.kosbrother.mongmongwoo.databinding.FragmentCheckoutBinding;
import com.kosbrother.mongmongwoo.databinding.ItemOrderDetailProductBinding;
import com.kosbrother.mongmongwoo.entity.checkout.Campaign;
import com.kosbrother.mongmongwoo.entity.checkout.CheckoutPostEntity;
import com.kosbrother.mongmongwoo.entity.checkout.CheckoutResultEntity;
import com.kosbrother.mongmongwoo.entity.checkout.OrderPrice;
import com.kosbrother.mongmongwoo.entity.checkout.ShoppingPointCampaign;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3EnterEvent;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.ShipType;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;

import java.util.ArrayList;
import java.util.List;

public class CheckoutFragment extends Fragment {

    public static final String ARG_BOOLEAN_SPEND_SHOPPING_POINT = "ARG_BOOLEAN_SPEND_SHOPPING_POINT";
    public static final String ARG_SERIALIZABLE_STORE = "ARG_SERIALIZABLE_STORE";
    public static final String ARG_STRING_SHIP_ADDRESS = "ARG_STRING_SHIP_ADDRESS";
    public static final String ARG_STRING_DELIVERY = "ARG_STRING_DELIVERY";

    private OnStep3ButtonClickListener mCallback;

    private String delivery;
    private OrderPrice orderPrice;

    private FragmentCheckoutBinding binding;
    private Button sendButton;
    private View loadingView;

    private DataManager.ApiCallBack postCheckoutCallBack = new DataManager.ApiCallBack() {
        @Override
        public void onError(String errorMessage) {
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            loadingView.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(Object data) {
            CheckoutResultEntity result = (CheckoutResultEntity) data;
            ShoppingCartManager.getInstance().storeShoppingItems(result.getProducts());
            orderPrice = result.getOrderPrice();

            binding.setOrderPrice(new OrderPriceViewModel(orderPrice));

            View view = binding.getRoot();

            RecyclerView activityCampaignRecyclerView =
                    (RecyclerView) view.findViewById(R.id.order_price_activity_campaign_rv);
            activityCampaignRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            List<ActivityCampaignViewModel> activityCampaignViewModels = new ArrayList<>();
            for (Campaign campaign : result.getOrderPrice().getCampaigns()) {
                activityCampaignViewModels.add(new ActivityCampaignViewModel(campaign, false));
            }
            ActivityCampaignAdapter adapter = new ActivityCampaignAdapter(activityCampaignViewModels);
            activityCampaignRecyclerView.setAdapter(adapter);

            RecyclerView shoppingPointCampaignRecyclerView =
                    (RecyclerView) view.findViewById(R.id.order_price_shopping_point_campaign_rv);
            shoppingPointCampaignRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            List<ShoppingPointCampaignViewModel> shoppingPointCampaignViewModels = new ArrayList<>();
            for (ShoppingPointCampaign campaign : result.getOrderPrice().getShoppingPointCampaigns()) {
                shoppingPointCampaignViewModels.add(new ShoppingPointCampaignViewModel(campaign, false));
            }
            ShoppingPointCampaignAdapter shoppingPointCampaignAdapter =
                    new ShoppingPointCampaignAdapter(shoppingPointCampaignViewModels);
            shoppingPointCampaignRecyclerView.setAdapter(shoppingPointCampaignAdapter);

            LinearLayout productsContainer = (LinearLayout) binding.getRoot()
                    .findViewById(R.id.fragment_purchase3_products_container_ll);
            updateProductsContainer(productsContainer, result.getProducts());

            loadingView.setVisibility(View.GONE);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStep3ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnStep3ButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        delivery = getArguments().getString(ARG_STRING_DELIVERY);
        Store store = (Store) getArguments().getSerializable(ARG_SERIALIZABLE_STORE);
        String shipAddress = getArguments().getString(ARG_STRING_SHIP_ADDRESS);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false);
        binding.setCheckoutFragment(this);
        binding.setDeliveryUserInfo(new DeliveryUserInfoViewModel(store, shipAddress, delivery));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingView = view.findViewById(R.id.loading_fragment_fl);
        sendButton = (Button) view.findViewById(R.id.fragment_purchase3_send_btn);
        initSendOrderButtonText(delivery);

        postCheckout(getArguments().getBoolean(ARG_BOOLEAN_SPEND_SHOPPING_POINT));
    }

    @Override
    public void onResume() {
        super.onResume();
        GAManager.sendEvent(new CheckoutStep3EnterEvent());
    }

    @Override
    public void onDestroy() {
        DataManager.getInstance().unSubscribe(postCheckoutCallBack);
        super.onDestroy();
    }

    public void setSendOrderButtonEnabled(boolean enabled) {
        sendButton.setEnabled(enabled);
    }

    public void onSendOrderButtonClick() {
        mCallback.onSendOrderClick(orderPrice);
    }

    private void initSendOrderButtonText(String delivery) {
        boolean isHomeDeliveryByCreditCard =
                ShipType.homeByCreditCard.getShipTypeText().equalsIgnoreCase(delivery);
        if (isHomeDeliveryByCreditCard) {
            sendButton.setText("信用卡付款");
        } else {
            sendButton.setText("送出訂單");
        }
    }

    private void postCheckout(boolean isSpendShoppingPoint) {
        loadingView.setVisibility(View.VISIBLE);

        int userId = Settings.getSavedUser().getUserId();
        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
        CheckoutPostEntity checkoutPostEntity = new CheckoutPostEntity(userId, isSpendShoppingPoint, products);
        DataManager.getInstance().postCheckout(checkoutPostEntity, postCheckoutCallBack);
    }

    private void updateProductsContainer(LinearLayout container, List<Product> products) {
        container.removeAllViews();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            ProductViewModel viewModel = new ProductViewModel(product, i);

            ItemOrderDetailProductBinding itemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(getActivity()), R.layout.item_order_detail_product, null, false);
            itemBinding.setProductViewModel(viewModel);

            container.addView(itemBinding.getRoot());
        }
    }

    public interface OnStep3ButtonClickListener {

        void onSendOrderClick(OrderPrice orderPrice);
    }

}
