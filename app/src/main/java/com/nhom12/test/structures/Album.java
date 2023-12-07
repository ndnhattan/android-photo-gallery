package com.nhom12.test.structures;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private long albumID;
    private String name;
    private String firstImagesData;

    public Album(long albumID,String name, String firstImagesData) {
        this.albumID = albumID;
        this.name = name;
        this.firstImagesData = firstImagesData;
    }

    public Long getAlbumID(){ return albumID; }

    public String getName() {
        return name;
    }

    public String getFirstImagesData() {
        return firstImagesData;
    }

}
