package com.calvin.games.bean;

/**
 * 游戏类主体
 * Created by calvin on 2014/8/3.
 */
public class GameBean {
    /**游戏名字*/
    private String name;
    /**游戏封面图片链接*/
    private String cover;
    /**游戏描述*/
    private String description;
    /**游戏类型,如:格斗*/
    private String type;
    /**游戏截图连接*/
    private String[] shots;
    /**游戏数据包大小MB*/
    private String size;
    /**游戏被下载数量*/
    private String count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getShots() {
        return shots;
    }

    public void setShots(String[] shots) {
        this.shots = shots;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
