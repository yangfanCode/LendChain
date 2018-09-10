package com.lend.lendchain.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.KeyBordUtils;
import com.yangfan.widget.DecimalDigitsEditText;


/**
 * Created by js on 2018/1/18.
 * 键盘输入面板
 */
public class KeyBoardInputPopWindow {
    private PopupWindow popupWindow;
    private Activity activity;
    private Keyboard mKeyboardNumber;//数字键盘
    private CustomKeyBoardView mKeyboardView;
    public OnOkClick mOnOkClick = null;
    public onCancelClick mOnCancelClick;
    private LinearLayout llInputMoney,llInputGoogleCode;
    private EditText mEditText,etGoogleCode;//绑定的键盘
    private DecimalDigitsEditText etMoney;
    private String code,investLave;
    private double interestRates;
    private int borrowDays;
    private double income;//收益
    private int inputType=1;//键盘输入u模式 1 金钱模式 2 谷歌code模式
    private int mode;//默认状态 1输入金钱模式 2谷歌验证码模式
    private double amount;//账户余额
    //    private int[] shareToast={R.string.noWeChat,R.string.noWeChat,R.string.noWeiBo};
    public KeyBoardInputPopWindow(Activity activity,int mode,String code,double interestRates,int borrowDays,double amount,String investLave) {
        this.activity = activity;
        this.mode=mode;
        this.code=code;
        this.interestRates=interestRates;
        this.borrowDays=borrowDays;
        this.amount=amount;
        this.investLave=investLave;
    }
    public KeyBoardInputPopWindow(Activity activity,int mode) {
        this.activity = activity;
        this.mode=mode;
    }

