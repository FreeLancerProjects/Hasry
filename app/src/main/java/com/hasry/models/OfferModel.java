package com.hasry.models;

import java.io.Serializable;

public class OfferModel implements Serializable {

    private int id;
    private String title;
    private String image;
    private int departemnt_id;
    private int markter_id;
    private String price;
    private String content;
    private String details;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public int getDepartemnt_id() {
        return departemnt_id;
    }

    public int getMarkter_id() {
        return markter_id;
    }

    public String getPrice() {
        return price;
    }

    public String getContent() {
        return content;
    }

    public String getDetails() {
        return details;
    }
}
