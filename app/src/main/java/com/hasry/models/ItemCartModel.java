package com.hasry.models;

import java.io.Serializable;

public class ItemCartModel implements Serializable {
    private int product_id;
    private int offer_id;
    private String image;
    private String name;
    private double price_before_discount;
    private double price;
    private int amount;
    private String offer_type;
    private int offer_value;


    public ItemCartModel() {

    }

    public ItemCartModel(int product_id, int offer_id, String image, String name, int amount) {
        this.product_id = product_id;
        this.offer_id = offer_id;
        this.image = image;
        this.name = name;
        this.amount = amount;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public void setOffer_type(String offer_type) {
        this.offer_type = offer_type;
    }

    public int getOffer_value() {
        return offer_value;
    }

    public void setOffer_value(int offer_value) {
        this.offer_value = offer_value;
    }

    public double getPrice_before_discount() {
        return price_before_discount;
    }

    public void setPrice_before_discount(double price_before_discount) {
        this.price_before_discount = price_before_discount;
    }
}
