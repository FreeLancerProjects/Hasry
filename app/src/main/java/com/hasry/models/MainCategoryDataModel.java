package com.hasry.models;

import java.io.Serializable;
import java.util.List;

public class MainCategoryDataModel implements Serializable {

    private Data data;

    public Data  getData() {
        return data;
    }

    public static class Data implements Serializable {
        private List<MainDepartments> main_departments;

        public List<MainDepartments> getMain_departments() {
            return main_departments;
        }

        public static class MainDepartments implements Serializable
        {
            private int id;
            private String title;
            private String background;
            private String icon;
            private String parent;


            public int getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getBackground() {
                return background;
            }

            public String getIcon() {
                return icon;
            }

            public String getParent() {
                return parent;
            }

        }

    }

}
