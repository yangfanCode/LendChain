package com.lend.lendchain.widget.chart.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.Log;

import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.widget.chart.bean.CMinute;
import com.lend.lendchain.widget.chart.bean.StickData;
import com.lend.lendchain.widget.chart.view.KLineView;

import java.util.ArrayList;

public class DrawUtils {
    //分时图时间、价格、涨幅文字大小
    public static final float FENSHI_TEXT_SIZE = DisplayUtil.sp2px(ContextHelper.getApplication(), 13);
    //时间文字大小
    public static final float FENSHI_TIME_SIZE = DisplayUtil.sp2px(ContextHelper.getApplication(), 9);
    //当文字在最上方的时候，需要往下偏移，如高的涨幅
    public static final float FENSHI_TEXT_OFFSET = 25f;

    //折线的弯曲率
    private static float smoothness = 0.35f;


    /**
     * 传入价格和
     *
     * @param prices   价格
     * @param xUnit    x轴每两点距离
     * @param height   控件高度
     * @param max      价格最大值
     * @param min      价格最小值
     * @param fromZero 是否从0开始，意思是，假如y轴为0是否画出该点（比如在SMA5、SMA10中，并不是由0开始）
     * @return
     */
    public static float[] getLines(float[] prices, float xUnit, float height, float max, float min, boolean fromZero, float y, float xOffset) {
        float[] result = new float[prices.length * 4];
        float yUnit = (max - min) / height;
        for (int i = 0; i < prices.length - 1; i++) {
            //排除起点为0的点
            if (!fromZero && prices[i] == 0) continue;
            result[i * 4 + 0] = xOffset + i * xUnit;
            result[i * 4 + 1] = y + height - (prices[i] - min) / yUnit;
            result[i * 4 + 2] = xOffset + (i + 1) * xUnit;
            result[i * 4 + 3] = y + height - (prices[i + 1] - min) / yUnit;
        }
        return result;
    }

    /**
     * 传入价格和
     *
     * @param prices 价格
     * @param xUnit  x轴每两点距离
     * @param height 控件高度
     * @param max    价格最大值
     * @param min    价格最小值
     * @return
     */
    public static Path getLinesPath(float[] prices, float xUnit, float height, float max, float min, float y, float xOffset, boolean close) {
        return getLinesPath(prices, xUnit, height, max, min, y, xOffset, close, false);
    }

    /**
     * 传入价格和
     *
     * @param prices  价格
     * @param xUnit   x轴每两点距离
     * @param height  控件高度
     * @param max     价格最大值
     * @param min     价格最小值
     * @param isCurve true 曲线  false 直线
     * @return
     */
    public static Path getLinesPath(float[] prices, float xUnit, float height, float max, float min, float y, float xOffset, boolean close, boolean isCurve) {
        Path path = new Path();

        float yUnit = (max - min) / height;
        float floatX, floatY;
        float lX = 0;
        float lY = 0;
        for (int i = 0; i < prices.length; i++) {
            floatX = xOffset + i * xUnit + (i == 0 ? 0 : xUnit * 0.5f);
            floatY = y + height - (prices[i] - min) / yUnit;
            if (i == 0) {
                path.moveTo(floatX, floatY);
            } else {
                if (isCurve) {
                    float xStart = xOffset + (i - 1) * xUnit + ((i - 1) == 0 ? 0 : xUnit * 0.5f);
                    float yStart = y + height - (prices[(i - 1)] - min) / yUnit;
                    float x1 = xStart + lX;
                    float y1 = yStart + lY;

                    int indexL = i + 1 < prices.length ? i + 1 : i;
                    float xEnd = xOffset + indexL * xUnit + (indexL == 0 ? 0 : xUnit * 0.5f);
                    float yEnd = y + height - (prices[indexL] - min) / yUnit;
                    lX = (xEnd - xStart) / 2 * smoothness;
                    lY = (yEnd - yStart) / 2 * smoothness;
                    float x2 = floatX - lX;
                    float y2 = floatY - lY;
                    if (y1 == floatY) {
                        y2 = y1;
                    }
                    path.cubicTo(x1, y1, x2, y2, floatX, floatY);
                } else
                    path.lineTo(floatX, floatY);
            }

            //最后一个点
            if (close && i == prices.length - 1) {
                //在这里把path圈起来，添加阴影。
                path.lineTo(floatX, height);
                path.lineTo(xOffset, height);
                path.close();
            }

        }
        return path;
    }

