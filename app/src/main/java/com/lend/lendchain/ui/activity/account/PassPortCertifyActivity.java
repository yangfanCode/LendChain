package com.lend.lendchain.ui.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.KycInfo;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.network.subscriber.SafeOnlyNextSubscriber;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.MainActivity;
import com.lend.lendchain.utils.BitmapUtil;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.LoadAnimationUtils;
import com.lend.lendchain.utils.LogUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SelectPicturePopupWindowUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.versioncontrol.utils.FilePathUtils;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PassPortCertifyActivity extends BaseActivity {

    @BindView(R.id.idcard_cerfity_ivIdCardPositive)
    ImageView ivIdCardPositive;
    @BindView(R.id.idcard_cerfity_ivPositiveCamera)
    ImageView ivPositiveCamera;
    @BindView(R.id.idcard_cerfity_ivIdCardNagetive)
    ImageView ivIdCardNagetive;
    @BindView(R.id.idcard_cerfity_ivNagetiveCamera)
    ImageView ivNagetiveCamera;
    @BindView(R.id.idcard_cerfity_ivSelfPhoto)
    ImageView ivSelfPhoto;
    @BindView(R.id.idcard_cerfity_ivSelfCamera)
    ImageView ivSelfCamera;
    @BindView(R.id.idcard_cerfity_btnConfirm)
    Button btnConfirm;
    private PopupWindow mPopupWindow;
    public Dialog processDialog;
    private LoadAnimationUtils loadAnimationUtils = null;
    private SelectPicturePopupWindowUtils selectPicturePopupWindowUtils;
    private KycInfo kycInfo;
    private String filePath = null;
    private String pic1,pic2,pic3;
    private int picPos=0;//判断是那张照片请求的接口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_port);
        StatusBarUtil.StatusBarLightMode(PassPortCertifyActivity.this);

    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.kyc_certify));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        loadAnimationUtils = new LoadAnimationUtils(this);
        initListener();
        if(getIntent().getExtras()!=null){
            kycInfo=getIntent().getExtras().getParcelable(Constant.INTENT_EXTRA_DATA);
        }
    }

    private void initListener() {
        ivPositiveCamera.setOnClickListener(v -> {
            picPos=0;//正面
            showPopWindow();
        });
        ivNagetiveCamera.setOnClickListener(v -> {
            picPos=1;//反面
            showPopWindow();
        });
        ivSelfCamera.setOnClickListener(v -> {
            picPos=2;//自己的照片
            showPopWindow();
        });
        btnConfirm.setOnClickListener(v -> {
            if(TextUtils.isEmpty(pic1)||TextUtils.isEmpty(pic2)||TextUtils.isEmpty(pic3)){
                TipsToast.showTips(getString(R.string.please_upload_document_material));
                return;
            }
            //友盟埋点 提交身份认证
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_KYC_SUBMIT);
            NetApi.kycCertify(PassPortCertifyActivity.this, SPUtil.getToken(),kycInfo.countryCode,kycInfo.country,kycInfo.realName
                    ,kycInfo.bornDate,kycInfo.type,kycInfo.documentNum,pic1,pic2,pic3,kycObserver);
        });
    }
    //展示 拍照 相册 pop
    private void showPopWindow() {
        if (mPopupWindow == null) {
            if (selectPicturePopupWindowUtils == null)
                selectPicturePopupWindowUtils = new SelectPicturePopupWindowUtils(PassPortCertifyActivity.this);
            mPopupWindow = selectPicturePopupWindowUtils.showSelectPicturePopupWindow();
        }
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(btnConfirm, Gravity.BOTTOM, 0, 0);
            CommonUtil.setBackgroundAlpha(PassPortCertifyActivity.this, 0.5f);
        }
    }

    private void compressUpload() {
        if (processDialog != null && processDialog.isShowing()) {
            processDialog.dismiss();
            loadAnimationUtils.closeProcessAnimation();
            loadAnimationUtils.showProcessAnimation();
            uploadPhoto();
        }
    }
    /**
     * 上传头像
     */
    private void uploadPhoto() {
        File headFile = new File(filePath);
        if (!headFile.exists()) {
            return;
        }
        loadAnimationUtils.closeProcessAnimation();
        NetApi.uploadPic(PassPortCertifyActivity.this,headFile,upLoadPicObserver);

    }
    private void compressImage(String path) {

        if (TextUtils.isEmpty(path)) {
            return;
        }
        processDialog = loadAnimationUtils.showProcessAnimation("");
        filePath = FilePathUtils.getFilePath(this, null) + File.separator + System.currentTimeMillis() + ".png";
        LogUtils.LogE(FilePathUtils.class, "filePath====>" + filePath);
        Observable.create(subscriber -> {
            try {
                BitmapUtil.compressAndGenImage(path, filePath, 500, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            subscriber.onNext(path.concat("_").concat(filePath));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SafeOnlyNextSubscriber<Object>(){
                    @Override
                    public void onNext(Object args) {
                        super.onNext(args);
                        compressUpload();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        loadAnimationUtils.closeProcessAnimation();
                        TipsToast.showTips("图片压缩失败");
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == Constant.REQUEST_CODE_PHOTO_CAMERA) {
            Bitmap bitmap= BitmapUtil.getSmallBitmap2(CommonUtils.getImagePathFromUri(Constant.uri, this), CommonUtils.getScreenWidth(this), CommonUtils.getScreenWidth(this), 500);
            String  filePath=getFileFromBitmap(bitmap);
            compressImage(filePath);

        } else if (requestCode == Constant.REQUEST_CODE_PHOTO_ALBUM) {
            if (intent != null && intent.getData() != null) {
                Uri path = intent.getData();
                Bitmap bitmap= BitmapUtil.getSmallBitmap2(CommonUtils.getImagePathFromUri(path, this), CommonUtils.getScreenWidth(this), CommonUtils.getScreenWidth(this), 500);
                String  filePath=getFileFromBitmap(bitmap);
                compressImage(filePath);
            }
        }
//        else if (requestCode == Constant.REQUEST_CODE_PHOTO_CLIP) {
//            if (intent != null && intent.getExtras() != null)
//                compressImage(intent.getStringExtra("image"));
//        }
    }

    private String getFileFromBitmap(Bitmap bitmap){
        String filePath = FilePathUtils.getFilePath(this, null);
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(filePath, fileName);

        LogUtils.LogE(FilePathUtils.class, "filePath====>" + file.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    Observer<ResultBean<String>> upLoadPicObserver=new NetClient.RxObserver<ResultBean<String>>() {
        @Override
        public void onSuccess(ResultBean<String> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                if(picPos==0){
                    pic1=resultBean.data;
                    ivIdCardPositive.setImageBitmap(BitmapFactory.decodeFile(filePath));
                }else if(picPos==1){
                    pic2=resultBean.data;
                    ivIdCardNagetive.setImageBitmap(BitmapFactory.decodeFile(filePath));
                }else{
                    pic3=resultBean.data;
                    ivSelfPhoto.setImageBitmap(BitmapFactory.decodeFile(filePath));
                }
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };

    Observer<ResultBean> kycObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                TipsToast.showTips(getString(R.string.please_wait_check));
                ContextHelper.getApplication().runDelay(() -> CommonUtil.openActicity(PassPortCertifyActivity.this, MainActivity.class,null), 500);
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };

}
