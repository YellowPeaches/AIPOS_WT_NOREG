package com.wintec.lamp.utils;

import android.graphics.Color;

import com.wintec.lamp.R;

import java.util.Random;

/**
 * @author 赵冲
 * @description:
 * @date :2021/1/14 14:08
 */
public class ColorUtil {

    //随机颜色
    public static String getRandColor() {
        String R, G, B;
        Random random = new Random();
        R = Integer.toHexString(random.nextInt(256)).toUpperCase();
        G = Integer.toHexString(random.nextInt(256)).toUpperCase();
        B = Integer.toHexString(random.nextInt(256)).toUpperCase();

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        return "#" + R + G + B;
    }


    public static int getColor() {
        int[] color = new int[]{Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.MAGENTA, 0xFFCC00FF, 0xFFFFA500};
        Random random = new Random();
        int i = random.nextInt(color.length - 1);
        return color[i];
    }
}
