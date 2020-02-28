package com.zivoy;

import com.zivoy.windows.ApplicationWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new ApplicationWindow("Encryption app");
            frame.setVisible(true);
        });
    }
}
