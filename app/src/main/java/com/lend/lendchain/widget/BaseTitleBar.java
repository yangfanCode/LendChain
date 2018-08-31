package com.lend.lendchain.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lend.lendchain.R;


/**
 * title bar
 */
public class BaseTitleBar extends RelativeLayout {
    protected ImageView imvLeftBack,imvUpdown, imvShare, imvCollect;
    protected TextView tvTitle,tvTitleChild;
    protected TextView tvRightText;
    protected FrameLayout layLeftBack;

    public BaseTitleBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public BaseTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseTitleBar(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.include_layout_title, this);
        setBackgroundResource(R.color.white);
        imvLeftBack = (ImageView) findViewById(R.id.imv_left_back);
        imvUpdown = (ImageView) findViewById(R.id.iv_title_updown);
        imvShare = (ImageView) findViewById(R.id.imv_share);
        imvCollect = (ImageView) findViewById(R.id.imv_collect);
        tvTitle = (TextView) findViewById(R.id.tv_title_text);
        tvTitleChild = (TextView) findViewById(R.id.tv_title_text_child);
        tvRightText = (TextView) findViewById(R.id.title_right_text);
        layLeftBack = (FrameLayout) findViewById(R.id.lay_title_image_left_back);

        parseStyle(context, attrs);
    }

    private void parseStyle(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseTitleBar);
            String title = ta.getString(R.styleable.BaseTitleBar_btbTitle);
            tvTitle.setText(title);

            Drawable leftDrawable = ta.getDrawable(R.styleable.BaseTitleBar_btbLeftImage);
            if (null != leftDrawable) {
                imvLeftBack.setImageDrawable(leftDrawable);
            }
            Drawable collectDrawable = ta.getDrawable(R.styleable.BaseTitleBar_btbCollectImage);
            if (null != collectDrawable) {
                imvCollect.setImageDrawable(collectDrawable);
                imvCollect.setVisibility(VISIBLE);
            }
            Drawable upDownDrawable = ta.getDrawable(R.styleable.BaseTitleBar_btbUpDownImage);
            if (null != upDownDrawable) {
                imvUpdown.setImageDrawable(upDownDrawable);
                imvUpdown.setVisibility(VISIBLE);
            }
            Drawable shareDrawable = ta.getDrawable(R.styleable.BaseTitleBar_btbShareImage);
            if (null != shareDrawable) {
                imvShare.setImageDrawable(shareDrawable);
                imvShare.setVisibility(VISIBLE);
            }

            Drawable background = ta.getDrawable(R.styleable.BaseTitleBar_btbBackground);
            if (null != background) {
                this.setBackgroundDrawable(background);
            }

            ta.recycle();
        }
    }


    public void setLayLeftBackClickListener(OnClickListener listener) {
        layLeftBack.setOnClickListener(listener);
    }

    public void setImvCollectClickListener(OnClickListener listener) {
        imvCollect.setOnClickListener(listener);
    }

    public void setImvShareClickListener(OnClickListener listener) {
        imvShare.setOnClickListener(listener);
    }

    public void setTvRightTextClickListener(OnClickListener listener) {
        tvRightText.setOnClickListener(listener);
    }

    public void setLeftImageResource(int resId) {
        imvLeftBack.setImageResource(resId);
    }

    public void setCollectImageResource(int resId) {
        imvCollect.setImageResource(resId);
        imvCollect.setVisibility(VISIBLE);
    }

    public void setUpDownImageResource(int resId) {
        imvUpdown.setImageResource(resId);
        imvUpdown.setVisibility(VISIBLE);
    }

    public void setShareImageResource(int resId) {
        imvShare.setImageResource(resId);
        imvShare.setVisibility(VISIBLE);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }
    public void setTitleChild(CharSequence title) {
        if(!TextUtils.isEmpty(title)){
            tvTitleChild.setVisibility(VISIBLE);
            tvTitleChild.setText(title);
        }else{
            tvTitleChild.setVisibility(GONE);
        }
    }

    public void setTvRightText(CharSequence title) {
        tvRightText.setText(title);
        tvRightText.setVisibility(VISIBLE);
    }


    public ImageView getImvLeftBack() {
        return imvLeftBack;
    }

    public ImageView getImvShare() {
        return imvShare;
    }

    public ImageView getImvCollect() {
        return imvCollect;
    }

    public ImageView getImvUpDown() {
        return imvUpdown;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvTitleChild() {
        return tvTitleChild;
    }

    public TextView getTvRightText() {
        return tvRightText;
    }

    public FrameLayout getLayLeftBack() {
        return layLeftBack;
    }
}
