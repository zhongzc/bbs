package com.gaufoo;

public class Main {
    public static enum Temp {
        我,
        你,
        他
    }

    public static void main(String[] args) {
        System.out.println( Temp.他.ordinal());
    }
}

