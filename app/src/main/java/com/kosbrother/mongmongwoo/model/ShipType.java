package com.kosbrother.mongmongwoo.model;

public enum ShipType {
    store("超商-取貨付款"), home("宅配-貨到付款"), homeByCreditCard("宅配-信用卡付款");

    private String shipTypeText;

    ShipType(String shipTypeText) {
        this.shipTypeText = shipTypeText;
    }

    public String getShipType() {
        String shipType;
        switch (shipTypeText) {
            case "超商-取貨付款":
                shipType = "store_delivery";
                break;
            case "宅配-貨到付款":
                shipType = "home_delivery";
                break;
            case "宅配-信用卡付款":
                shipType = "home_delivery_by_credit_card";
                break;
            default:
                shipType = "";
                break;
        }
        return shipType;
    }

    public String getShipTypeText() {
        return shipTypeText;
    }

    public static String getShipTypeFromShipTypeText(String shipTypeText) {
        if (shipTypeText != null) {
            for (ShipType shipType : ShipType.values()) {
                if (shipTypeText.equalsIgnoreCase(shipType.getShipTypeText())) {
                    return shipType.getShipType();
                }
            }
        }
        throw new IllegalArgumentException("No constant with shipTypeText " + shipTypeText + " found");
    }

    public static String getShipTypeTextFromShipType(String shipType) {
        if (shipType != null) {
            for (ShipType type : ShipType.values()) {
                if (shipType.equalsIgnoreCase(type.getShipType())) {
                    return type.getShipTypeText();
                }
            }
        }
        throw new IllegalArgumentException("No constant with shipType " + shipType + " found");
    }
}
