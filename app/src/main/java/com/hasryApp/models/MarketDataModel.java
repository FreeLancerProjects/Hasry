package com.hasryApp.models;

import java.io.Serializable;
import java.util.List;

public class MarketDataModel implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private List<Market> markets;

        public List<Market> getMarkets() {
            return markets;
        }

        public static class Market implements Serializable{
            private int id;
            private String name;
            private String email;
            private String logo;
            private String is_login;
            private String city;
            private String latitude;
            private String longitude;
            private String address;
            private String markter_status;



            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public String getEmail() {
                return email;
            }

            public String getLogo() {
                return logo;
            }

            public String getIs_login() {
                return is_login;
            }

            public String getCity() {
                return city;
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

            public String getMarkter_status() {
                return markter_status;
            }
        }

    }

}
