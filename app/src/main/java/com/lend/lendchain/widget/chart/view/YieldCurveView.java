package com.lend.lendchain.widget.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.TimeUtils;
import com.lend.lendchain.widget.chart.bean.CrossBean;
import com.lend.lendchain.widget.chart.bean.YieldCurve;
import com.lend.lendchain.widget.chart.utils.ColorUtil;
import com.lend.lendchain.widget.chart.utils.DrawUtils;
import com.lend.lendchain.widget.chart.utils.NumberUtil;
import com.yangfan.utils.CommonUtils;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.lend.lendchain.widget.chart.utils.DrawUtils.FENSHI_TEXT_SIZE;

/**
 * 收益曲线
 */
public class YieldCurveView extends ChartView {

    //分时数据的所有点
    private ArrayList<YieldCurve> minutes;
    //展示的数据
    private ArrayList<YieldCurve> showList;

    //所有价格
    private float[] price;

    protected static int DEF_SCALE_MINNUM = 10;
    //缩放最大值，该值最大理论上可为数据集合的大小
    protected static final int DEF_SCALE_MAXNUM_300 = 20;
    protected static int DEF_SCALE_MAXNUM = DEF_SCALE_MAXNUM_300;

    public YieldCurveView(Context context) {
        this(context, null);
        initUi();
    }

    public YieldCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    private void initUi() {
        DEFUALT_WIDTH = CommonUtils.getScreenWidth(getContext()) / 15;
        setCustomMainScale(1);
    }

