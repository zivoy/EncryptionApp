package com.zivoy;

import javax.swing.*;

public class ContactsListClass {

    private JList<Element> list;
    private DefaultListModel<Element> model;
    private Element myPrivate;

    ContactsListClass(JList<Element> list, PrivateKey mykey) {
        this.list = list;
        this.model = (DefaultListModel<Element>) list.getModel();
        this.myPrivate = new Element("Me - Decrypt", mykey, this.model.size());
        this.appendElement(myPrivate);
        list.setSelectedIndex(0);
    }

    void updatePrivateKey(PrivateKey key) {
        Element newPrivate = new Element("Me - Decrypt", key, 0);
        this.model.setElementAt(newPrivate, 0);
        this.myPrivate = newPrivate;
        //this.list.setModel(this.model);
    }

    void appendElement(String string, Key value) {
        this.appendElement(new Element(string, value, this.model.size()));
    }

    void appendElement(Element value) {
        this.model.add(this.model.size(), value);
    }

    Key getSelectedKey() {
        return this.list.getSelectedValue().getKeyValue();
    }

    boolean isSelected() {
        return !this.list.isSelectionEmpty();
    }

    public DefaultListModel<Element> getModel() {
        DefaultListModel<Element> model = new DefaultListModel<>();
        for (int i = 0; i < this.model.getSize(); i++) {
            model.addElement(this.model.elementAt(i));
        }

        model.removeElement(this.myPrivate);
        return model;
    }

    public void setModel(DefaultListModel<Element> model) {
        model.add(0, this.myPrivate);
        this.model = model;
        this.list.setModel(model);
    }
}