    /**
     * 画线，并且线整体在X轴有偏移
     * 使用：画K线均线时，X轴需要加一点
     */
    public static void drawLineWithXOffset(Canvas canvas, float[] prices, float xUnit, float height, int color, float max, float min, float xOffset) {
        if (canvas == null) {
            Log.w("DrawUtils", "canvas为空");
            return;
        }
        if (prices == null || prices.length == 0) return;

        float tmax = 0f;
        float tmin = 0f;
        for (float f : prices) {
            tmax = tmax > f ? tmax : f;
            tmin = tmin < f ? tmin : f;
        }
        //如果数组中值全为0，则不画该线
        if (tmax == 0 && tmin == 0)
            return;
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(color);
        p.setStrokeWidth(2.0f);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawLines(getLines(prices, xUnit, height, max, min, false, 0, xOffset), p);
        p.reset();
//        drawLines(canvas, prices, xUnit, height, color, max, min, false, 0, xOffset);
    }

    /**
     * 画线
     *
     * @param canvas
     * @param prices   价格
     * @param xUnit    x轴每两点距离
     * @param height   控件高度
     * @param color    颜色
     * @param max      价格最大值
     * @param min      价格最小值
     * @param fromZero 是否从0开始，意思是，假如y轴为0是否画出该点（比如在SMA5、SMA10中，并不是由0开始）
     */
    public static void drawLines(Canvas canvas, float[] prices, float xUnit, float height, int color, float max, float min, boolean fromZero) {
        drawLines(canvas, prices, xUnit, height, color, max, min, fromZero, 0, 0);
    }

    public static void drawPriceShader(Canvas canvas, float[] prices, float xUnit, float height, float max, float min) {
        drawPriceShader(canvas, prices, xUnit, height, max, min, 0);
    }

    public static void drawPriceShader(Canvas canvas, float[] prices, float xUnit, float height, float max, float min, float xOffset) {
        if (prices == null || prices.length == 0) return;
        Path path = getLinesPath(prices, xUnit, height, max, min, 0, xOffset, true);
        Paint paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
//        paint.setColor(ColorUtil.COLOR_SHADOW);
//        paint.setStyle(Paint.Style.FILL);

        float maxP = 0;
        for (float p : prices) {
            maxP = p > maxP ? p : maxP;
        }
        float yUnit = (max - min) / height;
        float heightY = height - (maxP - min) / yUnit;

        LinearGradient linearGradient = new LinearGradient(0, heightY, 0, height, ColorUtil.COLOR_SHADOW_START, ColorUtil.COLOR_SHADOW_END, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);

        canvas.drawPath(path, paint);
    }

    /**
     * 画线
     *
     * @param canvas
     * @param prices   价格
     * @param xUnit    x轴每两点距离
     * @param height   控件高度
     * @param color    颜色
     * @param max      价格最大值
     * @param min      价格最小值
     * @param fromZero 是否从0开始，意思是，假如y轴为0是否画出该点（比如在SMA5、SMA10中，并不是由0开始）
     * @param y        左上角Y轴,指标并不是从左上角开始画
     */
    public static void drawLines(Canvas canvas, float[] prices, float xUnit, float height, int color, float max, float min, boolean fromZero, float y, float xOffset) {
        drawLines(canvas, prices, xUnit, height, color, max, min, fromZero, y, xOffset, 2);
    }

