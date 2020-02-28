package com.zivoy;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ApplicationWindow extends JFrame {
    private JPanel mainPanel;
    private JList<Element> ContactsList;
    private JTextArea OutputTextArea;
    private JTextArea InputTextFeild;
    private JButton encryptDecryptButton;
    private JButton manageContactsButton;
    private JButton manageKeysButton;
    private JTextField PublicKeyFeild;
    private JScrollPane ConstactsScollFeild;
    private JScrollPane InputScrollArea;
    private JScrollPane OutputScrollArea;
    private JLabel PublicKeyLabel;
    private ContactsListClass contactList;

    private Key selected;
    private PrivateKey myPrivate;
    private PublicKey myPublic;
    private FileManeger contactSaver;

    public ApplicationWindow(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setSize(600, 500);
        this.setResizable(false);
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                System.out.println(info.getName());
                // if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(new WindowsLookAndFeel());//DarculaLaf());
                //break;
                //}
            }
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("error");
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(this);
        setup();
        this.pack();

        encryptDecryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = InputTextFeild.getText();
                if (text.equals("")) ;
                else if (selected instanceof PrivateKey) {
                    text = ((PrivateKey) selected).decode(text);
                } else if (selected instanceof PublicKey) {
                    text = ((PublicKey) selected).encode(text);
                }
                OutputTextArea.setText(text);
            }
        });
        ContactsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (contactList.isSelected()) {
                    selected = contactList.getSelectedKey();
                    updateButton();
                } else {
                    ContactsList.setSelectedIndex(0);
                }
            }
        });
        manageContactsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
                JDialog contacts = new EditContacts(new ContactsEditsTransfer(contactList.getModel()));
                contacts.pack();
                contacts.setVisible(true);
            }
        });
        manageKeysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
                JDialog keyManeger = new ManageKeys(new ManageKeysTransfer(myPrivate, myPublic));
                keyManeger.pack();
                keyManeger.setVisible(true);
            }
        });
    }

    private void updateButton(Key selected) {
        if (selected instanceof PrivateKey) {
            encryptDecryptButton.setText("Decrypt Text");
        } else if (selected instanceof PublicKey) {
            encryptDecryptButton.setText("Encrypt Text");
        }
    }

    private void updateButton() {
        updateButton(contactList.getSelectedKey());
    }

    private void load() {
        FileManeger.loadout items = contactSaver.load();
        myPrivate = items.privateKey;
        myPublic = items.publicKey;
        contactList.setModel(items.model);
    }


    private void setup() {
        contactSaver = new FileManeger("./contacts.txt");  // todo maybe encrypt this file
        FileManeger.loadout items = contactSaver.load();
        myPrivate = items.privateKey;
        myPublic = items.publicKey;
        this.selected = myPrivate;
        updateButton(myPrivate);
        contactList = new ContactsListClass(ContactsList, myPrivate);
        PublicKeyFeild.setText(myPublic.getKey());
        contactList.setModel(items.model);
        contactSaver.save(myPrivate, myPublic, contactList.getModel());
        if (!contactList.isSelected()) ContactsList.setSelectedIndex(0);
    }

    class ContactsEditsTransfer {
        public DefaultListModel<Element> model;

        public ContactsEditsTransfer(DefaultListModel<Element> model) {
            this.model = model;
        }

        public void close(DefaultListModel<Element> model) {
            contactList.setModel(model);
            contactSaver.save(myPrivate, myPublic, contactList.getModel());
        }
    }

    class ManageKeysTransfer {
        public PublicKey publicKey;
        public PrivateKey privateKey;

        public ManageKeysTransfer(PrivateKey privateKey, PublicKey publicKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public void close(PrivateKey privateKey, PublicKey publicKey) {
            myPublic = publicKey;
            myPrivate = privateKey;
            PublicKeyFeild.setText(publicKey.getKey());
            contactList.updatePrivateKey(privateKey);
            if (!contactList.isSelected()) ContactsList.setSelectedIndex(0);
            selected = contactList.getSelectedKey();
            contactSaver.save(myPrivate, myPublic, contactList.getModel());
        }
    }
}