package com.example.universalinterpreter;

public class Homepage_ListItem {
    private String name, email;
    int type; //0-read, 1-new
    int key;

    public Homepage_ListItem(String name, String email, int type) {
        this.email = email;
        this.name = name;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }


    public String getName() {
        return name;
    }

    public int getType() { return type; }
}
