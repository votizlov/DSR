package ru.org.dsr.domain;

import javax.persistence.*;

@Entity
@Table(name = "Comments")
public class Comment {

    @Id
    @GeneratedValue
    @Column(name = "Id")
    private long id;
    @Column(name = "Item_id")
    private long idItem;
    @Column(name = "Site")
    private String site;
    @Column(name = "Page")
    private int page;
    @Column(name = "Author")
    private String author;
    @Column(name = "Title")
    private String title;
    @Column(name = "Review", columnDefinition = "TEXT")
    private String desc;
    @Column(name = "Date")
    private String date;

    public Comment(String site, String author, String title, String desc, String date) {
        this.site = site;
        this.author = author;
        this.title = title;
        this.desc = desc;
        this.date = date;
    }

    public Comment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getIdItem() {
        return idItem;
    }

    public void setIdItem(long idItem) {
        this.idItem = idItem;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
