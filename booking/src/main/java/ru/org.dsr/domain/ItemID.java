package ru.org.dsr.domain;

import org.springframework.stereotype.Component;
import ru.org.dsr.search.factory.TypeItem;

public class ItemID {
    private String firstName;
    private String lastName;
    private TypeItem type;

    public ItemID(String firstName, String lastName, TypeItem type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }

    public ItemID() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public TypeItem getType() {
        return type;
    }

    public void setType(TypeItem type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ItemID{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
