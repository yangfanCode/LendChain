package com.lend.lendchain.network.api;

import android.content.Context;
import android.text.TextUtils;

import com.lend.lendchain.network.NetClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 接口调用类
 *Created by yangfan on 2017/04/26.
 */

public class NetApi {

    /**
     * 首页推荐
     */
    public static void banner(Context context, Observer observer) {
        NetClient.getInstance().getPost("", false, context).banner().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 首页推荐
     */
    public static void homeSupport(Context context, Observer observer) {
        NetClient.getInstance().getPost("", false, context).homeSupport().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 首页行情市值
     */
    public static void homeMarket(Context context, Observer observer) {
        NetClient.getInstance().getPost("", false, context).homeMarket().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 人民币美元汇率
     */
    public static void coinTransferRate(Context context, int symbol, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", symbol);
        NetClient.getInstance().getPost("", false, context).coinTransferRate(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 获得币种24小时K线
     */
    public static void KLinePrice24h(Context context, int symbol,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", symbol);
        NetClient.getInstance().getPost("", false, context).KLinePrice24h(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 投资列表接口
     */
    public static void investList(Context context,boolean isShow, int page,int page_size,int type ,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("page_size", page_size);
        map.put("type", type);
        NetClient.getInstance().getPost("", isShow, context).investList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 投资详情接口
     */
    public static void investSummary(Context context,boolean isShow, String borrowId ,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("borrowId", borrowId);
        NetClient.getInstance().getPost("", isShow, context).investSummary(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 发送邮箱验证码
     */
    public static void sendEmailCode(Context context,boolean isShow, String email,String sessionId ,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("sessionId", sessionId);
        map.put("type", "1");
        NetClient.getInstance().getPost("", isShow, context).sendEmailCode(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 注册
     */
    public static void regiest(Context context,boolean isShow, String email,String emailCode,String pwd ,String code,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("emailCode", emailCode);
        map.put("pwd", pwd);
        map.put("code", code);
        NetClient.getInstance().getPost("", isShow, context).regiest(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 登录
     */
    public static void login(Context context,boolean isShow, String username,String password,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        map.put("client", "frontend");
        NetClient.getInstance().getPost("", isShow, context).login(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 重置密码
     */
    public static void resetPwd(Context context,boolean isShow, String email,String emailCode,String pwd,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("emailCode", emailCode);
        map.put("pwd", pwd);
        NetClient.getInstance().getPost("", isShow, context).resetPwd(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 修改密码
     */
    public static void updatePwd(Context context,boolean isShow, String access_token,String oldpwd,String newpwd,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("oldpwd", oldpwd);
        map.put("newpwd", newpwd);
        map.put("access_token", access_token);
        NetClient.getInstance().getPost("", isShow, context).updatePwd(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 获取用户信息
     */
    public static void getUserInfo(Context context,boolean isShow, String access_token,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        NetClient.getInstance().getPost("", isShow, context).getUserInfo(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 查询kyc被拒理由
     */
    public static void kycRefuseReason(Context context,boolean isShow, String access_token,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        NetClient.getInstance().getPost("", isShow, context).kycRefuseReason(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 发送短信验证
     */
    public static void sendSMSCode(Context context,boolean isShow, String access_token,String phone,String countryCode,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("phone", phone);
        map.put("countryCode", countryCode);
        NetClient.getInstance().getPost("", isShow, context).sendSMSCode(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 手机认证
     */
    public static void phoneCertify(Context context,boolean isShow, String access_token,String phone,String code,String countryCode,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("phone", phone);
        map.put("code", code);
        map.put("countryCode", countryCode);
        NetClient.getInstance().getPost("", isShow, context).phoneCertify(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 获取谷歌验证码
     */
    public static void getGoogleCode(Context context,boolean isShow, String access_token,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        NetClient.getInstance().getPost("", isShow, context).getGoogleCode(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 发送短信验证码(不需要手机号)
     */
    public static void sendSMSCodeNoPhone(Context context,boolean isShow, String access_token,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        NetClient.getInstance().getPost("", isShow, context).sendSMSCodeNoPhone(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 谷歌认证
     */
    public static void googleCertfy(Context context,boolean isShow, String access_token,String google_code,String sms_code,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("google_code", google_code);
        map.put("sms_code", sms_code);
        NetClient.getInstance().getPost("", isShow, context).googleCertfy(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 获取某一币种数量
     */
    public static void getCoinCount(Context context, boolean isShow,String access_token,int crypto_id,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("cryptoId", String.valueOf(crypto_id));
        NetClient.getInstance().getPost("", isShow, context).getCoinCount(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的钱包资产列表
     */
    public static void myWalletList(Context context,boolean isShow, String access_token,int page,int page_size,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("page", page);
        map.put("page_size", page_size);
        NetClient.getInstance().getPost("", isShow, context).myWalletList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 货币属性获得我的充值地址 提现手续费
     */
    public static void coinAttribute(Context context,boolean isShow, String access_token,String cryptoId,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("cryptoId", cryptoId);
        NetClient.getInstance().getPost("", isShow, context).coinAttribute(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 货币属性获得我的充值地址
     */
    public static void investCreat(Context context,String access_token,String borrowId,String amount,String googleCode,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("borrowId", borrowId);
        map.put("amount", amount);
        map.put("googleCode", googleCode);
        NetClient.getInstance().getPost("", true, context).investCreat(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 借款类型
     */
    public static void loanTypes(Context context, Observer observer) {
        NetClient.getInstance().getPost("", false, context).loanTypes().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 借款币种
     */
    public static void loanPairs(Context context, Observer observer) {
        NetClient.getInstance().getPost("", true, context).loanPairs().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 获取币种价格
     */
    public static void getCoinPrice(Context context,boolean isShow,String mortgageCryptoId,String borrowCryptoId, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("mortgageCryptoId", mortgageCryptoId);
        map.put("borrowCryptoId", borrowCryptoId);
        NetClient.getInstance().getPost("", isShow, context).getCoinPrice(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 获取币种价格
     */
    public static void kycCertify(Context context,String access_token,String countryCode,String country,String name,String birthday,
                               String type,String idNumber, String frontPic, String backPic,String yourselfPic, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("countryCode", countryCode);
        map.put("country", country);
        map.put("name", name);
        map.put("birthday", birthday);
        map.put("type", type);
        map.put("idNumber", idNumber);
        map.put("frontPic", frontPic);
        map.put("backPic", backPic);
        map.put("yourselfPic", yourselfPic);
        NetClient.getInstance().getPost("", true, context).kycCertify(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 上传图片
     */
    public static void uploadPic(Context context, File file,  Observer observer) {
        Map<String, RequestBody> map = new HashMap<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        map.put("file\"; filename=\""+ file.getName(), requestBody);
        NetClient.getInstance().getPost("", true, context).uploadPic(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 借款创建抵押标
     */
    public static void createLoan(Context context,String borrowAmount,String borrowCryptoCode,String borrowCryptoId,String borrowDays,String googleCode,
                                  String interestRates,String mortgageAmount, String mortgageCryptoCode, String mortgageCryptoId,String mortgagePrice
                                  ,String time,String access_token,String symbol,String lvFee,String lvTime,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("borrowAmount", borrowAmount);
        map.put("borrowCryptoCode", borrowCryptoCode);
        map.put("borrowCryptoId", borrowCryptoId);
        map.put("borrowDays", borrowDays);
        map.put("googleCode", googleCode);
        map.put("interestRates", interestRates);
        map.put("mortgageAmount", mortgageAmount);
        map.put("mortgageCryptoCode", mortgageCryptoCode);
        map.put("mortgageCryptoId", mortgageCryptoId);
        map.put("mortgagePrice", mortgagePrice);
        map.put("time", time);
        map.put("access_token", access_token);
        map.put("symbol", symbol);
        map.put("lvFee", lvFee);
        map.put("lvTime", lvTime);
        NetClient.getInstance().getPost("", true, context).createLoan(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的钱包资产列表
     */
    public static void myInvestList(Context context,boolean isShow, String access_token,int page,int page_size,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("page", page);
        map.put("page_size", page_size);
        NetClient.getInstance().getPost("", isShow, context).myInvestList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的充值记录
     */
    public static void myReChargeList(Context context,boolean isShow, String access_token,int page,int page_size,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("page", page);
        map.put("page_size", page_size);
        NetClient.getInstance().getPost("", isShow, context).myReChargeList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的提现记录
     */
    public static void myWithDrawList(Context context,boolean isShow, String access_token,int page,int page_size,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("page", page);
        map.put("page_size", page_size);
        NetClient.getInstance().getPost("", isShow, context).myWithDrawList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的借款记录
     */
    public static void myLoanList(Context context,boolean isShow, String access_token,int page,int page_size,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("page", page);
        map.put("page_size", page_size);
        NetClient.getInstance().getPost("", isShow, context).myLoanList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的借款详情
     */
    public static void myLoanSummary(Context context, String borrowId,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("borrowId", borrowId);
        NetClient.getInstance().getPost("", true, context).myLoanSummary(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 追加抵押物
     */
    public static void addMortgage(Context context, String access_token, String borrowId,String googleCode,String amount,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("borrowId", borrowId);
        map.put("googleCode", googleCode);
        map.put("amount", amount);
        NetClient.getInstance().getPost("", true, context).addMortgage(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 设置昵称
     */
    public static void setNickName(Context context, String access_token, String nickname,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("nickname", nickname);
        NetClient.getInstance().getPost("", true, context).setNickName(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 发送邮箱验证码(不需要邮箱)
     */
    public static void sendEmailCodeNoEmail(Context context, boolean isShow, String access_token, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        NetClient.getInstance().getPost("", isShow, context).sendEmailCodeNoEmail(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 提现操作
     */
    public static void withDrawCreate(Context context,boolean isShow, String access_token,String googleCode,String address,
                                      String memo,String amount,String assetId,String emailCode,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("googleCode", googleCode);
        map.put("address", address);
        map.put("memo", memo);
        map.put("amount", amount);
        map.put("assetId", assetId);
        map.put("emailCode", emailCode);
        NetClient.getInstance().getPost("", isShow, context).withDrawCreate(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 发送邮箱验证码(不需要邮箱)
     */
    public static void transferCreate(Context context,boolean isShow, String access_token,String cryptoId,String cryptoCode,String googleCode,
                                      String email,String amount,String nickname,String emailCode,String phone,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("cryptoId", cryptoId);
        map.put("cryptoCode", cryptoCode);
        map.put("googleCode", googleCode);
        map.put("email", email);
        map.put("amount", amount);
        map.put("emailCode", emailCode);
        if(!TextUtils.isEmpty(nickname)){
            map.put("nickname", nickname);
        }
        if(!TextUtils.isEmpty(phone)){
            map.put("phone", phone);
        }
        NetClient.getInstance().getPost("", isShow, context).transferCreate(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 获取币种列表(充值加速)
     */
    public static void getCoinList(Context context, boolean isShow, String access_token, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        NetClient.getInstance().getPost("", isShow, context).getCoinList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 充值加速确认
     */
    public static void rechargeSpeedCreate(Context context, boolean isShow, String access_token, String uniqueId,String hash,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("uniqueId", uniqueId);
        map.put("hash", hash);
        NetClient.getInstance().getPost("", isShow, context).rechargeSpeedCreate(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 还款
     */
    public static void payBack(Context context, boolean isShow, String access_token,String borrowId, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("borrowId", borrowId);
        NetClient.getInstance().getPost("", isShow, context).payBack(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * app版本更新
     */
    public static void versionControl(Context context, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("plat", "android");
        NetClient.getInstance().getPost("", false, context).versionControl(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * LV发放明细
     */
    public static void sendLvRecord(Context context,boolean isShow,String access_token,int page,int page_size, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("page", page);
        map.put("page_size", page_size);
        NetClient.getInstance().getPost("", isShow, context).sendLvRecord(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的转账记录
     */
    public static void myTransferRecord(Context context,boolean isShow,String access_token,int page,int page_size, Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("pageNo", page);
        map.put("pageSize", page_size);
        NetClient.getInstance().getPost("", isShow, context).myTransferRecord(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 币种图片列表
     */
    public static void coinIconList(Context context,Observer observer) {
        NetClient.getInstance().getPost("", false, context).coinIconList().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的消息列表
     */
    public static void messageList(Context context,boolean isShow,String access_token,int page,int page_size, int type,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("page", page);
        map.put("page_size", page_size);
        map.put("type", type);
        NetClient.getInstance().getPost("", isShow, context).messageList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 我的消息列表
     */
    public static void messageDetail(Context context,boolean isShow,String access_token,String noticeId,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("noticeId", noticeId);
        NetClient.getInstance().getPost("", isShow, context).messageDetail(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 布洛克城钱包充值
     */
    public static void blockCityRecharge(Context context,boolean isShow,String access_token,String amount,String cryptoId,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("amount", amount);
        map.put("cryptoId", cryptoId);
        NetClient.getInstance().getPost("", isShow, context).blockCityRecharge(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 布洛克城钱包充值(查询充值订单)
     */
    public static void getBlockCityRecharge(Context context,boolean isShow,String access_token,String orderId,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("orderId", orderId);
        NetClient.getInstance().getPost("", isShow, context).getBlockCityRecharge(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 布洛克城钱包提现
     */
    public static void blockCityWithDraw(Context context,boolean isShow, String access_token,String googleCode,
                                         String memo,String amount,String assetId,String emailCode,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("googleCode", googleCode);
        map.put("memo", memo);
        map.put("amount", amount);
        map.put("assetId", assetId);
        map.put("emailCode", emailCode);
        NetClient.getInstance().getPost("", isShow, context).blockCityWithDraw(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 充值记录关闭订单
     */
    public static void closeOrderRecharge(Context context,boolean isShow,String access_token, String orderId,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("orderId", orderId);
        NetClient.getInstance().getPost("", isShow, context).closeOrderRecharge(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 兑换币种列表
     */
    public static void coinChangeList(Context context,boolean isShow,String access_token,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        NetClient.getInstance().getPost("", isShow, context).coinChangeList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 兑换-查看费率
     */
    public static void coinChangeRate(Context context,boolean isShow,String access_token,String depositCoinId,String receiveCoinId,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("depositCoinId", depositCoinId);
        map.put("receiveCoinId", receiveCoinId);
        NetClient.getInstance().getPost("", isShow, context).coinChangeRate(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 兑换-提交
     */
    public static void coinChangeComit(Context context,boolean isShow,String access_token,String depositCoinId,String depositAmount,String receiveCoinId,String receiveAmount,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("depositCoinId", depositCoinId);
        map.put("depositAmount", depositAmount);
        map.put("receiveCoinId", receiveCoinId);
        map.put("receiveAmount", receiveAmount);
        NetClient.getInstance().getPost("", isShow, context).coinChangeComit(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    /**
     * 兑换-列表
     */
    public static void changeList(Context context,boolean isShow,String access_token,int page,int page_size,Observer observer) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("page", page);
        map.put("page_size", page_size);
        NetClient.getInstance().getPost("", isShow, context).changeList(map).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
