package ru.org.dsr.domain;

public class Item {
    private ItemID itemID;
    private String desc;
    private String urlImg;

    public Item(ItemID itemID, String desc) {
        this.itemID = itemID;
        this.desc = desc;
    }

    public Item(ItemID itemID, String desc, String urlImg) {
        this.itemID = itemID;
        this.desc = desc;
        this.urlImg = urlImg;
    }

    public Item(ItemID itemID) {
        this.itemID = itemID;
    }

    public Item() {}

    public ItemID getItemID() {
        return itemID;
    }

    public void setItemID(ItemID itemID) {
        this.itemID = itemID;
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

    @Override
    public String toString() {
        return "Item{" +
                "itemID=" + itemID +
                ", desc='" + desc + '\'' +
                '}';
    }
}
