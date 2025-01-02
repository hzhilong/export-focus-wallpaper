package io.github.hzhilong.exportwallpaper.bean;

/**
 * 分辨率
 *
 * @author hzhilong
 * @version 1.0
 */
public class Resolution {

    private int width;
    private int height;

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}
