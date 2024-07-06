package com.tedu.element;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;

/**
 * author: 李熠烜
 * 类属性
 * 位置和尺寸：
 * int x, y：元素的坐标位置。
 * int w, h：元素的宽度和高度。
 * 图像和排序：
 * ImageIcon icon：元素的图标，用于绘制元素。
 * int sort：用于排序的字段，可能在绘制顺序或碰撞检测中使用。
 * 状态：
 * boolean live：元素是否存活的标志。
 */

public abstract class ElementObj implements Comparable<ElementObj> {

    private int x;
    private int y;
    // private AtomicInteger x = new AtomicInteger(0);
    // private AtomicInteger y = new AtomicInteger(0);
    private int w;
    private int h;
    private ImageIcon icon;
    private int sort;


    private boolean live = true;

    // 不含参构造函数
    public ElementObj() {
    }


    public abstract void showElement(Graphics g);


    public ElementObj createElement(String str) {
        return this;
    }


    public void keyClick(boolean isPressed, int key) {

    }

    public void count(long gameTime) {
    }


    public final void model(long gameTime) {
        updateImage(gameTime); // 换装需要间隔时间
        move(); // 移动
        add(); // 发射子弹
    }


    protected void add() {
    }


    protected void updateImage(long gameTime) {
    }


    protected void move() {
    }


    public void die(long gameTime) {
    }


    public Rectangle getRectangle() {
        return new Rectangle(x, y, w, h);
        // return new Rectangle(x.get(), y.get(), w, h);
    }


    public boolean collide(ElementObj obj) {
        return this.getRectangle().intersects(obj.getRectangle());
    }


    public int getX() {
        return x;
        // return x.get();
    }

    public void setX(int x) {
        // System.out.println("some motherfucker use setX with " + x);
        // this.x.set(x);
        this.x=x;
    }

    public int getY() {
        // return y.get();
        return y;
    }

    public void setY(int y) {
        // System.out.println("some motherfucker use setY with " + y);
        // this.y.set(y);
        this.y=y;
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

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

}
