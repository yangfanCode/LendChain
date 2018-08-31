package com.lend.lendchain.widget.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.lend.lendchain.bean.HomeMarketKLine;
import com.lend.lendchain.widget.chart.utils.ColorUtil;
import com.lend.lendchain.widget.chart.utils.DrawUtils;
import com.lend.lendchain.widget.chart.utils.GridUtils;
import com.lend.lendchain.widget.chart.utils.LineUtil;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * 分时图
 */
public class FenshiView extends ChartView {

    //分时数据的所有点
    private ArrayList<HomeMarketKLine> minutes;
    //展示的数据
    private ArrayList<HomeMarketKLine> showList;

    //所有价格
    private float[] price;
    //所有均线数据
    private float[] average;
    //分时线昨收
    private double yd;

    public FenshiView(Context context) {
        this(context, null);
    }

    public FenshiView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
//        drawCount = (int) (mWidth / DEFUALT_WIDTH);
        candleXDistance = drawCount * WIDTH_SCALE;
        if (minutes != null && minutes.size() > 0) {
            if (drawCount < minutes.size()) {
                getShowList(offset);
            } else {
                showList = new ArrayList<>();
                showList.addAll(minutes);
            }
            if (showList != null && showList.size() > 0)
                yd = showList.get(0).price;
        }
        if (showList == null) return;

        //计算最大最小值

        boolean first = true;
        for (HomeMarketKLine c : minutes) {
            if (first) {
                first = false;
                yMax = c.price;
                yMin = c.price;
            }
            yMax = c.price > yMax ? c.price : yMax;
//            yMax = c.getAverage() > yMax ? c.getAverage() : yMax;
            if (c.price != 0)
                yMin = c.price < yMin ? c.price : yMin;
//            if (c.getAverage() != 0)
//                yMin = c.getAverage() < yMin ? c.getAverage() : yMin;
        }
        Log.e(TAG, "yMax1 =" + yMax + "--yMin1=" + yMin);

        Log.e(TAG, "yMaxOut =" + yMaxOut + "--yMinOut=" + yMinOut);
        if (setOutYMinMax) {
            yMax = yMax > yMaxOut ? yMax : yMaxOut;
            yMin = yMin < yMinOut ? yMin : yMinOut;
        }
        yMax = yMax * 1.02;
        yMin = yMin * 0.98;
        Log.e(TAG, "yMax2 =" + yMax + "--yMin2=" + yMin);

