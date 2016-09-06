package com.kosbrother.mongmongwoo.model;

public enum ShipType {
    store("超商取貨付款"), home("宅配貨到付款");

    private String shipTypeText;

    ShipType(String shipTypeText) {
        this.shipTypeText = shipTypeText;
    }

    public String getShipType() {
        String shipType;
        switch (shipTypeText){
            case "超商取貨付款":
                shipType = "store_delivery";
                break;
            case "宅配貨到付款":
                shipType = "home_delivery";
                break;
            default:
                shipType = "";
                break;
        }
        return shipType;
    }

    public static ShipType fromString(String text) {
        if (text != null) {
            for (ShipType shipType : ShipType.values()) {
                if (text.equalsIgnoreCase(shipType.shipTypeText)) {
                    return shipType;
                }
            }
        }

        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
