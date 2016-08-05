package com.marco.circularmenu;

import java.io.Serializable;

public class CircularMenuItem implements Serializable {

    private int iconID;
    private String text;


    // Constructors

    public CircularMenuItem() {
    }

    public CircularMenuItem(int iconID, String text) {
        this.iconID = iconID;
        this.text = text;
    }


    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
