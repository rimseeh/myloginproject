package com.example.designandloginproject.signinmethods;

public class MyFacebook {
    private static MyFacebook ourInstance = null;

    static MyFacebook getInstance() {
        if (ourInstance == null) {
            ourInstance = new MyFacebook();
        }
        return ourInstance;
    }

    private MyFacebook() {
    }
}
