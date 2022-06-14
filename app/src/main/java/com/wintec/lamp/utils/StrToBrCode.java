package com.wintec.lamp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.OneDimensionalCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wintec.lamp.base.Const;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/12 11:23
 */
public class StrToBrCode {
    /**
     * @param expectWidth 期望的宽度
     * @param maxWidth    最大允许宽度
     * @param contents    生成条形码的内容
     * @param height
     * @return
     */
    public static Bitmap getBarCodeWithoutPadding(int expectWidth, int maxWidth, int height, String contents, BarcodeFormat barcodeFormat) {


        int realWidth = getBarCodeNoPaddingWidth(expectWidth, contents, maxWidth);

        return syncEncodeBarcode(contents, realWidth, height, barcodeFormat);
    }

    private static int getBarCodeNoPaddingWidth(int expectWidth, String contents, int maxWidth) {
        boolean[] code = new Code128Writer().encode(contents);

        int inputWidth = code.length;

        //code:210000000000000082 code.length:134 expectWidth:397 maxWidth:435
        // Add quiet zone on both sides.
        //int fullWidth = inputWidth + 0;

        double outputWidth = (double) Math.max(expectWidth, inputWidth);
        double multiple = outputWidth / inputWidth;

        //multiple:2.962686567164179

        //优先取大的
        int returnVal = 0;
        int ceil = (int) Math.ceil(multiple);
        if (inputWidth * ceil <= maxWidth) {
            returnVal = inputWidth * ceil;
        } else {
            int floor = (int) Math.floor(multiple);
            returnVal = inputWidth * floor;
        }


        return returnVal;
    }

    /**
     * 同步创建条形码图片
     *
     * @param content 要生成条形码包含的内容
     * @param width   条形码的宽度，单位px
     * @param height  条形码的高度，单位px
     * @return 返回生成条形的位图
     * <p>
     * 白边问题:
     * https://blog.csdn.net/sunshinwong/article/details/50156017
     * 已知高度,计算宽度:
     */
    private static Bitmap syncEncodeBarcode(String content, int width, int height, BarcodeFormat barcodeFormat) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, barcodeFormat, width, height, hints);
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            bitmap = showContent(bitmap, content, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 绘制条形码
     *
     * @param content       要生成条形码包含的内容
     * @param width         条形码的宽度
     * @param height        条形码的高度
     * @param isShowContent 否则显示条形码包含的内容
     * @return 返回生成条形的位图
     */
    public static Bitmap createBarcode(String content, int width, int height, boolean isShowContent, BarcodeFormat barcodeFormat, OneDimensionalCodeWriter oneDimensionalCodeWriter) {
        int widthPix = oneDimensionalCodeWriter.encode(content).length;
        int heightPix = 200;
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 容错级别 这里选择最高H级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, "0");
        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            // 图像数据转换，使用了矩阵转换 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bitMatrix = writer.encode(content, barcodeFormat, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
//             下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000; // 黑色
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;// 白色
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            if (isShowContent) {
                bitmap = showContent(bitmap, content, width, height);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 显示条形的内容
     *
     * @param bCBitmap 已生成的条形码的位图
     * @param content  条形码包含的内容
     * @return 返回生成的新位图
     */
    private static Bitmap showContent(Bitmap bCBitmap, String content, int width, int height) {
        if (TextUtils.isEmpty(content) || null == bCBitmap) {
            return null;
        }

//        RectF rectF = new RectF(startX, startY, endX, endY);  //画一个矩形

        Paint paint = new Paint();
        int textSize = 20;
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(widthPix);
        int textWidth = (int) paint.measureText(content);


        Paint.FontMetrics fm = paint.getFontMetrics();
        //绘制字符串矩形区域的高度
        int textHeight = (int) (fm.bottom - fm.top);
        bCBitmap = BmpUtil.scaleBitmap(bCBitmap, width, height - textHeight);
        float startX = 0;
        float startY = bCBitmap.getHeight();
        float endX = bCBitmap.getWidth();
        float endY = startY;
        // x 轴的缩放比率
        float scaleRateX = bCBitmap.getWidth() / textWidth;
        paint.setTextScaleX(scaleRateX);
        //创建一个图层，然后在这个图层上绘制bCBitmap、content
        Bitmap bitmap = Bitmap.createBitmap(bCBitmap.getWidth(), (int) endY + textHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bCBitmap, 0, 0, null);

//        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//        float baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 4;
        // canvas.drawText(content, rectF.centerX(), baseline, paint);
        //  canvas.drawRoundRect(rectF,0,0,paint);
        canvas.drawText(content, endX / 2, (int) endY + textHeight, paint);
        return bitmap;
    }

    public static Bitmap createQRCode(String content, int inputWidth, int inputHeight) {
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();//定义二维码参数
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//设置字符
        hints.put(EncodeHintType.ERROR_CORRECTION, "Q"); // 容错级别设置 L,H,M,Q
        hints.put(EncodeHintType.MARGIN, "0"); // 空白边距设置
        try {
//    生成二维码
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 400, 400, hints);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            bitmap = BmpUtil.scaleBitmap(bitmap, inputWidth, inputHeight);
//            if ("1".equals(Const.getSettingValue(Const.QRCODE_NUMBER_FLAG)))
//            {
//                bitmap = showContentQRCode(bitmap, content,inputWidth,inputHeight);
//            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap showContentQRCode(Bitmap bCBitmap, String content, int width, int height) {
        if (TextUtils.isEmpty(content) || null == bCBitmap) {
            return null;
        }

//        RectF rectF = new RectF(startX, startY, endX, endY);  //画一个矩形

        Paint paint = new Paint();
        int textSize = 20;
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setTextSize(widthPix);
        int textWidth = (int) paint.measureText(content);


        Paint.FontMetrics fm = paint.getFontMetrics();
        //绘制字符串矩形区域的高度
        int textHeight = (int) (fm.bottom - fm.top);
        float startX = 0;
        float startY = bCBitmap.getHeight() + 1;
        float endX = bCBitmap.getWidth();
        float endY = startY;
        // x 轴的缩放比率
        float scaleRateX = bCBitmap.getWidth() / textWidth;
        paint.setTextScaleX(scaleRateX);
        //创建一个图层，然后在这个图层上绘制bCBitmap、content
        Bitmap bitmap = Bitmap.createBitmap(bCBitmap.getWidth(), (int) endY + textHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bCBitmap, 0, 0, null);

//        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//        float baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 4;
        // canvas.drawText(content, rectF.centerX(), baseline, paint);
        //  canvas.drawRoundRect(rectF,0,0,paint);
        canvas.drawText(content, endX / 2, (int) endY + textHeight, paint);
        return bitmap;
    }
}
