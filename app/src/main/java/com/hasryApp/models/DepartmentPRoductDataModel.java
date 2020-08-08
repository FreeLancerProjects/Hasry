package com.hasryApp.models;

import java.io.Serializable;
import java.util.List;

public class DepartmentPRoductDataModel implements Serializable {

        private List<OfferModel> data;

    public List<OfferModel> getData() {
        return data;
    }
}
