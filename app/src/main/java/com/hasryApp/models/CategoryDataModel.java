package com.hasryApp.models;

import java.io.Serializable;
import java.util.List;

public class CategoryDataModel implements Serializable {

    private List<CategoryModel> data;

    public List<CategoryModel> getData() {
        return data;
    }

    public static class CategoryModel implements Serializable
    {
        private int id;
        private String title;


        public CategoryModel() {
        }

        public CategoryModel(int id, String title ){
            this.id = id;
         this.title=title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }
}
