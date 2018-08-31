package com.lend.lendchain.widget.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.TimeUtils;
import com.lend.lendchain.widget.chart.bean.CrossBean;
import com.lend.lendchain.widget.chart.bean.StickData;
import com.lend.lendchain.widget.chart.utils.ColorUtil;
import com.lend.lendchain.widget.chart.utils.DrawUtils;
import com.lend.lendchain.widget.chart.utils.GridUtils;
import com.lend.lendchain.widget.chart.utils.IndexParseUtil;
import com.lend.lendchain.widget.chart.utils.LineUtil;

import java.util.ArrayList;


/**
 * K线View
 */
public class KLineView extends ChartView implements ChartConstant {

    //K线所有数据
    private ArrayList<StickData> data;
    //K线展示的数据
    private ArrayList<StickData> showList;

    public KLineView(Context context) {
        this(context, null);
    }

    public KLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean onViewScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (data != null && drawCount < data.size() && Math.abs(distanceX) > DEF_PULL_LENGTH) {
            int temp = offset + (int) (0 - distanceX / DEFUALT_WIDTH);
            if (temp < 0 || temp + drawCount > data.size()) {

            } else {
                offset = temp;
                postInvalidate();
            }
            return true;
        }
        return false;
    }

    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (data == null) return super.onScale(detector);
            //放大是由1变大，缩小是由1变小
            float scale = detector.getScaleFactor();
            //没有缩放
            if (detector.getScaleFactor() == 1) return true;
