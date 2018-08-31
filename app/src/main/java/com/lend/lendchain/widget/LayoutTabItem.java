package com.lend.lendchain.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lend.lendchain.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LayoutTabItem extends FrameLayout {

    @BindView(R.id.v_oval_symbol)
    View vOvalSymbol;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.imv_image)
    ImageView imvImage;
    @BindView(R.id.tv_tab)
    TextView tvTab;
    private Context context;

    public LayoutTabItem(Context context) {
        super(context);
        initUI(context);
    }

    public LayoutTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    private void initUI(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_tab_item, this, true);
        ButterKnife.bind(this);
    }

    // 重置view
    public void reset() {
        setOvalSymbolVisibility(GONE);
        tvCount.setVisibility(GONE);
    }

    // 设置 消息数字
    public void setCount(int count) {
        tvCount.setText("" + count);
        tvCount.setVisibility(VISIBLE);
    }

    // 设置圆点  Visibility
    public void setOvalSymbolVisibility(int visibility) {
        vOvalSymbol.setVisibility(visibility);
    }

    public void setTab(int resId, String tabValue) {
        imvImage.setImageResource(resId);
        tvTab.setText(tabValue);
    }

}