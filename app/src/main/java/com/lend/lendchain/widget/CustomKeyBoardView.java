package com.lend.lendchain.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.lend.lendchain.R;
import com.lend.lendchain.utils.DisplayUtil;

import java.lang.reflect.Field;
import java.util.List;

public class CustomKeyBoardView extends KeyboardView {
    private Context mContext;
    private Keyboard mKeyBoard;

    public CustomKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public CustomKeyBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    /**
     * 重新画一些按键
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mKeyBoard = this.getKeyboard();
        List<Key> keys = null;
        if (mKeyBoard != null) {
            keys = mKeyBoard.getKeys();
        }

        if (keys != null) {
            for (Key key : keys) {
                // 数字键盘的处理
                if (key.codes[0] == -4) {
                    //重画确定键
                    drawKeyBackground(R.drawable.bg_keyboardview_yes, canvas, key);
                    drawText(canvas, key);
                    break;
                }
            }
        }
    }

    private void drawKeyBackground(int drawableId, Canvas canvas, Key key) {
        Drawable npd = mContext.getResources().getDrawable(
                drawableId);
        int[] drawableState = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            npd.setState(drawableState);
        }
        npd.setBounds(key.x, key.y, key.x + key.width, key.y
                + key.height);
        npd.draw(canvas);
    }

    /**
     * 设置确定文字 默认确认
     * @param text
     */
    public void setKeyBoardSubmitText(String text){
        if(TextUtils.isEmpty(text))return;
        invalidate();
        mKeyBoard = this.getKeyboard();
        List<Key> keys = null;
        if (mKeyBoard != null) {
            keys = mKeyBoard.getKeys();
        }

        if (keys != null) {
            for (Key key : keys) {
                // 数字键盘的处理
                if (key.codes[0] == -4) {
                    key.label=text;
                    break;
                }
            }
        }
    }

    private void drawText(Canvas canvas, Key key) {
        Rect bounds = new Rect();
//        Paint paint = new Paint();
        //换行要用TextPaint
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
//        textPaint.setTextAlign(Paint.Align.CENTER);
//        paint.setTextAlign(Paint.Align.CENTER);


        if (key.label != null) {
            String label = key.label.toString();

            Field field;

            if (label.length() > 1 && key.codes.length < 2) {
                int labelTextSize = 0;
                try {
                    field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                    field.setAccessible(true);
                    labelTextSize = (int) field.get(this);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                textPaint.setTextSize(getResources().getDimension(R.dimen.text_size_20));
                textPaint.setTypeface(Typeface.DEFAULT);
            } else {
                int keyTextSize = 0;
                try {
                    field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                    field.setAccessible(true);
                    keyTextSize = (int) field.get(this);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                textPaint.setTextSize(getResources().getDimension(R.dimen.text_size_20));
                textPaint.setTypeface(Typeface.DEFAULT);
            }

            textPaint.getTextBounds(key.label.toString(), 0, key.label.toString()
                    .length(), bounds);

            StaticLayout layout = new StaticLayout(key.label.toString(), textPaint, DisplayUtil.dp2px(mContext,95f), Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
            // 这里的参数95，表示字符串的长度，当满95时，就会换行，也可以使用“\r\n”来实现换行
            canvas.save();
            // bounds.with 和height是"确认\n投资"字符串的宽高
//            canvas.translate(key.x + (key.width / 2)-bounds.height() ,key.y+ (key.height / 2)-bounds.height()-15 );//从中间开始画
            canvas.translate(key.x ,key.y+ (key.height / 2)-bounds.height()-15 );//从中间开始画 微调 设置StaticLayout .width会水平居中
            layout.draw(canvas);
            canvas.restore();//别忘了restore
//            canvas.drawText(key.label.toString(), key.x + (key.width / 2),
//                    (key.y + key.height / 2) + bounds.height() / 2, paint);
        } else if (key.icon != null) {
            key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2,
                    key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
            key.icon.draw(canvas);
        }

    }
}
