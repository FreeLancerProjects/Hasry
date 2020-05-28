package com.hasry.models;

import java.io.Serializable;

public class OfferModel implements Serializable {

    private int id;
    private String title;
    private String image;
    private int departemnt_id;
    private int markter_id;
    private String price;
    private String contents;
    private String details;
    private Offer offer;



    public static class Offer implements Serializable{
        private int id;
        private String offer_type;
        private double offer_value;
        private String offer_status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOffer_type() {
            return offer_type;
        }

        public void setOffer_type(String offer_type) {
            this.offer_type = offer_type;
        }

        public double getOffer_value() {
            return offer_value;
        }

        public void setOffer_value(double offer_value) {
            this.offer_value = offer_value;
        }

        public String getOffer_status() {
            return offer_status;
        }

        public void setOffer_status(String offer_status) {
            this.offer_status = offer_status;
        }
    }
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
        return contents;
    }

    public String getDetails() {
        return details;
    }

    public Offer getOffer() {
        return offer;
    }
}
