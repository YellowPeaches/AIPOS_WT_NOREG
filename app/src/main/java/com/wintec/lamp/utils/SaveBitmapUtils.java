package com.wintec.lamp.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SaveBitmapUtils {
    private static final String IMAGE_PATH = "images2";

    /**
     * 保存图片到SD卡
     *
     * @param bitmap 图片的bitmap对象
     * @return
     */
    public static String savePhotoToSDCard(Bitmap bitmap) {
        String newFilePath = savePhotoToSDCard(bitmap, 100);
        return newFilePath;
    }

    public static String savePhotoToSDCard(Bitmap bitmap, int quality) {
        String newFilePath = getPhotoPath(bitmap, quality);
        return newFilePath;
    }

    private static String getPhotoPath(Bitmap bitmap, int quality) {
        FileOutputStream fileOutputStream = null;
        String newFilePath = getImagePath();
        if (newFilePath == null || newFilePath.length() == 0) {
            return null;
        }
        File file = FileUtil.createNewFile(newFilePath);
        if (file == null) {
            return null;
        }
        try {
            fileOutputStream = new FileOutputStream(newFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality,
                    fileOutputStream);
        } catch (FileNotFoundException e1) {
            return null;
        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                return null;
            }
        }
        return newFilePath;
    }

    public static String getImagePath() {
        String imagePath = FileUtil.getRootFilePath() + IMAGE_PATH
                + File.separator;
        boolean createSuccess = FileUtil.createDirFile(imagePath);
        if (createSuccess) {
            return imagePath + UUID.randomUUID().toString() + ".jpg";
        }
        return null;
    }

    /**
     * 删除图片缓存目录
     */
    public static void deleteImageFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String imagePath = FileUtil.getRootFilePath() + IMAGE_PATH
                        + File.separator;
                File dir = new File(imagePath);
                if (dir.exists()) {
                    FileUtil.delFolder(imagePath);
                }
            }
        }).start();
    }

}
