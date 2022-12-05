package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class ShoppingListModel implements Serializable {

    public String imagePath;
    public String caption;

    public ShoppingListModel(){

    }

    public ShoppingListModel(String imagePath, String caption) {
        this.imagePath = imagePath;
        this.caption = caption;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
