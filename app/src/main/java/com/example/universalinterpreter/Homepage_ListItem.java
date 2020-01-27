package com.example.universalinterpreter;

public class Homepage_ListItem {
    private String name, email;
    int type; //0-normal priority, 1-high priority
    int key;

    public Homepage_ListItem(String name, String email) {
        this.email = email;
        this.name = name;

    }

    public String getEmail() {
        return email;
    }


    public String getName() {
        return name;
    }
}
