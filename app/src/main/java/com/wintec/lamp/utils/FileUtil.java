package com.wintec.lamp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    private static final String TAG = "FileUtil";
    int temp = 0;
    private static final String TEMP_PHOTO_NAME = "TempPhoto.jpg";

    public static File createFile(Bitmap bitmap, Context context) {
        String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/a/";
        File folders = new File(path);
        /*
        * @param source 需要裁剪的bitmap
          @param x 裁剪x点起始坐标（横向）
          @param y 裁剪y点起始坐标（纵向）
          @param width 裁剪后，新生成的bitmap的宽度
          @param height 裁剪后，新生成的bitmap的高度
          @return 返回一个裁剪过的bitmap 或者为操作过的(原)bitmap
          */
        bitmap = Bitmap.createBitmap(bitmap, 430, 0, 800, 1100);  //剪裁图片
        if (!folders.exists()) {
            folders.mkdirs();
        }
        File file = new File(path, TEMP_PHOTO_NAME);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.i(TAG, "" + e.toString());
        }
        return file;
    }

    public static File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 判断SD是否可以
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取根目录
     *
     * @return
     */
    public static String getRootFilePath() {
        // filePath: sdcard/
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 创建根目录
     *
     * @param path 目录路径
     */
    public static boolean createDirFile(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            return true;
        }
        return dir.mkdirs();
    }

    public static void createDir(String... dirPath) {
        File dir = null;
        for (int i = 0; i < dirPath.length; i++) {
            dir = new File(dirPath[i]);
            if (!dir.exists() && !dir.isDirectory()) {
                dir.mkdirs();
            }
        }
    }

    /**
     * 创建文件
     *
     * @param path 文件路径
     * @return 创建的文件
     */
    public static File createNewFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return file;
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹的路径
     */
    public static void delFolder(String folderPath) {
        delAllFile(folderPath);
        String filePath = folderPath;
        filePath = filePath.toString();
        File myFilePath = new File(filePath);
        myFilePath.delete();
    }

    /**
     * 删除文件
     *
     * @param path 文件的路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    /**
     * 删除文件
     *
     * @param floder 文件夹
     */
    public static void delFloderAllFile(String floder) {
        //获取根目录
        String rootPath = FileUtil.getRootFilePath();
        //文件夹路径
        String folderPath = rootPath + floder + File.separator;
        //创建文件夹
        FileUtil.createDirFile(folderPath);
        File file = new File(folderPath);
        String[] tempList = file.list();
        File temp;
        for (int i = 0; i < tempList.length; i++) {
            if (folderPath.endsWith(File.separator)) {
                temp = new File(folderPath + tempList[i]);
            } else {
                temp = new File(folderPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(folderPath + "/" + tempList[i]);
                delFolder(folderPath + "/" + tempList[i]);
            }
        }
    }

    //文件夹路径
    public static String getFirstFlodar(String string) {
        return getRootFilePath() + "/" + string + "/";
    }

    //文件夹路径
    public static String getSecondFlodar(String string, String uuid) {
        return getRootFilePath() + "/" + string + "/" + uuid + "/";
    }

    //文件路径
    public static String getUrlByFileName(String string, String fileName) {
        return getRootFilePath() + "/" + string + "/" + fileName;
    }

    //文件路径
    public static String getUrlByUuidAndFileName(String string, String uuid, String fileName) {
        return getRootFilePath() + "/" + string + "/" + uuid + "/" + fileName;
    }

    // 文件是否存在
    public static boolean isExit(String localFileUrl) {
        File dir = new File(localFileUrl);
        if (dir.exists()) {
            return true;
        } else {
            return false;
        }
    }

    //文件转化为二进制流
    public static byte[] readFileToByteArray(File file) throws IOException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            boolean n = false;
            int n1;
            while (-1 != (n1 = inputStream.read(buffer))) {
                output.write(buffer, 0, n1);
            }
            byte[] var5 = output.toByteArray();
            return var5;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }
    }

    //把从服务器获得图片的输入流InputStream写到本地磁盘
    public static void saveImageToDisk(InputStream inputStream, String floder, String finleName)
            throws IOException {
        //获取根目录
        String rootPath = FileUtil.getRootFilePath();
        //文件夹路径
        String folderPath = rootPath + floder + File.separator;
        //创建文件夹
        FileUtil.createDirFile(folderPath);
        //文件路径
        String fileName = folderPath + finleName;
        //创建文件
        FileUtil.createNewFile(fileName);
        byte[] data = new byte[1024];
        int len = 0;
        FileOutputStream fileOutputStream = null;
        fileOutputStream = new FileOutputStream(fileName);
        while ((len = inputStream.read(data)) != -1) {
            fileOutputStream.write(data, 0, len);
        }
        if (inputStream != null) {
            inputStream.close();
        }
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }


}
