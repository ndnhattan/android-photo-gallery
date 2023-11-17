package com.nhom12.test.structures;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private String name;
    private String firstImagesData;

    public Album(String name) {
        this.name = name;
        this.firstImagesData = "";
    }

    public String getName() {
        return name;
    }

    public String getFirstImagesData() {
        return firstImagesData;
    }

    public void addFirstImagesData(String data){
        firstImagesData = data;
    }
}
