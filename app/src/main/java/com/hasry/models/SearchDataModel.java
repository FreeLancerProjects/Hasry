package com.hasry.models;

import java.io.Serializable;
import java.util.List;

public class SearchDataModel implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }


    public static class  Data implements Serializable{
        private List<OfferModel> products;

        public List<OfferModel> getProducts() {
            return products;
        }
    }
}
