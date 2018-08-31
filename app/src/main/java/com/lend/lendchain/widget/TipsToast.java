package com.lend.lendchain.widget;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
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
public class TipsToast extends Toast {
    private static TipsToast tipsToast;
    public TipsToast(Context context) {
        super(context);
    }

    public static TipsToast makeText(Context context, CharSequence text, int duration,int res) {
        TipsToast result = new TipsToast(context);

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.view_tips, null);
        TextView tv = (TextView) v.findViewById(R.id.tips_msg);
        if(tv!=null&&!TextUtils.isEmpty(text)){
            tv.setText(text);
            if(res!=0){//有图片
                ImageView icon=v.findViewById(R.id.tips_icon);
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(res);
            }

            result.setView(v);
            // setGravity方法用于设置位置，此处为垂直居中
            result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            result.setDuration(duration);
        }
        return result;
    }

    public static TipsToast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration,0);
    }

    @Override
    public void setText(CharSequence s) {
        if (getView() == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView tv = (TextView) getView().findViewById(R.id.tips_msg);
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        tv.setText(s);
    }
    //展示单例toast
    public static  void showTips(String msg) {
        if (tipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tipsToast.cancel();
            }else{
                tipsToast.setText(msg);
            }
        } else {
            tipsToast = TipsToast.makeText(ContextHelper.getApplication(), msg, TipsToast.LENGTH_SHORT,0);
        }
        tipsToast.show();
    }
    //展示新的toast
    public static void showNewTips(String msg){
        TipsToast.makeText(ContextHelper.getApplication(), msg, TipsToast.LENGTH_SHORT).show();
    }
    //展示带图片
    public static  void showTips(String msg,int res) {
        if (tipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tipsToast.cancel();
            }else{
                tipsToast.setText(msg);
            }
        } else {
            tipsToast = TipsToast.makeText(ContextHelper.getApplication(), msg, TipsToast.LENGTH_SHORT,res);
        }
        tipsToast.show();
    }
}