    @Override
    protected boolean onViewScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //distanceX 往左滑 正数，，往右滑动 负数
        Log.e("dingzuo", "" + distanceX);
        if (minutes != null && drawCount < minutes.size() && Math.abs(distanceX) > DEF_PULL_LENGTH) {
            int temp = offset + (int) (0 - distanceX / DEFUALT_WIDTH);
            if (temp < 0 || temp + drawCount > minutes.size()) {

            } else {
                offset = temp;
                moveKView(distanceX);
            }
            return true;
        }
        return false;
    }

    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (minutes == null) return super.onScale(detector);
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
            if (DEF_SCALE_MAXNUM >= minutes.size()) {
                DEF_SCALE_MAXNUM = minutes.size();
            } else
                DEF_SCALE_MAXNUM = DEF_SCALE_MAXNUM_300;
            //变大了(拉伸了)，数量变少了
            int tempCount = isBigger ? drawCount - changeNum : drawCount + changeNum;

            Log.e("dingzuo", "size = " + minutes.size() + "  DEF_SCALE_MAXNUM = " + DEF_SCALE_MAXNUM + "   tempCount = " + tempCount);
            //缩小大到最小了或者放大到很大了
            if (tempCount > DEF_SCALE_MAXNUM || tempCount < DEF_SCALE_MINNUM) return true;
            drawCount = tempCount;
            DEFUALT_WIDTH = DEFUALT_WIDTH * scale;

            //计算新的开始位置。这个地方比较难以理解:拉伸了起始点变大，并且是拉伸数量的一半，结束点变小，也是原来的一半。
            // 收缩，相反。可以自己画一个图看看
            mBeginIndex = isBigger ? mBeginIndex + helfChangeNum : mBeginIndex - helfChangeNum;
            if (mBeginIndex < 0) {
                mBeginIndex = 0;
            } else if ((mBeginIndex + drawCount) > minutes.size()) {
                mBeginIndex = minutes.size() - drawCount;
            }

            mEndIndex = mBeginIndex + drawCount;
            //只要找好起始点和结束点就可以交给处理重绘的方法就好啦~
            seekAndCalculateCellData();
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
                Log.e("dingzuoq--FenshiView", "mPressedX =" + mPressedX + " mPressedY =" + mPressedY);
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
                        Log.e("dingzuoq--FenshiView", "ACTION_MOVE  event.getX() =" + event.getX() + " event.getY() =" + event.getY());
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
                Log.e("dingzuoq--FenshiView", "ACTION_UP-----------");
                hiddenLongPressView();
                break;
        }
        return true;
    }

    /**
     * 移动K线图计算移动的单位和重新计算起始位置和结束位置
     *
     * @param moveLen
     */
    protected void moveKView(float moveLen) {
        //移动之前将右侧的内间距值为0
        mInnerRightBlankPadding = 0;

        mPullRight = moveLen < 0;
        int moveCount = (int) Math.ceil(Math.abs(moveLen) / xUnit);
        if (mPullRight) {
            int len = mBeginIndex - moveCount;
            if (len < DEF_MINLEN_LOADMORE) {
                //加载更多
                if (mTimeSharingListener != null && mCanLoadMore) {
                    loadMoreIng();
                    mTimeSharingListener.needLoadMore();
                }
            }
            if (len < 0) {
                mBeginIndex = 0;
                mPullType = PullType.PULL_LEFT_STOP;
                Log.e("dingzuo", "滑动到最左边");
            } else {
                mBeginIndex = len;
                mPullType = PullType.PULL_LEFT;
                Log.e("dingzuo", "左->右滑动");
            }
        } else {
            int len = mBeginIndex + moveCount;
            if (len + drawCount > minutes.size()) {
                mBeginIndex = minutes.size() - drawCount;
                //滑动到最右边
                mPullType = PullType.PULL_RIGHT_STOP;
                Log.e("dingzuo", "滑动到最右边");
                //重置到之前的状态
                mInnerRightBlankPadding = DEF_INNER_RIGHT_BLANK_PADDING;
            } else {
                mBeginIndex = len;
                mPullType = PullType.PULL_RIGHT;
                Log.e("dingzuo", "右->左滑动");
            }
        }
        mEndIndex = mBeginIndex + drawCount;
        //开始位置和结束位置确认好，就可以重绘啦~
        seekAndCalculateCellData();
    }


    @Override
    protected void init() {
        Log.e("dingzuo", "----init()-------");
        if (minutes == null) return;
        drawCount = (int) (mWidth / DEFUALT_WIDTH);
        candleXDistance = drawCount * WIDTH_SCALE;
        if (minutes != null && minutes.size() > 0) {
            if (drawCount < minutes.size()) {
                getShowList(offset);
            } else {
                showList = new ArrayList<>();
                showList.addAll(minutes);
            }
        }
        if (showList == null) return;

        //计算最大最小值
        boolean first = true;
        for (YieldCurve c : minutes) {
            if (first) {
                first = false;
                yMax = c.blance;
                yMin = c.blance;
            }
            yMax = c.blance > yMax ? c.blance : yMax;
            yMin = c.blance < yMin ? c.blance : yMin;
        }
        Log.e(TAG, "yMax1 =" + yMax + "--yMin1=" + yMin);

        Log.e(TAG, "yMaxOut =" + yMaxOut + "--yMinOut=" + yMinOut);
        if (setOutYMinMax) {
            yMax = yMax > yMaxOut ? yMax : yMaxOut;
            yMin = yMin < yMinOut ? yMin : yMinOut;
        }
        yMax = yMax * 1.05;
        yMin = yMin * 0.95;
        Log.e(TAG, "yMax2 =" + yMax + "--yMin2=" + yMin);

        xUnit = mWidth / drawCount;
    }

    /**
     * 数据设置入口
     *
     * @param list
     */
    public void setDataAndInvalidate(ArrayList<YieldCurve> list) {
        minutes = list;
        offset = 0;
        seekAndCalculateCellData();
        seekBeginAndEndByNewer();
    }

    /**
     * 加载更多数据
     *
     * @param list
     */
    public void loadMoreTimeSharingData(ArrayList<YieldCurve> list) {
        if (list == null || list.isEmpty()) {
//            Toast.makeText(mContext, "数据异常", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "loadMoreTimeSharingData: 数据异常");
            return;
        }
        minutes.addAll(0, list);

        //到这里就可以判断，加载更对成功了
        loadMoreSuccess();

        //特别特别注意，加载更多之后，不应该更新起始位置和结束位置，
        //因为可能在加载的过程中，原来的意图是在最左边，但是加载完毕后，又不在最左边了。
        // 因此，只要保持原来的起始位置和结束位置即可。【原来：指的是视觉上的原来】
        int addSize = list.size();
        mBeginIndex = mBeginIndex + addSize;
        if (mBeginIndex + drawCount > minutes.size()) {
            mBeginIndex = minutes.size() - drawCount;
        }
        mEndIndex = mBeginIndex + drawCount;
        //重新测量一下,这里不能重新测量。因为重新测量的逻辑是寻找最新的点。
        seekAndCalculateCellData();
    }

    /**
     * 获取页面一页可展示的数据
     */
    private void getShowList(int offset) {
        if (offset != 0 && minutes.size() - drawCount - offset < 0) {
            offset = minutes.size() - drawCount;
        }
        showList = new ArrayList<>();
        showList.addAll(minutes.subList(minutes.size() - drawCount - offset, minutes.size() - offset));
    }


    /**
     * 计算各指标
     */
    private void seekAndCalculateCellData() {
        if (minutes.isEmpty()) return;

        //根据当前显示的指标类型，优先计算指标
//        IndexParseUtil.initSma(this.data);
        //重绘
        postInvalidate();
        resetIndexText();
    }

    /**
     * 获取最新数据时（包括第一次进来）获取可见数据的开始位置和结束位置。来最新数据或者刚加载的时候，计算开始位置和结束位置。
     * 特别注意，最新的数据在最后面，所以数据范围应该是[(size-mShownMaxCount)~size)
     */
    protected void seekBeginAndEndByNewer() {
        if (minutes.isEmpty()) return;
        int size = minutes.size();
        if (size >= drawCount) {
            mBeginIndex = size - drawCount;
            mEndIndex = mBeginIndex + drawCount;
        } else {
            mBeginIndex = 0;
            mEndIndex = mBeginIndex + minutes.size();
        }
    }

    @Override
    protected void drawGrid(Canvas canvas) {
        if (minutes == null || minutes.size() == 0) return;
        //1,画网格
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));
        p.setColor(ColorUtil.COLOR_YIELD_CURVE_DASHED);
        p.setAntiAlias(true);

        for (int i = 1; i < 4; i++) {
//        //横虚线
            float y = mainH * i / 4;
            canvas.drawLine(0, y, mWidth, y, p);
        }
    }

    /**
     * Y轴价格
     *
     * @param canvas
     * @param max
     * @param min
     * @param height
     */
    public void drawYPrice(Canvas canvas, double max, double min, float height) {
        double diff = max - min;
        Paint p = new Paint();
        p.setColor(ColorUtil.COLOR_YIELD_CURVE_PRICE_TEXT);
        p.setTextSize(FENSHI_TEXT_SIZE);
        p.setTextAlign(Paint.Align.RIGHT);

        float x = mWidth - DisplayUtil.dp2px(getContext(), 17);
        float padding5 = DisplayUtil.dp2px(getContext(), 5);
        for (int i = 1; i < 4; i++) {
//        //横虚线
            float y = mainH * i / 4 - padding5;
            String string = NumberUtil.getMoneyString(min + diff * (1 - i / 4d));
            canvas.drawText(string, x, y, p);
        }
    }

    @Override
    protected void drawLines(Canvas canvas) {
        if (minutes == null) return;
        drawPriceLine(canvas);
    }

    @Override
    protected void drawText(Canvas canvas) {
        if (minutes == null || minutes.size() == 0) return;
        // 1. 右侧价格线
        drawYPrice(canvas, yMax, yMin, mainH);
        // 2. 底部日期
        String showTime;
        Paint p = new Paint();
        p.setColor(ColorUtil.COLOR_YIELD_CURVE_DATE_TEXT);
        p.setTextSize(FENSHI_TEXT_SIZE);
        p.setAntiAlias(true);
        for (int i = 0; i < showList.size(); i++) {
            showTime = TimeUtils.getDateToStringMs(showList.get(i).date, "dd");
            float xIn = xUnit * i + (mWidth / candleXDistance / 2) - p.measureText(showTime) / 2;
            canvas.drawText(showTime, xIn, mainH + DrawUtils.FENSHI_TIME_SIZE, p);
        }
    }

    @Override
    protected void drawVOL(Canvas canvas) {

    }

    /**
     * 价格线
     *
     * @param canvas
     */
    private void drawPriceLine(Canvas canvas) {
        if (showList == null || showList.size() == 0) return;
        if (showList.size() != 1) {
            price = new float[showList.size()];
            for (int i = 0; i < showList.size(); i++) {
                price[i] = (float) showList.get(i).blance;
            }
            DrawUtils.drawPriceShader(canvas, price, xUnit, mainH, (float) yMax, (float) yMin);

            DrawUtils.drawLines(canvas, price, xUnit, mainH, ColorUtil.COLOR_YIELD_CURVE_LINE, (float) yMax, (float) yMin, false, 0, 0, DisplayUtil.dp2px(getContext(), 2));
        } else {
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(ColorUtil.COLOR_YIELD_CURVE_LINE);
            p.setStrokeWidth(1f);
            p.setStyle(Paint.Style.FILL);
            float xIn = xUnit * 0 + (mWidth / candleXDistance / 2);
            float cy = (float) getY(showList.get(0).blance);
            canvas.drawCircle(xIn, cy, 10, p);
        }

    }

    /**
     * 当十字线移动到某点时，回调到此处，用此处的数据判断是否显示十字线
     *
     * @param x x轴坐标
     * @param y y轴坐标
     */
    @Override
    public void onCrossMove(float x, float y) {
        super.onCrossMove(x, y);
        if (showList == null) return;
        int position = (int) Math.rint(new Double(x) / new Double(DEFUALT_WIDTH));
        if (position < showList.size()) {
            YieldCurve cMinute = showList.get(position);
            float xIn = (mWidth / drawCount * position) + (mWidth / candleXDistance / 2);
            float cy = (float) getY(cMinute.blance);
            CrossBean bean = new CrossBean(xIn, cy);
            bean.price = cMinute.blance + "";
            bean.time = cMinute.date;
            bean.timeYMD = TimeUtils.getDateToStringMs(cMinute.date, timeFormat);
            if (crossView != null) {
                crossView.drawLine(bean);
                crossView.setDrawTimeText(false);
                if (crossView.getVisibility() == GONE)
                    crossView.setVisibility(VISIBLE);
            }
            if (msgText != null) {
                msgText.setVisibility(VISIBLE);
                msgText.setText(Html.fromHtml(getCurPriceInfo(cMinute)));
            }

        }
    }

    @Override
    public void onDismiss() {
        if (msgText != null)
            msgText.setVisibility(INVISIBLE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (crossView != null)
            crossView.setVisibility(GONE);
    }

    //获取价格对应的Y轴
    private double getY(double price) {
        if (price == yMax) return 0;
        if (price == yMin) return mainH;
        return mainH - (new Float(price) - yMin) / ((yMax -yMin) / mainH);
    }

    /**
     * 余额信息
     *
     * @param entity
     * @return
     */
    private String getCurPriceInfo(YieldCurve entity) {
        StringBuffer sb = new StringBuffer();
        sb.append("余额:" + CommonUtil.getMarketInfoAdapterString(entity.blance));
        sb.append("\u3000\u3000" + TimeUtils.getDateToStringMs(entity.date, timeFormat));
        return sb.toString();
    }
}
