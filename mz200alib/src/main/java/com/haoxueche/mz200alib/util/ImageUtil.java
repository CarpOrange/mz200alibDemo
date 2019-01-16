package com.haoxueche.mz200alib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.ByteArrayOutputStream;

/**
 * Created by xiezhongming on 17/9/22.
 * 图片处理工具类
 */

public class ImageUtil {

    /**
     * 压缩文件
     *
     * @param image
     * @param size  压缩大小(kb)
     * @return
     */
    public static byte[] compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > size
                && options > 0) { // 循环判断如果压缩后图片是否大于35kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
            // 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        return baos.toByteArray();
    }

    /**
     * 图片上绘制文字
     */
    public static Bitmap drawTextToBitmap(Bitmap bitmap, String text,
                                          TextPaint textPaint, int paddingLeft, int paddingTop) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        textPaint.setDither(true); // 获取跟清晰的图像采样
        textPaint.setFilterBitmap(true);// 过滤一些
        StaticLayout layout = new StaticLayout(text, textPaint, 1000, Layout.Alignment
                .ALIGN_NORMAL, 1.0F, 0.0F, true);
        // 这里的参数300，表示字符串的长度，当满300时，就会换行，也可以使用“\r\n”来实现换行
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.translate( DipUtil.dp2px(paddingLeft),
                bitmap.getHeight() - bounds.height() * 3);
        layout.draw(canvas);
        return bitmap;

    }

    /**
     * 镜面翻转
     *
     * @param a
     * @return
     */
    public static Bitmap convert(Bitmap a) {
        int w = a.getWidth();
        int h = a.getHeight();
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        Matrix m = new Matrix();
//        m.postScale(1, -1);   //镜像垂直翻转
        m.postScale(-1, 1);   //镜像水平翻转
//        m.postRotate(-90);  //旋转-90度
        Bitmap new2 = Bitmap.createBitmap(a, 0, 0, w, h, m, true);
        cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()), new Rect(0, 0, w,
                h), null);
        return newb;
    }

    /**
     * 图像旋转
     *
     * @param a
     * @param degress
     * @return
     */
    public static Bitmap rotate(Bitmap a, int degress) {
        int w = a.getWidth();
        int h = a.getHeight();
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        Matrix m = new Matrix();
//        m.postScale(1, -1);   //镜像垂直翻转
//        m.postScale(-1, 1);   //镜像水平翻转
        m.postRotate(degress);  //旋转-90度
        Bitmap new2 = Bitmap.createBitmap(a, 0, 0, w, h, m, true);
        cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()), new Rect(0, 0, w,
                h), null);
        return newb;
    }

    public static Bitmap nv21toBitmap(Context context, byte[] nv21Data, int width, int height) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element
                .U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21Data.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY
                (height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21Data);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);

        return bmpout;

    }

    public byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

}
