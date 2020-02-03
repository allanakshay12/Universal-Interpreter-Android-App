package com.example.universalinterpreter;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.firebase.database.FirebaseDatabase;

public class MultiDex_Support extends Application {
    public MultiDex_Support() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);


    }
}
