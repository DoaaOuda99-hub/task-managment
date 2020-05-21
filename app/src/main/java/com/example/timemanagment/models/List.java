package com.example.timemanagment.models;


public class List {

    private String id;
    private String name;
    private int colorHexa;
    private int iconName;
    private String userId ;



    public List(String id,String name, int colorHexa, int iconName, String userId) {
        this.id = id;
        this.name = name;
        this.colorHexa = colorHexa;
        this.iconName = iconName;
        this.userId = userId;
    }


    public List() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColorHexa() {
        return colorHexa;
    }

    public void setColorHexa(int colorHexa) {
        this.colorHexa = colorHexa;
    }

    public int getIconName() {
        return iconName;
    }

    public void setIconName(int iconName) {
        this.iconName = iconName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
