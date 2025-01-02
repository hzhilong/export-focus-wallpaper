package io.github.hzhilong.exportwallpaper.bean;

import java.io.File;

/**
 * 导出的图片
 *
 * @author hzhilong
 * @version 1.0
 */
public class ExportedImg {
    private File src;
    private File des;
    private int w;
    private int h;

    public ExportedImg() {
    }

    public ExportedImg(File src, File des, int w, int h) {
        this.src = src;
        this.des = des;
        this.w = w;
        this.h = h;
    }

    public File getSrc() {
        return src;
    }

    public void setSrc(File src) {
        this.src = src;
    }

    public File getDes() {
        return des;
    }

    public void setDes(File des) {
        this.des = des;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
