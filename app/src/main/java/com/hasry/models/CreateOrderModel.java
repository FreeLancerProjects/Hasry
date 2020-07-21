package com.hasry.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderModel implements Serializable {
    private int markter_id;
    private String driver_id;
    private int user_id;
    private String latitude;
    private String longitude;
    private String address;
    private String order_time;
    private String order_date;
    private String details;
    private List<ItemCartModel> products;
    private String order_type;

    public CreateOrderModel() {
        products = new ArrayList<>();
    }

    public int getMarkter_id() {
        return markter_id;
    }

    public void setMarkter_id(int markter_id) {
        this.markter_id = markter_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<ItemCartModel> getProducts() {
        return products;
    }

    public void setProducts(List<ItemCartModel> products) {
        this.products = products;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public void addNewProduct(ItemCartModel itemCartModel) {
        int pos = isItemExist(itemCartModel);
        if (pos == -1) {
            products.add(itemCartModel);
        } else {
            products.set(pos, itemCartModel);
        }

    }


    public void removeProduct(int pos) {
        products.remove(pos);
    }

    private int isItemExist(ItemCartModel itemCartModel) {
        int pos = -1;
        for (int i = 0; i < products.size(); i++) {

            if (itemCartModel.getProduct_id() == products.get(i).getProduct_id()) {
                pos = i;
                break;
            }
        }
        return pos;
    }


}
