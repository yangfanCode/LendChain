package com.lend.lendchain.ui.activity.login;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.widget.TipsToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class UpdatePwdActivity extends BaseActivity {

    @BindView(R.id.update_etOldPwd)
    EditText etOldPwd;
    @BindView(R.id.update_etNewPwd)
    EditText etNewPwd;
    @BindView(R.id.update_etConfirm)
    EditText etConfirm;
    @BindView(R.id.update_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.update_ivClose)
    ImageView ivClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows = false;
        setContentView(R.layout.activity_update_pwd);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        etOldPwd.setTypeface(Typeface.DEFAULT);
        etOldPwd.setTransformationMethod(new PasswordTransformationMethod());//修复inputType="textPassword" 时hint字体改变
        etNewPwd.setTypeface(Typeface.DEFAULT);
        etNewPwd.setTransformationMethod(new PasswordTransformationMethod());//修复inputType="textPassword" 时hint字体改变
        etConfirm.setTypeface(Typeface.DEFAULT);
        etConfirm.setTransformationMethod(new PasswordTransformationMethod());//修复inputType="textPassword" 时hint字体改变
        FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) ivClose.getLayoutParams();
        int statusBar=CommonUtil.getStatusBarHeight();
        params.topMargin=statusBar+ DisplayUtil.dp2px(this,23f);
        ivClose.setLayoutParams(params);
        initListener();

    }

    private void initListener() {
        ivClose.setOnClickListener(v -> finish());
        etNewPwd.setOnFocusChangeListener((v, hasFocus) -> {//密码校验
            if(!hasFocus){
                editTextCheck();
            }
        });
        etConfirm.setOnFocusChangeListener((v, hasFocus) -> {//密码校验
            if(!hasFocus){
                editTextCheck();
            }
        });
        btnConfirm.setOnClickListener(v -> {
            String oldPwd=etOldPwd.getText().toString().trim();
            String newPwd=etNewPwd.getText().toString().trim();
            if(TextUtils.isEmpty(oldPwd)||TextUtils.isEmpty(newPwd)){
                TipsToast.showTips(getString(R.string.please_input_correct_pwd));//请输入正确格式
                return;
            }
            if(!checkPwdEquels()){
                return;
            }
            NetApi.updatePwd(UpdatePwdActivity.this,true, SPUtil.getToken(),oldPwd,newPwd,updatePwdObserver);
        });
    }

    //检测密码输入
    private void editTextCheck() {
        String pwd=etNewPwd.getText().toString().trim();
        String confrimPwd=etConfirm.getText().toString().trim();
        if(!CommonUtil.checkPwd(pwd)){//先校验格式
            TipsToast.showTips(getString(R.string.please_input_correct_pwd));//请输入正确格式
            return;
        }//再校验两次密码
        if(!TextUtils.isEmpty(pwd)&&!TextUtils.isEmpty(confrimPwd))checkPwdEquels();
    }

    //检测两次输入密码一致性
    private boolean checkPwdEquels() {
        String pwd=etNewPwd.getText().toString().trim();
        String confrimPwd=etConfirm.getText().toString().trim();
        if(!pwd.equals(confrimPwd)){
            TipsToast.showTips(getString(R.string.pwd_not_equals));
            return false;
        }
        return true;
    }

    //修改密码
    Observer<ResultBean> updatePwdObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                TipsToast.showTips(getString(R.string.update_success));
                ContextHelper.getApplication().runDelay(UpdatePwdActivity.this::finish,500);//延时关闭页面
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };

}
