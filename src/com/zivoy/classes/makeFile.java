package com.zivoy.classes;

import java.io.PrintWriter;

public class makeFile {
    private String path;
    private String[] contacts;

    public makeFile(String path, String[] contacts) {
        this.path = path;
        this.contacts = contacts;
    }

    public void wright() {
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");

            for (String contact : contacts) writer.println(contact);
            writer.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}