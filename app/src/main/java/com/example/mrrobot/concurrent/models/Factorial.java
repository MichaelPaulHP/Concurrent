package com.example.mrrobot.concurrent.models;

public class Factorial {
    public static int factorial(int arg) {
        if (arg == 0) {
            return 1;
        }
        else {
            return arg * factorial(arg - 1);
        }
    }
}
