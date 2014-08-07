package com.calvin.games.bean;

/**
 * 自定义一个记载下载器详细的类
 * Created by calvin on 2014/8/4.
 */
public class LoadInfoBean {
    /**文件大小*/
    public int fileSize;
    /**完成度*/
    private int complete;
    /**下载器标识*/
    private String url;

    public LoadInfoBean(int fileSize, int complete, String url) {
        this.fileSize = fileSize;
        this.complete = complete;
        this.url = url;
    }

    public LoadInfoBean(){}

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "LoadInfoBean{" +
                "fileSize=" + fileSize +
                ", complete=" + complete +
                ", url='" + url + '\'' +
                '}';
    }
}

































