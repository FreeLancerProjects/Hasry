package com.hasry.models;

import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable {
    private int id;
    private String order_code;
    private String markter_id;
    private String driver_id;
    private String user_id;
    private String latitude;
    private String longitude;
    private String address;
    private String order_date;
    private String order_time;
    private String total_price;
    private String order_status;
    private UserModel.User markter;
    private UserModel.User client;
    private UserModel.User driver;
    private List<OrderDetails> order_details;


    public int getId() {
        return id;
    }

    public String getOrder_code() {
        return order_code;
    }

    public String getMarkter_id() {
        return markter_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getOrder_date() {
        return order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public String getTotal_price() {
        return total_price;
    }

    public String getOrder_status() {
        return order_status;
    }

    public UserModel.User getMarkter() {
        return markter;
    }

    public UserModel.User getClient() {
        return client;
    }

    public UserModel.User getDriver() {
        return driver;
    }

    public List<OrderDetails> getOrder_details() {
        return order_details;
    }

    public static class OrderDetails implements Serializable{
        private int id;
        private int product_id;
        private int offer_id;
        private String price;
        private int amount;
        private int order_id;
        private Offer offer;


        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getOffer_id() {
            return offer_id;
        }

        public String getPrice() {
            return price;
        }

        public int getAmount() {
            return amount;
        }

        public int getOrder_id() {
            return order_id;
        }

        public Offer getOffer() {
            return offer;
        }

        public static class Offer implements Serializable{
            private int id;
            private int product_id;
            private int markter_id;
            private String from_date;
            private String to_date;
            private String offer_type;
            private String offer_value;
            private String offer_status;
            private OfferModel product;

            public int getId() {
                return id;
            }

            public int getProduct_id() {
                return product_id;
            }

            public int getMarkter_id() {
                return markter_id;
            }

            public String getFrom_date() {
                return from_date;
            }

            public String getTo_date() {
                return to_date;
            }

            public String getOffer_type() {
                return offer_type;
            }

            public String getOffer_value() {
                return offer_value;
            }

            public String getOffer_status() {
                return offer_status;
            }

            public OfferModel getProduct() {
                return product;
            }
        }
    }

}
