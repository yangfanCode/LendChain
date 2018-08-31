package com.lend.lendchain.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * Created by yangfan on 2018/1/31.
 */
public class QRCodeUtil {

    private static int image_halfwidth = 50;//宽度值，影响中间图片大小
    private static int margin=0;//白边大小 取值0-4


    /**
     * 生成二维码，默认大小为500*500
     *
     * @param text 需要生成二维码的文字、网址等
     * @return bitmap
     */
    public static Bitmap createQrcode(String text) {
        return createQrcode(text, 500,0);
    }

    /**
     * 生成二维码
     *
     * @param text 需要生成二维码的文字、网址等
     * @param size 需要生成二维码的大小（）
     * @return bitmap
     */
    public static Bitmap createQrcode(String text, int size,int margin) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, margin);   //设置白边
            BitMatrix bitmatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, size, size, hints);
            //            bitmatrix=deleteWhite(bitmatrix);//删除白边
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitmatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成带logo的二维码，默认二维码的大小为500，logo为二维码的1/5
     *
     * @param text 需要生成二维码的文字、网址等
     * @param mbitmap logo文件
     * @return bitmap
     */
    public static Bitmap createQrcodeWithLogo(String text, Bitmap mbitmap) {
        return createQrcodeWithLogo(text,500,mbitmap,0);
    }

    /**
     * 生成带logo的二维码，logo默认为二维码的1/5
     *
     * @param text 需要生成二维码的文字、网址等
     * @param size 需要生成二维码的大小（）
     * @param mbitmap logo文件
     * @return bitmap
     */
    public static Bitmap createQrcodeWithLogo(String text, int size, Bitmap mbitmap,int margin) {
        try {
            image_halfwidth = size/10;
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
  /*
   * 设置容错级别，默认为errorcorrectionlevel.l
   * 因为中间加入logo所以建议你把容错级别调至h,否则可能会出现识别不了
   */
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, margin);   //设置白边
            BitMatrix bitmatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, size, size, hints);
//            bitmatrix=deleteWhite(bitmatrix);//删除白边
            int width = bitmatrix.getWidth();//矩阵高度
            int height = bitmatrix.getHeight();//矩阵宽度
            int halfw = width / 2;
            int halfh = height / 2;

            Matrix m = new Matrix();
            float sx = (float) 2 * image_halfwidth / mbitmap.getWidth();
            float sy = (float) 2 * image_halfwidth
                    / mbitmap.getHeight();
            m.setScale(sx, sy);
            //设置缩放信息
            //将logo图片按martix设置的信息缩放
            mbitmap = Bitmap.createBitmap(mbitmap, 0, 0,
                    mbitmap.getWidth(), mbitmap.getHeight(), m, false);

            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (x > halfw - image_halfwidth && x < halfw + image_halfwidth
                            && y > halfh - image_halfwidth
                            && y < halfh + image_halfwidth) {
                        //该位置用于存放图片信息
                        //记录图片每个像素信息
                        pixels[y * width + x] = mbitmap.getPixel(x - halfw
                                + image_halfwidth, y - halfh + image_halfwidth);
                    } else {
                        if (bitmatrix.get(x, y)) {
                            pixels[y * size + x] = 0xff000000;
                        } else {
                            pixels[y * size + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除白边
     * */
    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }
}
