package com.zivoy;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;

public class EditContacts extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList<Element> list1;
    private JTextField keyFeild;
    private JTextField nameFeild;
    private JButton addContactButton;
    private JButton removeContactButton;
    private JTextField SelectedFeild;
    private JPanel SelctedPanel;
    private JCheckBox addPrivateCheckBox;
    private JLabel KeyTypeName;

    private DefaultListModel<Element> model;
    private ApplicationWindow.ContactsEditsTransfer onclose;
    private int selected = -1;


    public EditContacts(ApplicationWindow.ContactsEditsTransfer onclose) {
        this.model = onclose.model;
        this.onclose = onclose;

        list1.setModel(model);

        setContentPane(contentPane);
        setModal(true);
        SelctedPanel.setVisible(false);
        setResizable(false);
        //getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!list1.isSelectionEmpty()) {
                    selected = list1.getSelectedIndex();
                } else {
                    list1.setSelectedIndex(0);
                    selected = 0;
                }
                if (model.size() == 0) selected = -1;
            }
        });

        removeContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.size() > 0) {
                    selected -= 1;
                    list1.setSelectedIndex(selected);
                } else {
                    selected = -1;
                }
                if (selected == -1) {
                    if (list1.getModel().getSize() == 0) {
                        JOptionPane.showMessageDialog(null, "Select a contact first",
                                "Error -- no contact selected", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        selected++;
                    }
                }
                model.removeElementAt(selected);
                reindex();
            }
        });

        Action action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameFeild.getText().equals("") || keyFeild.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Values must be filled in",
                            "Error -- invalid values", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String name = nameFeild.getText();
                boolean fail = false;
                Key key = null;
                if (!addPrivateCheckBox.isSelected()) {
                    try {
                        key = PublicKey.fromString(keyFeild.getText());
                    } catch (Exception E) {
                        fail = true;
                    }
                    if (fail || !((PublicKey) key).validate()) {
                        JOptionPane.showMessageDialog(null, "I don't think that key is correct",
                                "Error -- invalid public key", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    try {
                        key = PrivateKey.fromString(keyFeild.getText());
                    } catch (Exception E) {
                        JOptionPane.showMessageDialog(null, "I don't think that key is correct",
                                "Error -- invalid public key", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                Element entery = new Element(name, key, model.size() + 1);
                model.addElement(entery);
                keyFeild.setText("");
                nameFeild.setText("");
            }
        };

        nameFeild.addActionListener(action);
        keyFeild.addActionListener(action);
        addContactButton.addActionListener(action);

        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!list1.isSelectionEmpty()) {
                    SelctedPanel.setVisible(true);
                    SelectedFeild.setText(list1.getSelectedValue().getKeyValue().getKey());
                } else {
                    SelctedPanel.setVisible(false);
                }
            }
        });
        addPrivateCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addPrivateCheckBox.isSelected()) {
                    KeyTypeName.setText("Private Key:");
                } else {
                    KeyTypeName.setText("Public Key:");
                }
            }
        });
    }

    private void onOK() {
        reindex();
        this.onclose.close(this.model);
        dispose();
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void reindex() {
        for (int i = 0; i < this.model.size(); i++) {
            Element curr = this.model.get(i);
            curr.index = i + 1;
            this.model.set(i, curr);
        }
    }

}
