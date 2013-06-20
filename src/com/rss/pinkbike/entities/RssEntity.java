package com.rss.pinkbike.entities;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/4/13
 * Time: 2:05 PM
 */
public class RssEntity {

    private int id;
    private String title;
    private String description;
    private Date pubDate;
    private String link;
    private String imgName;
    private int state;
    private int position;

    public RssEntity() {
    }

    public RssEntity(String title, String description, Date pubDate, String link, String imgName, int state, int position) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.link= link;
        this.imgName = imgName;
        this.state = state;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean getStateAsBoolean() {
        return state != 0;
    }
}
