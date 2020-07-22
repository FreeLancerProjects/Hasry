package com.hasry.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel implements Serializable {

    private List<NotificationModel> data;

    public List<NotificationModel> getData() {
        return data;
    }


    public static class NotificationModel implements Serializable {
        private int id;
        private String title;
        private String message;
        private int order_id;
        private int from_user;
        private int to_user;
        private String from_user_type;
        private String to_user_type;
        private String type;
        private int is_read;
        private String action_type;
        private String image;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public int getOrder_id() {
            return order_id;
        }

        public int getFrom_user() {
            return from_user;
        }

        public int getTo_user() {
            return to_user;
        }

        public String getFrom_user_type() {
            return from_user_type;
        }

        public String getTo_user_type() {
            return to_user_type;
        }

        public String getType() {
            return type;
        }

        public int getIs_read() {
            return is_read;
        }

        public String getAction_type() {
            return action_type;
        }

        public String getImage() {
            return image;
        }
    }
}
