package com.lend.lendchain.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lend.lendchain.R;


/**标题置顶titleListView
 * Created by yangfan on 2018/1/20.
 */
public class TitledListView extends ListView {

    private View mTitle;
    TextView title_text;
    public static boolean FLAG_VIEW;// 隐藏title标识,true:gone ； false:visibile（如果要不显示标题，改值一定要在刷新适配器值前调用）

    public TitledListView(Context context) {
        super(context);
    }

    public TitledListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitledListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTitle != null) {
            measureChild(mTitle, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mTitle != null) {
            mTitle.layout(0, 0, mTitle.getMeasuredWidth(), mTitle.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (FLAG_VIEW) {
            mTitle.setVisibility(View.GONE);
        } else {
            if (mTitle != null) {

                drawChild(canvas, mTitle, getDrawingTime());
            }
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mTitle = inflater.inflate(R.layout.title_listview_title, this, false);
        title_text = (TextView) mTitle.findViewById(R.id.fastInfo_titled_text);
    }

    public void moveTitle(String title) {
        View bottomChild = getChildAt(0);
        if (bottomChild != null) {
            int bottom = bottomChild.getBottom();
            int height = mTitle.getMeasuredHeight();
            int y = 0;
            if (bottom < height) {
                y = bottom - height;
            }
            if (title != null) {
                title_text.setText(title);
            }
            mTitle.layout(0, y, mTitle.getMeasuredWidth(), mTitle.getMeasuredHeight() + y);
        }
    }

    public void updateTitle(String title) {
        if (title != null) {
            title_text.setText(title);
        }
        mTitle.layout(0, 0, mTitle.getMeasuredWidth(), mTitle.getMeasuredHeight());
    }

    public String getTitle() {
        return title_text.getText().toString();
    }

    public View getTitleView(){
        return title_text;
    }
}
