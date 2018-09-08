package com.company;

public class Main {

    public static void main(String[] args) {

        if (args.length != 1 || !args[0].matches("[a-zA-Z]+")){
            System.err.println("Argument error!");
            System.exit(1);
        }
    }
}
