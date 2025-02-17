package com.hetic;

import java.net.URL;

public class FileReader {
    public static Object load(String arg) {
        URL resourceUrl = App.class.getClassLoader().getResource(arg);

        if (resourceUrl != null) {
            return resourceUrl.getPath();
        } else {
            System.out.println("File not found in resources");
            return new Exception();
        }
    }
}
