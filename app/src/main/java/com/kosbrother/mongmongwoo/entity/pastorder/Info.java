package com.kosbrother.mongmongwoo.entity.pastorder;

public class Info extends InfoEntity {

    @Override
    public String getShipStoreName() {
        return "門市名稱：" + super.getShipStoreName();
    }

    @Override
    public String getShipStoreAddress() {
        return "地        址：" + super.getShipStoreAddress();
    }

    @Override
    public String getShipStorePhone() {
        return "門市電話：" + super.getShipStorePhone();
    }

    @Override
    public String getShipName() {
        return "姓        名：" + super.getShipName();
    }

    @Override
    public String getShipPhone() {
        return "電        話：" + super.getShipPhone();
    }

    @Override
    public String getShipEmail() {
        return "聯絡信箱：" + super.getShipEmail();
    }
}
