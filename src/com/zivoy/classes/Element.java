package com.zivoy.classes;

import com.zivoy.keyHandlers.Key;

import java.util.Objects;

public class Element {
    public String name;
    public int index;
    private Key keyValue;

    public Element(String name, Key keyValue, int index) {
        this.name = name;
        this.keyValue = keyValue;
        this.index = index;
    }

    public Key getKeyValue() {
        return keyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;
        return index == element.index &&
                Objects.equals(name, element.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, index);
    }

    @Override
    public String toString() {
        return name;
    }
}
