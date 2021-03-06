package com.hasryApp.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private User data;

    public User getData() {
        return data;
    }

    public static class User implements Serializable {
        private int id;
        private String name;
        private String email;
        private String city;
        private String district;
        private String user_type;
        private String phone_code;
        private String phone;
        private String logo;
        private String car_image;
        private String car_documentation_image;
        private String drive_documentation_image;
        private String block;
        private String token;
        private String latitude;
        private String longitude;
        private double points;
        private String notification_status;
        private String user_link;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getCity() {
            return city;
        }

        public String getDistrict() {
            return district;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getUser_link() {
            return user_link;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getLogo() {
            return logo;
        }

        public String getCar_image() {
            return car_image;
        }

        public String getCar_documentation_image() {
            return car_documentation_image;
        }

        public String getDrive_documentation_image() {
            return drive_documentation_image;
        }

        public String getBlock() {
            return block;
        }

        public String getToken() {
            return token;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public double getPoints() {
            return points;
        }

        public String getNotification_status() {
            return notification_status;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
