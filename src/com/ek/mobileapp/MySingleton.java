package com.ek.mobileapp;

public class MySingleton {

    private static MySingleton instance;
    public String customVar="tag";

    public static void initInstance() {
        if (instance == null) {
            // Create the instance
            instance = new MySingleton();
        }
    }

    public static MySingleton getInstance() {
        // Return the instance
        return instance;
    }

    private MySingleton() {
        // Constructor hidden because this is a singleton
    }

    public void customSingletonMethod() {
        // Custom method
    }
    //example
    //app = (MyApplication)getApplication();
    //// Call a custom application method
    //app.customAppMethod();
    //// Call a custom method in MySingleton
    //MySingleton.getInstance().customSingletonMethod();
    //// Read the value of a variable in MySingleton
    //String singletonVar = MySingleton.getInstance().customVar;
}
