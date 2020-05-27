package com.hasry.models;

import java.io.Serializable;

public class SettingModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private String term_conditions;
        private String whatsapp;
        private String instagram;
        private String facebook;
        private String twitter;
        private String offer_muted;

        public String getTerm_conditions() {
            return term_conditions;
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
    }
}
