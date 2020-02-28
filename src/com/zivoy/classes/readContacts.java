package com.zivoy.classes;

import java.util.ArrayList;
import java.util.Scanner;

public class readContacts {
    private String path;

    public readContacts(String path) {
        this.path = path;
    }

    public ArrayList<String> getList() throws java.io.FileNotFoundException {
        java.io.File file = new java.io.File(path);
        String absolutePath = file.getAbsolutePath();
        Scanner s = new Scanner(new java.io.File(absolutePath));
        ArrayList<String> list = new ArrayList<>();
        while (s.hasNextLine()) {
            list.add(s.nextLine());
        }
        s.close();
        return list;
    }
}