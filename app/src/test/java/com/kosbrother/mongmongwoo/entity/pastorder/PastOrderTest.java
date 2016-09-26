package com.kosbrother.mongmongwoo.entity.pastorder;

import com.kosbrother.mongmongwoo.model.ShipType;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PastOrderTest {

    @Test
    public void isCancelable_homeByCreditCard() throws Exception {
        PastOrder pastOrder = new PastOrder() {
            @Override
            public String getShipType() {
                return ShipType.homeByCreditCard.getShipType();
            }

            @Override
            public String getStatus() {
                return "訂單成立";
            }
        };

        boolean isCancelable = pastOrder.isCancelable();

        assertFalse(isCancelable);
    }

    @Test
    public void isCancelable_status_new_order() throws Exception {
        PastOrder pastOrder = new PastOrder() {
            @Override
            public String getShipType() {
                return ShipType.home.getShipType();
            }

            @Override
            public String getStatus() {
                return "訂單成立";
            }
        };

        boolean isCancelable = pastOrder.isCancelable();

        assertTrue(isCancelable);
    }

    @Test
    public void isCancelable_status_dealing() throws Exception {
        PastOrder pastOrder = new PastOrder() {
            @Override
            public String getShipType() {
                return ShipType.home.getShipType();
            }

            @Override
            public String getStatus() {
                return "處理中";
            }
        };

        boolean isCancelable = pastOrder.isCancelable();

        assertFalse(isCancelable);
    }

    @Test
    public void isCancelable_status_delivering() throws Exception {
        PastOrder pastOrder = new PastOrder() {
            @Override
            public String getShipType() {
                return ShipType.home.getShipType();
            }

            @Override
            public String getStatus() {
                return "配送中";
            }
        };

        boolean isCancelable = pastOrder.isCancelable();

        assertFalse(isCancelable);
    }

}