        xUnit = mWidth / drawCount;
    }


    @Override
    protected void drawGrid(Canvas canvas) {
        //1,画网格
//        if(data != null && data.getParam() != null && LineUtil.isIrregular(data.getParam().getDuration())) {
//            //如果是不规则网格画不规则网格
//            GridUtils.drawIrregularGrid(canvas, mWidth, mainH, data.getParam().getDuration());
//            GridUtils.drawIrregularIndexGrid(canvas, indexStartY, mWidth, indexH, data.getParam().getDuration());
//        } else {
//            GridUtils.drawGrid(canvas, mWidth, mainH);
        GridUtils.drawIndexGrid(canvas, indexStartY, mWidth, indexH);
//        }

    }

    /**
     * 数据设置入口
     *
     * @param list
     */
    public void setDataAndInvalidate(ArrayList<HomeMarketKLine> list) {
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
    public void loadMoreTimeSharingData(ArrayList<HomeMarketKLine> list) {
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
     * 实时推送过来的数据，实时更新
     *
     * @param HomeMarketKLine
     */
    public void pushingTimeSharingData(HomeMarketKLine HomeMarketKLine) {
        if (HomeMarketKLine == null) {
//            Toast.makeText(mContext, "数据异常", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "pushingTimeSharingData: 数据异常");
            return;
        }
        minutes.add(HomeMarketKLine);
        //如果是在左右移动，则不去实时更新K线图，但是要把数据加进去
        if (mPullType == PullType.PULL_RIGHT_STOP) {
            //Log.e(TAG, "pushingTimeSharingData: 处理实时更新操作...");
            seekAndCalculateCellData();
            seekBeginAndEndByNewer();
        }
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
    protected void drawLines(Canvas canvas) {
        if (minutes == null) return;
        drawPriceLine(canvas);
//        drawAverageLine(canvas);
    }


    @Override
    protected void drawText(Canvas canvas) {
//        if (minutes == null || minutes.size() == 0) return;
////        DrawUtils.drawYPercentAndPrice(canvas, yMax, yMin, yd,mWidth, mainH);
////        DrawUtils.drawXTime(canvas, data.getParam().getDuration(), data.getParam().getUntil(),mWidth, mainH);
////        if (showList.size() <= drawCount) {
////            DrawUtils.drawKLineXTime(canvas, showList.get(0).getTimeStr(), showList.get(showList.size() - 1).getTimeStr(), mWidth, mainH);
////        } else {
////            DrawUtils.drawKLineXTime(canvas, showList.get(0).getTimeStr(), null, mWidth, mainH);
////        }
//
//        //细节，让中间虚线对应的文字居中
//        float txtWidth = mXYTxtPaint.measureText("00:00");
//
//        int padding20 = DisplayUtil.dp2px(getContext(), 20);
//        //单位间距，注意这里需要加上右边内边距
//        double perXWith = (mWidth - padding20 * 2 - txtWidth * 5) / (xTimeCount - 1);
//
//        String showTime;
//        float finalHalfTxtWidth;
//        double x;
//
//        for (int i = 0; i < xTimeCount; i++) {
//            x = padding20 + perXWith * i + i * txtWidth;
//            int position = (int) Math.rint(new Double(x) / new Double(DEFUALT_WIDTH));
//            if (position < showList.size()) {
//                HomeMarketKLine HomeMarketKLine = showList.get(position);
//                showTime = TimeUtils.getDateToString3(HomeMarketKLine.getTime(), "HH:mm");
//                canvas.drawText(showTime, (float) (x), mainH + DrawUtils.FENSHI_TIME_SIZE, mXYTxtPaint);
//            }
//        }
    }

    @Override
    protected void drawVOL(Canvas canvas) {
//        if (minutes == null || minutes.size() == 0) return;
////        double max = 0;
////        for (HomeMarketKLine minute : showList) {
////            max = minute.getCount() > max ? minute.getCount() : max;
////        }
////        //如果量全为0，则不画
////        if (max != 0) {
////            //2,画量线，多条竖直线
////            DrawUtils.drawVOLRects(canvas, xUnit, indexStartY, indexH, max, (float) yd, showList);
////        }
    }

    /**
     * 价格线
     *
     * @param canvas
     */
    private void drawPriceLine(Canvas canvas) {
        price = new float[showList.size()];
        for (int i = 0; i < showList.size(); i++) {
            price[i] = (float) showList.get(i).price;
        }
        //乘以1.001是为了让上下分别空一点出来
        double[] maxAndMin = LineUtil.getMaxAndMinByYd(yMax, yMin, yd);
        DrawUtils.drawPriceShader(canvas, price, xUnit, mainH, (float) maxAndMin[0], (float) maxAndMin[1]);

        DrawUtils.drawLines(canvas, price, xUnit, mainH, ColorUtil.COLOR_PRICE_LINE, (float) maxAndMin[0], (float) maxAndMin[1], false);
    }


//    private void drawAverageLine(Canvas canvas) {
//        average = new float[showList.size()];
//        for (int i = 0; i < showList.size(); i++) {
//            average[i] = (float) showList.get(i).getAverage();
//        }
//        float[] maxAndMin1 = LineUtil.getMaxAndMin(average);
////        //如果均线值全为0.01则不画改线，否则会影响价格线展示
////        if (maxAndMin1[0] == 0.01 && maxAndMin1[1] == 0.01)
////            return;
//        //乘以1.001是为了让上下分别空一点出来
//        double[] maxAndMin = LineUtil.getMaxAndMinByYd(yMax, yMin, yd);
////        DrawUtils.drawPriceShader(canvas, price, xUnit, mainH, (float) maxAndMin[0], (float) maxAndMin[1]);
//        DrawUtils.drawLines(canvas, average, xUnit, mainH, ColorUtil.COLOR_SMA_LINE, (float) maxAndMin[0], (float) maxAndMin[1], false);
//    }

//    /**
//     * 当十字线移动到某点时，回调到此处，用此处的数据判断是否显示十字线
//     *
//     * @param x x轴坐标
//     * @param y y轴坐标
//     */
//    @Override
//    public void onCrossMove(float x, float y) {
//        super.onCrossMove(x, y);
//        if (crossView == null || showList == null) return;
//        int position = (int) Math.rint(new Double(x) / new Double(DEFUALT_WIDTH));
//        if (position < showList.size()) {
//            HomeMarketKLine HomeMarketKLine = showList.get(position);
//            float xIn = (mWidth / drawCount * position) + (mWidth / candleXDistance / 2);
//            float cy = (float) getY(HomeMarketKLine.getPrice());
//            CrossBean bean = new CrossBean(xIn, cy);
//            bean.y2 = (float) getY(HomeMarketKLine.getAverage());
//            bean.price = HomeMarketKLine.getPrice() + "";
//            bean.time = HomeMarketKLine.getTime();
////            setIndexTextAndColor(position, HomeMarketKLine, bean);
//            crossView.drawLine(bean);
//            if (crossView.getVisibility() == GONE)
//                crossView.setVisibility(VISIBLE);
//            //TODO 此处把该点的数据写到界面上
//            msgText.setVisibility(VISIBLE);
//            msgText.setText(Html.fromHtml(getCurPriceInfo(HomeMarketKLine)));
//        }
//    }

    @Override
    public void onDismiss() {
        msgText.setVisibility(INVISIBLE);
    }

//    /**
//     * 计算指标左上角应该显示的文字
//     */
//    private void setIndexTextAndColor(int position, HomeMarketKLine HomeMarketKLine, CrossBean bean) {
//        switch (indexType) {
//            case INDEX_VOL:
//                bean.indexText = new String[]{"VOL:" + HomeMarketKLine.getCount()};
//                bean.indexColor = new int[]{HomeMarketKLine.getPrice() > yd ? ColorUtil.INCREASING_COLOR : ColorUtil.DECREASING_COLOR};
//                break;
//        }
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (crossView != null)
            crossView.setVisibility(GONE);
    }

    //获取价格对应的Y轴
    private double getY(double price) {
        double[] maxAndMin = LineUtil.getMaxAndMinByYd(yMax, yMin, yd);
        if (price == maxAndMin[0]) return 0;
        if (price == maxAndMin[1]) return mainH;
        return mainH - (new Float(price) - maxAndMin[1]) / ((maxAndMin[0] - maxAndMin[1]) / mainH);
    }

//    /**
//     * 价格信息
//     *
//     * @param entity
//     * @return
//     */
//    private String getCurPriceInfo(HomeMarketKLine entity) {
//        StringBuffer sb = new StringBuffer();
////        sb.append("价格:" + NumberUtil.beautifulDouble(entity.getPrice(), scale));
//        sb.append("价格:" + CommonUtil.getMarketInfoAdapterString(entity.getPrice()));
//        sb.append("\u3000\u3000成交:" + CommonUtil.getMarketInfoAdapterString(entity.getCount()));
//        sb.append("\u3000\u3000" + TimeUtils.getDateToString3(entity.time, timeFormat));
//        return sb.toString();
////        return ColorUtil.getCurPriceInfo(entity, yd);
//    }
}
