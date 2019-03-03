package com.lend.lendchain.widget;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lend.lendchain.R;
import com.lend.lendchain.helper.ContextHelper;


/**
 * 自定义提示Toast
 * 
 * @author yangfan
 */
public class TipsToast{
    private static final int LONG_DELAY = 3500; // 3.5 seconds
    private static final int SHORT_DELAY = 2000; // 2 seconds
    private static String oldMsg;

    private static long lastTime = 0;

    private Toast toast = null;
    private View layoutToast;
    private TextView tvToast;
    private ImageView ivToast;
    private static TipsToast tipsToast = new TipsToast();

    public static synchronized void showTips(String content){
        showTips(content,0);
    }

    public static synchronized void showTips(String content,int res) {//  0
        cancelToast();
        if (tipsToast.toast == null) {
            tipsToast.toast = new Toast(ContextHelper.getApplication());
            tipsToast.toast.setGravity(Gravity.CENTER, 0, 0);
            tipsToast.layoutToast = LayoutInflater.from(ContextHelper.getApplication()).inflate(R.layout
                    .view_tips, null);

            tipsToast.tvToast = (TextView) tipsToast.layoutToast.findViewById(R.id
                    .tips_msg);
            tipsToast.tvToast.setText(content);
            if(res!=0){//有图片
                tipsToast.ivToast=tipsToast.layoutToast.findViewById(R.id.tips_icon);
                tipsToast.ivToast.setVisibility(View.VISIBLE);
                tipsToast.ivToast.setImageResource(res);
            }
            tipsToast.toast.setView(tipsToast.layoutToast);
            tipsToast.toast.setDuration(Toast.LENGTH_SHORT);
            tipsToast.toast.show();
            lastTime = System.currentTimeMillis();
            oldMsg = content;
        } else {
            //小于Toast的显示时间
            if (System.currentTimeMillis() - lastTime <= SHORT_DELAY) {
                tipsToast.tvToast.setText(content);
                oldMsg = content;
                tipsToast.toast.setDuration(Toast.LENGTH_SHORT);
                tipsToast.toast.show();
                lastTime = System.currentTimeMillis();
            } else {
                tipsToast.tvToast.setText(content);
                tipsToast.toast.setDuration(Toast.LENGTH_SHORT);
                tipsToast.toast.show();
                lastTime = System.currentTimeMillis();
            }
        }
    }

    public static synchronized void cancelToast() {
        if (null != tipsToast.toast) {
            tipsToast.toast.cancel();
        }
        tipsToast.toast = null;
        tipsToast.tvToast = null;
        tipsToast.ivToast=null;
        tipsToast.layoutToast = null;
    }

}
