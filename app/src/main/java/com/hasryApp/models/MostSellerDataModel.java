package com.hasryApp.models;

import java.io.Serializable;
import java.util.List;

public class MostSellerDataModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private Products products;

        public Products getProducts() {
            return products;
        }

        public static class Products implements Serializable{
            private int current_page;
            private List<OfferModel> data;

            public int getCurrent_page() {
                return current_page;
            }

            public List<OfferModel> getData() {
                return data;
            }
        }
    }
}
