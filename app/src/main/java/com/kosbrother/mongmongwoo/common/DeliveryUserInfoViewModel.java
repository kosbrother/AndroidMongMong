package com.kosbrother.mongmongwoo.common;

import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.entity.pastorder.InfoEntity;
import com.kosbrother.mongmongwoo.model.ShipType;
import com.kosbrother.mongmongwoo.model.Store;

public class DeliveryUserInfoViewModel {

    private String userName;
    private String userPhone;
    private String userEmail;

    private boolean isStoreDelivery;
    private String storeName;
    private String storeAddress;
    private String storePhone;

    private boolean isHomeDelivery;
    private String shipAddress;
    private String shipTypeText;

    private boolean isHomeDeliveryByCreditCard;

    public DeliveryUserInfoViewModel(Store savedStore, String shipAddress, String shipTypeText) {
        userName = Settings.getShipName();
        userPhone = Settings.getShipPhone();
        userEmail = Settings.getShipEmail();

        isStoreDelivery = ShipType.store.getShipTypeText().equals(shipTypeText);
        isHomeDelivery = ShipType.home.getShipTypeText().equals(shipTypeText);
        isHomeDeliveryByCreditCard = ShipType.homeByCreditCard.getShipTypeText().equals(shipTypeText);
        this.shipTypeText = shipTypeText;

        storeName = savedStore.getName();
        storeAddress = savedStore.getAddress();
        storePhone = savedStore.getPhone();

        this.shipAddress = shipAddress;
    }

    public DeliveryUserInfoViewModel(InfoEntity infoEntity, String shipType, String shipName, String shipPhone, String shipEmail) {
        userName = shipName;
        userPhone = shipPhone;
        userEmail = shipEmail;

        storeName = infoEntity.getShipStoreName();
        storeAddress = infoEntity.getShipStoreAddress();
        storePhone = infoEntity.getShipStorePhone();
        shipAddress = infoEntity.getShipAddress();

        isStoreDelivery = ShipType.store.getShipType().equals(shipType);
        isHomeDelivery = ShipType.home.getShipType().equals(shipType);
        isHomeDeliveryByCreditCard = ShipType.homeByCreditCard.getShipType().equals(shipType);
        shipTypeText = ShipType.getShipTypeTextFromShipType(shipType);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean isStoreDelivery() {
        return isStoreDelivery;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public boolean isHomeDelivery() {
        return isHomeDelivery;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public String getShipTypeText() {
        return shipTypeText;
    }

    public boolean isHomeDeliveryByCreditCard() {
        return isHomeDeliveryByCreditCard;
    }
}
