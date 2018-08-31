package com.lvfq.pickerview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.lvfq.pickerview.R;
import com.lvfq.pickerview.adapter.WheelAdapter;

/**
 *
 */
public class BlankView extends View {


    Context context;

    Paint paintOuterText;
    Paint paintCenterText;
    Paint paintIndicator;

    WheelAdapter adapter;

    private String label;//附加单位
    int textSize;//选项的文字大小
    int maxTextHeight;
    float itemHeight;//每行高度

    int textColorOut;
    int textColorCenter;
    int dividerColor;

    // 条目间距倍数
    float lineSpacingMultiplier = 2F;

    // 第一条线Y坐标值
    float firstLineY;
    //第二条线Y坐标
    float secondLineY;
    //中间Y坐标
    float centerY;

    //滚动总高度y值
    int totalScrollY;
    //初始化默认选中第几个
    int initPosition;
    //滚动偏移值,用于记录滚动了多少个item
    int change;

    // 显示几个条目
    int itemsVisible = 11;//  实际 只显示11-2 = 9 个

    int measuredHeight;
    int measuredWidth;

    // 半圆周长
    int halfCircumference;
    // 半径
    int radius;

    int widthMeasureSpec;

    private int mGravity = Gravity.CENTER;
    private static final float CENTERCONTENTOFFSET = 6;//中间文字文字居中需要此偏移值

    public BlankView(Context context) {
        this(context, null);
    }

    public BlankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textColorOut = getResources().getColor(R.color.pickerview_wheelview_textcolor_out);
        textColorCenter = getResources().getColor(R.color.pickerview_wheelview_textcolor_center);
        dividerColor = getResources().getColor(R.color.pickerview_wheelview_textcolor_divider);
        textSize = getResources().getDimensionPixelSize(R.dimen.pickerview_textsize);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.wheelview, 0, 0);
            mGravity = a.getInt(R.styleable.wheelview_gravity, Gravity.CENTER);
            textColorOut = a.getColor(R.styleable.wheelview_textColorOut, textColorOut);
            textColorCenter = a.getColor(R.styleable.wheelview_textColorCenter, textColorCenter);
            dividerColor = a.getColor(R.styleable.wheelview_dividerColor, dividerColor);
            textSize = a.getDimensionPixelOffset(R.styleable.wheelview_textSize, textSize);
        }
        initLoopView(context);
    }

    private void initLoopView(Context context) {
        this.context = context;

        totalScrollY = 0;
        initPosition = -1;

        initPaints();

    }

    private void initPaints() {
        paintOuterText = new Paint();
        paintOuterText.setColor(textColorOut);
        paintOuterText.setAntiAlias(true);
        paintOuterText.setTypeface(Typeface.MONOSPACE);
        paintOuterText.setTextSize(textSize);

        paintCenterText = new Paint();
        paintCenterText.setColor(textColorCenter);
        paintCenterText.setAntiAlias(true);
//        paintCenterText.setTextScaleX(1.1F);
        paintCenterText.setTypeface(Typeface.MONOSPACE);
        paintCenterText.setTextSize(textSize);

        paintIndicator = new Paint();
        paintIndicator.setColor(dividerColor);
        paintIndicator.setAntiAlias(true);
        paintIndicator.setStrokeWidth(1);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void remeasure() {
//        if (adapter == null) {
//            return;
//        }

        measureTextWidthHeight();

        //最大Text的高度乘间距倍数得到 可见文字实际的总高度，半圆的周长
        halfCircumference = (int) (itemHeight * (itemsVisible - 1));
        //整个圆的周长除以PI得到直径，这个直径用作控件的总高度
        measuredHeight = (int) ((halfCircumference * 2) / Math.PI);
        //求出半径
        radius = (int) (halfCircumference / Math.PI);
        //控件宽度，这里支持weight
        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        //计算两条横线和控件中间点的Y位置
        firstLineY = (measuredHeight - itemHeight) / 2.0F;
        secondLineY = (measuredHeight + itemHeight) / 2.0F;
        centerY = (measuredHeight + maxTextHeight) / 2.0F - CENTERCONTENTOFFSET;

    }

    /**
     * 计算最大len的Text的宽高度
     */
    private void measureTextWidthHeight() {
        Rect rect = new Rect();
        paintCenterText.getTextBounds("\u661F\u671F", 0, 2, rect); // 星期
        int textHeight = rect.height();
        if (textHeight > maxTextHeight) {
            maxTextHeight = textHeight;
        }
        itemHeight = lineSpacingMultiplier * maxTextHeight;
    }

    public final void setTextSize(float size) {
        if (size > 0.0F) {
            textSize = (int) (context.getResources().getDisplayMetrics().density * size);
            paintOuterText.setTextSize(textSize);
            paintCenterText.setTextSize(textSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if (adapter == null) {
//            return;
//        }

         //中间两条横线
        canvas.drawLine(0.0F, firstLineY, measuredWidth, firstLineY, paintIndicator);
        canvas.drawLine(0.0F, secondLineY, measuredWidth, secondLineY, paintIndicator);
        //单位的Label
        if (label != null) {
            int drawRightContentStart = measuredWidth - getTextWidth(paintCenterText, label);
            //靠右并留出空隙
            canvas.drawText(label, drawRightContentStart - CENTERCONTENTOFFSET, centerY, paintCenterText);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        remeasure();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    /**
     * 附加在右边的单位字符串
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        if (lineSpacingMultiplier > 0)
            this.lineSpacingMultiplier = lineSpacingMultiplier;
    }
}