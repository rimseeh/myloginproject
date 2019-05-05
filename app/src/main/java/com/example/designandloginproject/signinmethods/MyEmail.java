package com.example.designandloginproject.signinmethods;

public class MyEmail {
    private static MyEmail ourInstance = null;

    static MyEmail getInstance() {
        if (ourInstance == null) {
            ourInstance = new MyEmail();
        }
        return ourInstance;
    }

    private MyEmail() {
    }
}
