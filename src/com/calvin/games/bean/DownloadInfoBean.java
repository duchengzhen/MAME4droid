package com.calvin.games.bean;

/**
 * 记录下载类实体
 * Created by calvin on 2014/8/3.
 */
public class DownloadInfoBean {
    /**下载器id*/
    private int threadId;
    /**开始点*/
    private int startPos;
    /**结束点*/
    private int endPos;
    /**完成度*/
    private int competeSize;
    /**下载地址标识*/
    private String url;

    public DownloadInfoBean(){

    }

    public DownloadInfoBean(int threadId, int startPos, int endPos, int competeSize, String url) {
        this.threadId = threadId;
        this.startPos = startPos;
        this.endPos = endPos;
        this.competeSize = competeSize;
        this.url = url;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public int getCompeteSize() {
        return competeSize;
    }

    public void setCompeteSize(int competeSize) {
        this.competeSize = competeSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DownloadInfoBean{" +
                "threadId=" + threadId +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", competeSize=" + competeSize +
                ", url='" + url + '\'' +
                '}';
    }
}





















