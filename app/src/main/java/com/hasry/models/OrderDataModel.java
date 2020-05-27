package com.hasry.models;

import java.io.Serializable;
import java.util.List;

public class OrderDataModel implements Serializable {

    public Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private List<OrderModel> orders;

        public List<OrderModel> getOrders() {
            return orders;
        }
    }
}
