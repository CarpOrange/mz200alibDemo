package com.haoxueche.mz200alib.util;

import android.os.Environment;
import android.text.TextUtils;

import com.haoxueche.winterlog.L;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lyc(987424501@qq.com) on 2019/1/15.
 */
public class FileUtil {
    // 根目录
    public static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    // 计时图片文件夹
    public static final String IMAGE_DIR = "timingPics";
    public static final String DEFAULT_IMG_DIR = getRoot() + IMAGE_DIR;
    public static final String IMAGE_TEMP_NAME = "faceImg.jpg";
    public static final String IMAGE_TEMP_ABSOLUTE_DIR = getRoot() + IMAGE_DIR + File.separator +
            IMAGE_TEMP_NAME;

    /**
     * 保存byte[] 到指定路径
     *
     * @param data
     * @param path
     * @return
     */
    public static boolean saveBytesToFile(byte[] data, String path) {

        if (data == null || TextUtils.isEmpty(path)) {
            return false;
        }

        boolean saved = false;

        System.out.println("File Path==" + path);

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            L.e(e);
        }

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            saved = true;

        } catch (FileNotFoundException e) {
            L.e(e);
        } catch (IOException e) {
            L.e(e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                L.e(e);
            }
        }
        return saved;
    }

    public static String getRoot() {
        String path = FileUtil.ROOT;
        if(TextUtils.isEmpty(path) || path.contains("null")) {
            throw new IllegalStateException("设备存储路径未初始化:" + path);
        }
        L.d("root==" + path);
        return path + File.separator;
    }

    public static float getSimilarity (byte[] data1, byte[] data2) {
        int sameCount = 0;
        int totalCount;
        int caculateCount;
        if( data1.length > data2.length) {
            totalCount = data1.length;
            caculateCount = data2.length;
        } else {
            totalCount = data2.length;
            caculateCount = data1.length;
        }
        for(int i = 0; i < caculateCount; i++) {
            if(data1[i] == data2[i]) {
                sameCount ++;
            }
        }
        return sameCount * 1.0f / totalCount;
    }

    /**
     * 通过输入流获取字节数组
     *
     * @param inStream
     * @return
     * @throws IOException
     */
    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }


}
