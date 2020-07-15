package com.hasry.models;

import java.io.Serializable;

public class SettingModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable {
        private String term_conditions;
        private String about_app;
        private String whatsapp;
        private String instagram;
        private String facebook;
        private String twitter;
        private String offer_muted;
        private String email1;

        public String getTerm_conditions() {
            return term_conditions;
        }

        public String getAbout_app() {
            return about_app;
        }


        public String getWhatsapp() {
            return whatsapp;
        }

        public String getInstagram() {
            return instagram;
        }

        public String getFacebook() {
            return facebook;
        }

        public String getTwitter() {
            return twitter;
        }

        public String getOffer_muted() {
            return offer_muted;
        }

        public String getEmail1() {
            return email1;
        }
    }
}
