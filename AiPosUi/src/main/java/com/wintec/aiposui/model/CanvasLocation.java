package com.wintec.aiposui.model;

/**
 * @author 赵冲
 * @description:
 * @date :2021/6/30 9:02
 */
public class CanvasLocation {

    private Integer heigth;
    private Integer widht;
    private Integer xOffset;
    private Integer yOffset;

    public CanvasLocation(Integer xOffset, Integer yOffset, Integer heigth, Integer widht) {
        this.heigth = heigth;
        this.widht = widht;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public Integer getHeigth() {
        return heigth;
    }

    public void setHeigth(Integer heigth) {
        this.heigth = heigth;
    }

    public Integer getWidht() {
        return widht;
    }

    public void setWidht(Integer widht) {
        this.widht = widht;
    }

    public Integer getxOffset() {
        return xOffset;
    }

    public void setxOffset(Integer xOffset) {
        this.xOffset = xOffset;
    }

    public Integer getyOffset() {
        return yOffset;
    }

    public void setyOffset(Integer yOffset) {
        this.yOffset = yOffset;
    }
}
