package ru.org.dsr.domain;

import ru.org.dsr.search.factory.TypeItem;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Items")
public class Item {
    @Id
    @GeneratedValue
    @Column(name = "Id")
    private long id;
    @Column(name = "First_name")
    private String firstName;
    @Column(name = "Last_name")
    private String lastName;
    @Column(name = "Type")
    private String type;
    @Column(name = "Description", columnDefinition = "TEXT")
    private String desc;
    @Column(name = "Url_image")
    private String urlImg;
    @Column(name = "Date")
    private LocalDateTime date;

    public Item(ItemID itemID, String desc) {
        firstName = itemID.getFirstName();
        lastName = itemID.getLastName();
        type = itemID.getType().toString();
        this.desc = desc;
    }

    public Item(ItemID itemID, String desc, String urlImg) {
        firstName = itemID.getFirstName();
        lastName = itemID.getLastName();
        type = itemID.getType().toString();
        this.desc = desc;
        this.urlImg = urlImg;
    }

    public Item(ItemID itemID) {
        firstName = itemID.getFirstName();
        lastName = itemID.getLastName();
        type = itemID.getType().toString();
    }

    public Item() {}

    public ItemID getItemID() {
        return new ItemID(firstName, lastName, TypeItem.valueOf(type));
    }

    public void setItemID(ItemID itemID) {
        firstName = itemID.getFirstName();
        lastName = itemID.getLastName();
        type = itemID.getType().toString();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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
        return TypeItem.valueOf(type);
    }

    public void setType(TypeItem type) {
        this.type = type.toString();
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", type=" + type +
                ", desc='" + desc + '\'' +
                ", urlImg='" + urlImg + '\'' +
                ", date=" + date +
                '}';
    }
}
