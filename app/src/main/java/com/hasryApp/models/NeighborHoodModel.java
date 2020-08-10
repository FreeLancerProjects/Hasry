package com.hasryApp.models;

import java.io.Serializable;
import java.util.List;

public class NeighborHoodModel implements Serializable {

    public Neighborhood data;

    public Neighborhood getData() {
        return data;
    }

    public static class Neighborhood implements Serializable{
        private List<String> neighborhoods;

        public List<String> getNeighborhoods() {
            return neighborhoods;
        }
    }
}