    /**
     * 画线
     *
     * @param canvas
     * @param prices      价格
     * @param xUnit       x轴每两点距离
     * @param height      控件高度
     * @param color       颜色
     * @param max         价格最大值
     * @param min         价格最小值
     * @param fromZero    是否从0开始，意思是，假如y轴为0是否画出该点（比如在SMA5、SMA10中，并不是由0开始）
     * @param y           左上角Y轴,指标并不是从左上角开始画
     * @param strokeWidth 线条宽度
     */
    public static void drawLines(Canvas canvas, float[] prices, float xUnit, float height, int color, float max, float min, boolean fromZero, float y, float xOffset, float strokeWidth) {
        if (canvas == null) {
            Log.w("DrawUtils", "canvas为空");
            return;
        }
        if (prices == null || prices.length == 0) return;
        Path path = getLinesPath(prices, xUnit, height, max, min, 0, xOffset, false);

        float tmax = 0f;
        float tmin = 0f;
        for (float f : prices) {
            tmax = tmax > f ? tmax : f;
            tmin = tmin < f ? tmin : f;
        }
        //如果数组中值全为0，则不画该线
        if (tmax == 0 && tmin == 0)
            return;
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(color);
        p.setStrokeWidth(strokeWidth);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, p);
//        canvas.drawLines(getLines(prices, xUnit, height, max, min, fromZero, y, xOffset), p);
        p.reset();
    }

    /**
     * 画烛形图
     *
     * @param canvas
     * @param maxY   最高 传入计算完成的值（对应x,y轴）max/yUnit
     * @param minY   最低 传入计算完成的值（对应x,y轴）
     * @param openY  开盘 传入计算完成的值（对应x,y轴）
     * @param closeY 收盘 传入计算完成的值（对应x,y轴）
     * @param x      x轴 烛形图左上x坐标
     * @param y      y轴坐标 烛形图左上y坐标
     * @param width  屏幕宽度
     */
    public static void drawCandle(Canvas canvas, float maxY, float minY, float openY, float closeY, float x, float y, float drawCount, float width) {
        if (canvas == null) {
            Log.w("DrawUtils", "canvas为空");
            return;
        }
        //当在坐标系之外，不画
        if (x < 0 || y < 0) return;
        float xUnit = width / drawCount;
        float diff = xUnit - xUnit / KLineView.WIDTH_SCALE;
        //是否上涨,由于计算成了Y轴坐标，所以上面的小，下面的大
        boolean isRise = closeY <= openY;
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(ColorUtils.getTextColorAsh(isRise ? 1 : -1, 0));
        p.setStrokeWidth(2.0f);
        canvas.drawLine(x + xUnit / 2, y, x + xUnit / 2, y + (minY - maxY) + 1, p);
        canvas.drawRect(x + diff / 2, y + ((!isRise ? closeY : openY) - maxY), x + xUnit - diff / 2, y + ((!isRise ? openY : closeY) - maxY) + 1, p);
        p.reset();
    }


    /**
     * K线：Y轴价格
     *
     * @param canvas
     * @param max
     * @param min
     * @param height
     */
    public static void drawKLineYPrice(Canvas canvas, double max, double min, float height) {
        double diff = max - min;
        String p1 = NumberUtil.getMoneyString(max);
        String p2 = NumberUtil.getMoneyString(min + diff * 3 / 4);
        String p3 = NumberUtil.getMoneyString(min + diff * 2 / 4);
        String p4 = NumberUtil.getMoneyString(min + diff * 1 / 4);
        String p5 = NumberUtil.getMoneyString(min);
        Paint p = new Paint();
        p.setColor(ColorUtils.getTextColorAsh(1, 0));
        p.setTextSize(FENSHI_TEXT_SIZE);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(p1, 0, FENSHI_TEXT_OFFSET, p);
        canvas.drawText(p2, 0, height * 1 / 4, p);
        canvas.drawText(p3, 0, height * 2 / 4, p);
        canvas.drawText(p4, 0, height * 3 / 4, p);
        canvas.drawText(p5, 0, height, p);
    }

    /**
     * 分时线：Y轴涨跌幅
     *
     * @param canvas
     * @param max    最大值
     * @param min    最小值
     * @param yd     昨收
     */
    public static void drawYPercentAndPrice(Canvas canvas, double max, double min, double yd, float width, float height) {
        //计算出最大涨跌幅
        double upPercent = (max - yd) / yd;
        double downPercent = (min - yd) / yd;
        double maxPercent = Math.abs(upPercent) > Math.abs(downPercent) ? upPercent : downPercent;
        double halfPercent = maxPercent / 2d;
        //计算出最大价格
        double diff = Math.abs(max - yd) > Math.abs(min - yd) ? Math.abs(max - yd) : Math.abs(min - yd);
        String p1 = NumberUtil.getMoneyString(yd + diff);
        String p2 = NumberUtil.getMoneyString(yd + diff / 2);
        String p3 = NumberUtil.getMoneyString(yd - diff / 2);
        String p4 = NumberUtil.getMoneyString(yd - diff);

        Paint p = new Paint();
        p.setColor(ColorUtils.getTextColorAsh(1, 0));
        p.setTextSize(FENSHI_TEXT_SIZE);
        p.setTextAlign(Paint.Align.RIGHT);
        //最大涨幅(价格)
        canvas.drawText(NumberUtil.getPercentString(Math.abs(maxPercent)), width, FENSHI_TEXT_OFFSET, p);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(p1, 0, FENSHI_TEXT_OFFSET, p);
        //一半涨幅(价格)
        p.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(NumberUtil.getPercentString(Math.abs(halfPercent)), width, height * 1 / 4, p);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(p2, 0, height * 1 / 4, p);
        //中间0%
        p.setColor(ColorUtils.getTextColorAsh(0, 0));
        p.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("0.00%", width, height / 2, p);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(NumberUtil.getMoneyString(yd), 0, height * 2 / 4, p);
        //跌幅一半
        p.setColor(ColorUtils.getTextColorAsh(0, 1));
        p.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("-" + NumberUtil.getPercentString(Math.abs(halfPercent)), width, height * 3 / 4, p);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(p3, 0, height * 3 / 4, p);
        //最下面跌幅
        p.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("-" + NumberUtil.getPercentString(Math.abs(maxPercent)), width, height - 4, p);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(p4, 0, height - 4, p);
        p.reset();
    }

    /**
     * K线:下面的时间
     *
     * @param canvas
     * @param s1
     * @param s2
     * @param width
     * @param height
     */
    public static void drawKLineXTime(Canvas canvas, String s1, String s2, float width, float height) {

        Paint p = new Paint();
        p.setTextSize(FENSHI_TIME_SIZE);
        p.setColor(ColorUtil.TIME_COLOR);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(s1, 0, height, p);
        p.setTextAlign(Paint.Align.RIGHT);
        if (s2 != null)
            canvas.drawText(s2, width, height, p);
    }

    /**
     * K线:最高最低价
     *
     * @param canvas
     * @param s1
     * @param width
     * @param x
     * @param y
     */
    public static void drawKLineMinMaxPrice(Canvas canvas, String s1, float width, float x, float y) {
        Paint p = new Paint();
        p.setTextSize(FENSHI_TIME_SIZE);
        p.setColor(ColorUtil.K_TEXT_COLOR_MIN_MAX);
        s1 = x < width / 2 ? "←" + s1 : s1 + "→";
        p.setTextAlign(x < width/ 2 ? Paint.Align.LEFT : Paint.Align.RIGHT);
        canvas.drawText(s1, x, y, p);
    }

    /**
     * 指标1：VOL
     * 画VOL线
     *
     * @param canvas
     * @param xUnit   x轴单位距离
     * @param y       左上y坐标
     * @param height  绘画区域高度
     * @param max     最大量
     * @param yd      昨收
     * @param minutes 量集合
     */
    public static void drawVOLRects(Canvas canvas, float xUnit, float y, float height, double max, float yd, ArrayList<CMinute> minutes) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        float yUnit = height / (float) max;
        p.setTextSize(FENSHI_TEXT_SIZE);
        p.setColor(Color.BLACK);
        float diff = xUnit - xUnit / KLineView.WIDTH_SCALE;
        for (int i = 0; i < minutes.size(); i++) {
            if (minutes.get(i).getCount() != 0) {
                int color = 0;
                if (i == 0) {
                    color = ColorUtils.getTextColorAsh(minutes.get(i).getPrice() > yd ? 1 : -1, 0);
                } else {
                    color = ColorUtils.getTextColorAsh(minutes.get(i).getPrice() > minutes.get(i - 1).getPrice() ? 1 : -1, 0);
                }
                p.setColor(color);
                canvas.drawRect(xUnit * i, y + (height - (float) minutes.get(i).getCount() * yUnit), xUnit * (i + 1) - diff, y + height, p);
            }
        }
    }

    /**
     * 指标1：VOL
     * 画VOL线,K线使用
     *
     * @param canvas
     * @param y      左上y坐标
     * @param height 绘画区域高度
     * @param max    最大量
     * @param datas  量集合
     */
    public static void drawVOLRects(Canvas canvas, float xUnit, float y, float height, double max, ArrayList<StickData> datas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        float yUnit = (float) (height / max);
        p.setTextSize(FENSHI_TEXT_SIZE);
        p.setColor(Color.BLACK);
        float diff = xUnit - xUnit / KLineView.WIDTH_SCALE;
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getCount() != 0) {
                StickData data = datas.get(i);
                int color = ColorUtils.getTextColorAsh(data.getClose() > data.getOpen() ? 1 : -1, 0);
                p.setColor(color);
                canvas.drawRect(xUnit * i, y + (height - (float) data.getCount() * yUnit), xUnit * (i + 1) - diff, y + height, p);
            }
        }
    }

    public static float getY(float mainH, float input, float yMin, float yUnit) {
        return mainH - (input - yMin) / yUnit;
    }
}
