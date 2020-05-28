package com.hasry.models;

import java.io.Serializable;
import java.util.List;

public class CityDataModel implements Serializable {

    public City data;

    public City getData() {
        return data;
    }

    public static class City implements Serializable{
        private List<String> cities;

        public List<String> getCities() {
            return cities;
        }
    }
}
