package com.lend.lendchain.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.utils.ViewUtils;
import com.yangfan.utils.CommonUtils;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 抵押满标的警戒线布局 用px计算适配全面屏
 */
public class MortgageFullProgressView extends LinearLayout {
    private ProgressBar pb;
    private LinearLayout llPoint1, llPoint2,llLine,llPbLayout;
    private int progressBarWidth;//进度条宽度
    private int px30,px13;

    public MortgageFullProgressView(Context context) {
        super(context);
    }

    public MortgageFullProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.mortgage_full_progress_layout, this, true);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        llPoint1 = findViewById(R.id.mortgage_full_llPoint1);
        llPoint2 = findViewById(R.id.mortgage_full_llPoint2);
        llLine = findViewById(R.id.mortagage_full_llLine);
        llPbLayout=findViewById(R.id.mortagage_full_llProgressLayout);
        pb = findViewById(R.id.mortagage_full_pb);
        int width = CommonUtils.getScreenWidth(context);//获得屏幕宽度
        px30=30;//30px 像素值 适配全面屏
        px13=13;//30px 像素值 适配全面屏
        progressBarWidth = width - px30 * 2;//进度条宽度
        int dp7Progress=px13*100/progressBarWidth;//7dp所代表的进度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0以上才能用
            pb.setMin(100-dp7Progress);
        }
        pb.setMax(200+dp7Progress);//初始化progressBar动态设置起点终点
    }
    //左右要留白 各7dp 转化为进度progress
    private void setPointLayoutMargin(int progressBarWidth,int progress, LinearLayout layout) {
        double margin = 0;//进度条最小98 进度条进度宽度+边距
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0以上可以用
            margin = progressBarWidth * ((double)(progress - pb.getMin()) / (double)(pb.getMax()-pb.getMin())) + px30- ViewUtils.getViewWidth(layout)/2;
        }else{
            margin = progressBarWidth * ((progress - 98) / (pb.getMax()-98)) + px30;
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins((int)margin, 0, 0, 0);
        layout.setLayoutParams(params);
    }

    public void setProgress(int progress){
        pb.setProgress(progress);
    }
    //设置文字后重新设置margin
    public void setPoint1Text(String text){
        TextView tvCode1=llPoint1.findViewById(R.id.mortagage_full_tvUnWindCode);
        tvCode1.setText(text);
        setPointLayoutMargin(progressBarWidth,110,llPoint1);
    }
    //设置文字后重新设置margin
    public void setPoint2Text(String text){
        TextView tvCode2=llPoint2.findViewById(R.id.mortagage_full_tvCordonCode);
        tvCode2.setText(text);
        setPointLayoutMargin(progressBarWidth,140,llPoint2);
    }
}