//            //这个变化太快，把scale变慢一点
            scale = 1 + ((scale - 1) * DEF_SCALE_SPEED);
            //是放大还是缩小
            boolean isBigger = detector.getScaleFactor() > 1;
            //变化的个数（缩小或者放大），必须向上取整，不然当mShownMaxCount过小时容易取到0。
            int changeNum = (int) Math.ceil(drawCount * Math.abs(detector.getScaleFactor() - 1));

            //一半
            int helfChangeNum = (int) Math.ceil(changeNum / 2f);

            //缩放个数太少，直接return
            if (changeNum == 0 || helfChangeNum == 0) return true;
            //容错处理,获取最大最小值
            if (DEF_SCALE_MINNUM < 3) {
                DEF_SCALE_MINNUM = 3;
            }
            if (DEF_SCALE_MAXNUM > data.size()) {
                DEF_SCALE_MAXNUM = data.size();
            } else
                DEF_SCALE_MAXNUM = DEF_SCALE_MAXNUM_300;

            //变大了(拉伸了)，数量变少了
            int tempCount = isBigger ? drawCount - changeNum : drawCount + changeNum;

            //缩小大到最小了或者放大到很大了
            if (tempCount > DEF_SCALE_MAXNUM || tempCount < DEF_SCALE_MINNUM) return true;
            drawCount = tempCount;
            DEFUALT_WIDTH = DEFUALT_WIDTH * scale;

            postInvalidate();
            return super.onScale(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //指头数量
            if (mFingerPressedCount != 2) return true;
            return true;
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //按下的手指个数
        mFingerPressedCount = event.getPointerCount();

        if (gestureDetector != null)
            gestureDetector.onTouchEvent(event);
        if (mScaleGestureDetector != null)
            mScaleGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPressedX = event.getX();
                mPressedY = event.getY();
                Log.e("dingzuoq--KLineView", "mPressedX =" + mPressedX + " mPressedY =" + mPressedY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDrawLongPress) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (crossView != null && crossView.getVisibility() == View.VISIBLE) {
                        onCrossMove(event.getX(), event.getY());
                    }
                } else {
                    if (mFingerPressedCount == 2)
                        getParent().requestDisallowInterceptTouchEvent(true);
                    else {
                        Log.e("dingzuoq--KLineView", "ACTION_MOVE  event.getX() =" + event.getX() + " event.getY() =" + event.getY());
                        float moveX = Math.abs(event.getX() - mPressedX);
                        float moveY = Math.abs(event.getY() - mPressedY);
                        if (moveX > moveY)
                            getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                Log.e("dingzuoq--KLineView", "ACTION_UP-----------");
                hiddenLongPressView();
                break;
        }
        return true;
    }

    @Override
    protected void init() {
        if (data == null) return;
        drawCount = (int) (mWidth / DEFUALT_WIDTH);
        candleXDistance = drawCount * WIDTH_SCALE;
        if (data != null && data.size() > 0) {
            if (drawCount < data.size()) {
                getShowList(offset);
            } else {
                showList = new ArrayList<>();
                showList.addAll(data);
            }
        }
        if (showList == null) return;
        float[] low = new float[showList.size()];
        float[] high = new float[showList.size()];
        int i = 0;
        for (StickData d : showList) {
            low[i] = (float) d.getLow();
            high[i] = (float) d.getHigh();
            i++;
        }
        float[] maxAndMin = LineUtil.getMaxAndMin(low, high);
        yMax = maxAndMin[0];
        yMin = maxAndMin[1];
        yUnit = (float) (yMax - yMin) / mainH;
        xUnit = mWidth / drawCount;
    }

    @Override
    protected void drawGrid(Canvas canvas) {
//        GridUtils.drawGrid(canvas, mWidth, mainH);
        GridUtils.drawIndexGrid(canvas, indexStartY, mWidth, indexH);
    }

    @Override
    protected void drawCandles(Canvas canvas) {
        if (data == null || data.size() == 0) return;
        float x = 0;
        if (showList == null || showList.size() == 0) return;
        float maxY, minY, openY, closeY;
        //计算出页面能显示多少个
        for (int i = 0; i < showList.size(); i++) {
            if (drawCount < data.size()) {
                x = mWidth - (mWidth / drawCount * (showList.size() - i));
            } else {
                x = (mWidth / drawCount * i);
            }
            StickData stickData = showList.get(i);

            maxY = parseNumber(stickData.getHigh());
            minY = parseNumber(stickData.getLow());
            openY = parseNumber(stickData.getOpen());
            closeY = parseNumber(stickData.getClose());
            // 烛形图
            DrawUtils.drawCandle(canvas, maxY, minY, openY, closeY, x, parseNumber(showList.get(i).getHigh()), candleXDistance, mWidth);
            // K线:最高最低价
            if (yMax == (float) stickData.getHigh())// 取最大最小时，有做 (float) 强转，，所以此处比较时 也需要强转
                DrawUtils.drawKLineMinMaxPrice(canvas, CommonUtil.getMarketInfoAdapterString(yMax), mWidth, x + mWidth / candleXDistance / 2, maxY + 20);
            if (yMin == (float) stickData.getLow())
                DrawUtils.drawKLineMinMaxPrice(canvas, CommonUtil.getMarketInfoAdapterString(yMin), mWidth, x + mWidth / candleXDistance / 2, minY + 10);

        }
    }

    /**
     * SMA5 SMA10 SMA20
     *
     * @param canvas
     */
    @Override
    protected void drawLines(Canvas canvas) {
        if (data == null || data.size() == 0) return;
        float[] sma5 = new float[showList.size()];
        float[] sma10 = new float[showList.size()];
        float[] sma20 = new float[showList.size()];
        int size = showList.size();
        for (int i = 0; i < showList.size(); i++) {
            if (size > IndexParseUtil.START_SMA5)
                sma5[i] = (float) showList.get(i).getSma5();
            if (size > IndexParseUtil.START_SMA10)
                sma10[i] = (float) showList.get(i).getSma10();
            if (size > IndexParseUtil.START_SMA20)
                sma20[i] = (float) showList.get(i).getSma20();
        }
        DrawUtils.drawLineWithXOffset(canvas, sma5, DEFUALT_WIDTH, mainH, ColorUtil.COLOR_SMA5, (float) yMax, (float) yMin, DEFUALT_WIDTH / 2);
        DrawUtils.drawLineWithXOffset(canvas, sma10, DEFUALT_WIDTH, mainH, ColorUtil.COLOR_SMA10, (float) yMax, (float) yMin, DEFUALT_WIDTH / 2);
        DrawUtils.drawLineWithXOffset(canvas, sma20, DEFUALT_WIDTH, mainH, ColorUtil.COLOR_SMA20, (float) yMax, (float) yMin, DEFUALT_WIDTH / 2);
    }

    @Override
    protected void drawText(Canvas canvas) {
        if (data == null || data.size() == 0) return;
        //4，画X轴时间
        if (showList.size() <= drawCount) {
            DrawUtils.drawKLineXTime(canvas, showList.get(0).getTime(), showList.get(showList.size() - 1).getTime(), mWidth, mainH + DrawUtils.FENSHI_TIME_SIZE);
        } else {
            DrawUtils.drawKLineXTime(canvas, showList.get(0).getTime(), null, mWidth, mainH + DrawUtils.FENSHI_TIME_SIZE);
        }
        //5，画Y轴价格
//        DrawUtils.drawKLineYPrice(canvas, yMax, yMin, mainH);
    }

    @Override
    protected void drawVOL(Canvas canvas) {
        if (data == null || data.size() == 0) return;
        double max = 0;
        for (StickData data : showList) {
            max = data.getCount() > max ? data.getCount() : max;
        }
        //如果量全为0，则不画
        if (max == 0) return;
        //2,画量线，多条竖直线
        DrawUtils.drawVOLRects(canvas, xUnit, indexStartY, indexH, max, showList);
    }

    /**
     * 把传入的参数计算成坐标，直接展示到界面上
     *
     * @param input
     * @return 返回里面的StickData的最高价最低价，都是可以直接显示在坐标上的
     */
    private float parseNumber(double input) {
        return mainH - (float) ((input - yMin) / yUnit);
    }

    public void setDataAndInvalidate(ArrayList<StickData> data) {
        this.data = data;
        parseData();
        postInvalidate();
    }

    /**
     * 获取页面一页可展示的数据
     */
    private void getShowList(int offset) {
        if (offset != 0 && data.size() - drawCount - offset < 0) {
            offset = data.size() - drawCount;
        }
        showList = new ArrayList<>();
        showList.addAll(data.subList(data.size() - drawCount - offset, data.size() - offset));
    }

    /**
     * 设置K线类型
     */
    public void setType(int type) {
        lineType = type;
    }

    /**
     * 计算各指标
     */
    private void parseData() {
        offset = 0;
        //根据当前显示的指标类型，优先计算指标
        IndexParseUtil.initSma(this.data);

    }

    @Override
    public void onCrossMove(float x, float y) {
        super.onCrossMove(x, y);
        if (crossView == null || showList == null) return;
        int position = (int) Math.rint(new Double(x) / new Double(DEFUALT_WIDTH));
        if (position < showList.size()) {
            StickData data = showList.get(position);
            float xIn = (mWidth / drawCount * position) + (mWidth / candleXDistance / 2);
            CrossBean bean = new CrossBean(xIn, getY(data.getClose()));
            bean.price = data.getClose() + "";
            //注意，这个值用来区分，是否画出均线的小圆圈
            bean.y2 = -1;
            bean.timeYMD = data.getTime();
            crossView.drawLine(bean);
            if (crossView.getVisibility() == GONE)
                crossView.setVisibility(VISIBLE);
            msgText.setVisibility(VISIBLE);
            msgText.setText(Html.fromHtml(getCurPriceInfo(data)));
        }
    }

    @Override
    public void onDismiss() {
        msgText.setVisibility(INVISIBLE);
    }

    //获取价格对应的Y轴
    private float getY(double price) {
        return mainH - (new Float(price) - (float) yMin) / yUnit;
    }

    /**
     * 价格信息
     *
     * @param entity
     * @return
     */
    private String getCurPriceInfo(StickData entity) {
        StringBuffer sb = new StringBuffer();
//        sb.append("开:" + NumberUtil.beautifulDouble(entity.getOpen(), scale));
//        sb.append("\u3000收:" + NumberUtil.beautifulDouble(entity.getClose(), scale));
//        sb.append("\u3000高:" + NumberUtil.beautifulDouble(entity.getHigh(), scale));
//        sb.append("\u3000低:" + NumberUtil.beautifulDouble(entity.getLow(), scale));
//        sb.append("\u3000量" + entity.getCount());
        sb.append("开:" + CommonUtil.getMarketInfoAdapterString(entity.getOpen()));
        sb.append("\u3000收:" + CommonUtil.getMarketInfoAdapterString(entity.getClose()));
        sb.append("\u3000高:" + CommonUtil.getMarketInfoAdapterString(entity.getHigh()));
        sb.append("\u3000低:" + CommonUtil.getMarketInfoAdapterString(entity.getLow()));
        sb.append("<br>量:" + CommonUtil.getMarketInfoAdapterString(entity.getCount()));
        sb.append("\u3000" + TimeUtils.getDateToStringMs(entity.getTimeLone(), timeFormat));

        return sb.toString();
//        return ColorUtil.getCurPriceInfo(data);
    }

}
