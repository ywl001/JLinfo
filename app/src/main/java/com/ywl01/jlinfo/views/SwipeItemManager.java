package com.ywl01.jlinfo.views;

/**
 * Created by ywl01 on 2016/12/27.
 */

public class SwipeItemManager {
    private static SwipeItemManager instance = new SwipeItemManager();

    private SwipeItem currentItem;
    private SwipeItem prevItem;

    private SwipeItemManager() {
    }

    public static SwipeItemManager getInstance() {
        return instance;
    }

    public void setItem(SwipeItem item) {
        System.out.println("family_item manager set family_item");
        currentItem = item;
    }

    public void clearItem(SwipeItem item) {
        System.out.println("family_item manager clear family_item");
        if(currentItem == item)
            currentItem = null;
    }

    public void closeItem() {
        if(currentItem != null)
            currentItem.close();
    }

    public boolean isCanSwip(SwipeItem item) {
        if (currentItem == null) {
            return true;
        }
        else{
            return item == currentItem;
        }
    }

}
