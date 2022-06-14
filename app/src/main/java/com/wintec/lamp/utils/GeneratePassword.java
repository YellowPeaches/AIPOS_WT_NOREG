package com.wintec.lamp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GeneratePassword {

    private static Random rand;

    static {
        rand = new Random();
    }


    public static String generatePassword(Integer wordNum) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < wordNum; i++) {
            stringBuilder.append(getNum());
        }
        return stringBuilder.toString();
    }

    /**
     * 随机获取一个0-9的数字
     *
     * @return
     */
    public static int getNum() {
        return getRadomInt(0, 9);
    }

    /**
     * 获取一个范围内的随机数字
     *
     * @return
     */
    public static int getRadomInt(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

}



