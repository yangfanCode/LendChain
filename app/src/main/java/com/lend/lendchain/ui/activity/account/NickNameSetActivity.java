package com.lend.lendchain.ui.activity.account;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.PopUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.ViewUtils;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class NickNameSetActivity extends BaseActivity {

    @BindView(R.id.nick_name_set_etNickName)
    EditText etNickName;
    @BindView(R.id.nick_name_set_ivSlices)
    ImageView ivSlices;
    @BindView(R.id.nick_name_set_btnConfirm)
    TextView btnConfirm;
    private PopupWindow popupWindow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name_set);
        StatusBarUtil.setStatusBarColor(NickNameSetActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(NickNameSetActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.set_nickname));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        initListener();
    }

    private void initListener() {
        ivSlices.setOnClickListener(v -> {
            if (popupWindow == null) {
                popupWindow = PopUtils.createPop(NickNameSetActivity.this, R.layout.pwd_tip_layout);
            }
            ImageView iv=popupWindow.getContentView().findViewById(R.id.tip_iv);
            iv.setBackground(null);
            iv.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.icon_nickname_slicens));
            int width= ViewUtils.getViewWidth(iv);
            int height=ViewUtils.getViewHeight(iv);
            int[] location = new int[2];
            ivSlices.getLocationOnScreen(location);
            //x 屏幕宽度-popwindow宽度-margin  y ivSlices纵坐标-popwindow高度
            popupWindow.showAtLocation(ivSlices, Gravity.TOP | Gravity.LEFT, CommonUtils.getScreenWidth(NickNameSetActivity.this) - width-DisplayUtil.dp2px(NickNameSetActivity.this, 5), location[1] - height);//展示按钮上方
        });
        btnConfirm.setOnClickListener(v -> {
            String nickName=etNickName.getText().toString().trim();
            if(TextUtils.isEmpty(nickName)){
                TipsToast.showTips(getString(R.string.please_input_nickname));
                return;
            }
            //中文英文数字韩文
            if(!Pattern.matches("^([a-zA-Z0-9\\u4e00-\\u9fa5\\uac00-\\ud7ff]*)$",nickName)){
                TipsToast.showTips(getString(R.string.please_input_regex_nickname));
                return;
            }
            NetApi.setNickName(NickNameSetActivity.this, SPUtil.getToken(),nickName,nickNameObserver);
        });
    }

    Observer<ResultBean> nickNameObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                TipsToast.showTips(getString(R.string.set_success));
                ContextHelper.getApplication().runDelay(NickNameSetActivity.this::finish,500);//延时关闭
            }else{
                setHttpFailed(NickNameSetActivity.this,resultBean);
            }
        }
    };

}
