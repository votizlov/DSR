package ru.org.dsr.domain;

import java.util.List;

public class FullItem {
    private Item item;
    private List<Comment> comments;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "FullItem{" +
                "item=" + item +
                ", comments=" + comments +
                '}';
    }
}
