package com.rss.pinkbike.util;

import com.rss.pinkbike.entities.RssEntity;

import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/18/13
 * Time: 11:16 PM
 */
public class ApplicationManager {
    private static ApplicationManager instance;
    private HashMap<Integer, RssEntity> mapToShow = new HashMap<Integer, RssEntity>();
    private long startTime = new Date().getTime();
    private boolean needToUpdate = true;

    private ApplicationManager() {

    }

    public static void initInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
    }

    public static ApplicationManager getInstance() {
        return instance;
    }

    public HashMap<Integer, RssEntity> getMapToShow() {
        return mapToShow;
    }

    public void setMapToShow(HashMap<Integer, RssEntity> mapToShow) {
        this.mapToShow = mapToShow;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isNeedToUpdate() {
        return needToUpdate;
    }

    public void setNeedToUpdate(boolean needToUpdate) {
        this.needToUpdate = needToUpdate;
    }
}
