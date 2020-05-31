package com.hasry.models;

import java.io.Serializable;

public class OrderResponseModel implements Serializable {

    private OrderModel data;

    public OrderModel getData() {
        return data;
    }
}