    public PopupWindow creatPop() {
        View contentView = LayoutInflater.from(activity)
                .inflate(R.layout.keyboard_input_check_layout, null);
        mKeyboardView = (CustomKeyBoardView) contentView.findViewById(R.id.keyboard_view);
        llInputMoney=contentView.findViewById(R.id.keyboard_input_money_layout);
        llInputGoogleCode=contentView.findViewById(R.id.keyboard_input_googlecCode_layout);
        etMoney=llInputMoney.findViewById(R.id.keyboard_input_money_etMoney);
        etGoogleCode=llInputGoogleCode.findViewById(R.id.keyboard_input_money_etGoogleCode);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,true
        );
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.popWindow_animation);
        popupWindow.setOnDismissListener(() -> {
            etMoney.getText().clear();
            etGoogleCode.getText().clear();
            CommonUtil.setBackgroundAlpha(activity,1.0f);
        });
        if(mode==1){
            setInputMoneyMode();
        }else{
            setInputGoogleCodeMode();
        }
        return popupWindow;
    }

    public void setOnOkClick(OnOkClick onOkClick) {
        mOnOkClick = onOkClick;
    }

    public void setOnCancelClick(onCancelClick onCancelClick) {
        mOnCancelClick = onCancelClick;
    }

    //打开自定义数字键盘
    public void showSoftKeyboard() {
        if (mKeyboardNumber == null) {
            mKeyboardNumber = new Keyboard(activity, R.xml.keyboardnumber);
        }
//        if (mIfRandom) {
//            randomKeyboardNumber();
//        } else {
        mKeyboardView.setKeyboard(mKeyboardNumber);
//        }
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);

    }

    /**
     * edittext绑定自定义键盘
     *
     * @param editText 需要绑定自定义键盘的edittext
     */
    public void attachTo(EditText editText) {
        this.mEditText = editText;
        KeyBordUtils.hideSystemSofeKeyboard(activity.getApplicationContext(),editText);
        showSoftKeyboard();
    }

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = mEditText.getText();
            int start = mEditText.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 隐藏键盘
                dismiss();
                if (mOnCancelClick != null) {
                    mOnCancelClick.onCancellClick();
                }
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {// 隐藏键盘
//                dismiss();
                if (mOnOkClick != null) {
                    mOnOkClick.onOkClick();
                }
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };


    public interface OnOkClick {
        void onOkClick();
    }

    public interface onCancelClick {
        void onCancellClick();
    }

    public void dismiss(){
        if(popupWindow!=null){
            if(popupWindow.isShowing())popupWindow.dismiss();
        }
    }
    public EditText getEditTextMoney(){
        return etMoney;
    }
    public EditText getEditTextGoogleCode(){
        return etGoogleCode;
    }

    public double getIncome(){
        return income;
    }

    public int getInputType(){
        return inputType;
    }

    //设置是否可点击
    public void setKeyEnabled(boolean enabled){
        if(enabled){
            mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        }else{
            mKeyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
                @Override
                public void onPress(int primaryCode) { }

                @Override
                public void onRelease(int primaryCode) { }
                @Override
                public void onKey(int primaryCode, int[] keyCodes) { }
                @Override
                public void onText(CharSequence text) { }
                @Override
                public void swipeLeft() { }
                @Override
                public void swipeRight() { }
                @Override
                public void swipeDown() { }
                @Override
                public void swipeUp() { }
            });
        }
    }

    //设置确定文字
    public void setConfirmText(String text){
        mKeyboardView.setKeyBoardSubmitText(text);
    }
    /**
     * 输入金额模式
     */
    public void setInputMoneyMode(){
        inputType=1;
        llInputMoney.setVisibility(View.VISIBLE);
        llInputGoogleCode.setVisibility(View.GONE);
        TextView tvAllInvest=llInputMoney.findViewById(R.id.keyboard_input_money_tvAllInvest);
        TextView tvCode1=llInputMoney.findViewById(R.id.keyboard_input_money_tvCode1);
        TextView tvCode2=llInputMoney.findViewById(R.id.keyboard_input_money_tvCode2);
        TextView tvCode3=llInputMoney.findViewById(R.id.keyboard_input_money_tvCode3);
        TextView tvIncome=llInputMoney.findViewById(R.id.keyboard_input_money_tvIncome);
        ImageView ivClose=llInputMoney.findViewById(R.id.keyboard_input_money_ivClose);
        TextView tvAmount=llInputMoney.findViewById(R.id.keyboard_input_money_tvOver);
        tvCode1.setText(code);
        tvCode2.setText(code);
        tvCode3.setText(code);
        tvAmount.setText(DoubleUtils.doubleTransRound6(amount));
        etMoney.setFocusable(true);
        etMoney.setFocusableInTouchMode(true);
        etMoney.requestFocus();
        ivClose.setOnClickListener(v -> dismiss());
        tvAllInvest.setOnClickListener(v -> {//余额全投操作
            //当用余额小于标的余额时余额全投展示用户余额 当用户余额大于标的余额时展示标的余额
            if(amount<Double.parseDouble(investLave)){
                etMoney.setText(DoubleUtils.doubleTransRound6(amount));
            }else{
                etMoney.setText(investLave);
            }
            etMoney.setSelection(etMoney.getText().length());
        });
        etMoney.setOnTouchListener((v, event) -> {
            attachTo(etMoney);
            return false;
        });
        //输入文字监听 计算收益
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String investMoney=etMoney.getText().toString().trim();
                if(!TextUtils.isEmpty(investMoney)&&!investMoney.endsWith(".")){
                    if(Double.parseDouble(investMoney)>Double.parseDouble(investLave)){//输入的数值超过了剩余可投
                        etMoney.setText(investLave);
                        etMoney.setSelection(etMoney.getText().length());
                        return;
                    }
                    income=Double.parseDouble(investMoney)*interestRates*borrowDays;
                    tvIncome.setText(DoubleUtils.doubleTransRound6(income));//计算收益
                }else{
                    if(TextUtils.isEmpty(investMoney)){
                        tvIncome.setText("0");//计算收益
                    }
                }
            }
        });
        attachTo(etMoney);
    }

    /**
     * 输入谷歌验证码模式
     */
    public void setInputGoogleCodeMode(){
        inputType=2;
        llInputGoogleCode.setVisibility(View.VISIBLE);
        llInputMoney.setVisibility(View.GONE);
        ImageView ivClose=llInputGoogleCode.findViewById(R.id.keyboard_input_googleCode_ivClose);
        TextView tvPaste=llInputGoogleCode.findViewById(R.id.keyboard_input_money_tvPaste);
        etGoogleCode.setFocusable(true);
        etGoogleCode.setFocusableInTouchMode(true);
        etGoogleCode.requestFocus();
        ivClose.setOnClickListener(v -> dismiss());
        etGoogleCode.setOnTouchListener((v, event) -> {
            attachTo(etGoogleCode);
            return false;
        });
        tvPaste.setOnClickListener(v -> {
            String str=CommonUtil.clipboardPasteStr();
            if(!TextUtils.isEmpty(str)){
                etGoogleCode.setText(str);
                etGoogleCode.setSelection(etGoogleCode.getText().length());
            }
        });
        attachTo(etGoogleCode);
    }

}
