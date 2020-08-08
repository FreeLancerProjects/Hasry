package com.hasryApp.models;

import java.io.Serializable;
import java.util.List;

public class OrderDataModel implements Serializable {

    public Data data;
    private OrderModel order_details;

    public Data getData() {
        return data;
    }

    public OrderModel getOrder_details() {
        return order_details;
    }

    public static class Data implements Serializable {
        private List<OrderModel> orders;

        public List<OrderModel> getOrders() {
            return orders;
        }
    }
}
