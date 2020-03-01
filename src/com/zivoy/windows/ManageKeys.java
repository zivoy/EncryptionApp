package com.zivoy.windows;

import com.zivoy.keyHandlers.Key;
import com.zivoy.keyHandlers.PrivateKey;
import com.zivoy.keyHandlers.PublicKey;

import javax.swing.*;
import java.awt.event.*;

public class ManageKeys extends JDialog {
    PrivateKey currPrivate;
    PublicKey currPublic;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField PublicKeyFeild;
    private JTextField PrivateKeyFeild;
    private JButton MakeRandomKeys;
    private JCheckBox editKeysCheckBox;
    private ApplicationWindow.ManageKeysTransfer onclose;

    public ManageKeys(ApplicationWindow.ManageKeysTransfer closeOperation) {
        setContentPane(contentPane);
        this.onclose = closeOperation;
        currPrivate = closeOperation.privateKey;
        currPublic = closeOperation.publicKey;
        PrivateKeyFeild.setText(currPrivate.getKey());
        PublicKeyFeild.setText(currPublic.getKey());
        setModal(true);

        setResizable(false);
        getRootPane().setDefaultButton(buttonCancel);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        editKeysCheckBox.addActionListener(e -> {
            boolean on = editKeysCheckBox.isSelected();
            PublicKeyFeild.setEditable(on);
            PrivateKeyFeild.setEditable(on);
        });
        MakeRandomKeys.addActionListener(e -> {
            PrivateKey newPrivate = new PrivateKey();
            PublicKey newPublic = newPrivate.makePublic();
            currPrivate = newPrivate;
            currPublic = newPublic;
            PublicKeyFeild.setText(newPublic.getKey());
            PrivateKeyFeild.setText(newPrivate.getKey());
        });
    }

    private void onOK() {
        int dialogResult = JOptionPane.showConfirmDialog (null,
                "Are you sure you would like to change your keys?\n" +
                        "You would have to send your new public key to your contacts!",
                "Warning", JOptionPane.OK_CANCEL_OPTION);
        if(dialogResult == JOptionPane.CANCEL_OPTION){
            return;
        }
        currPrivate = PrivateKey.fromString(PrivateKeyFeild.getText());
        currPublic = PublicKey.fromString(PublicKeyFeild.getText());
        if (!Key.validateKeyPair(currPrivate, currPublic)) {
            JOptionPane.showMessageDialog(null, "This is not is not a valid key pair",
                    "Error -- invalid key pair", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.onclose.close(currPrivate, currPublic);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
