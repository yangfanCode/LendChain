package com.lend.lendchain.network.api;


import com.lend.lendchain.bean.BannerModel;
import com.lend.lendchain.bean.CoinIconList;
import com.lend.lendchain.bean.CoinList;
import com.lend.lendchain.bean.HomeMarket;
import com.lend.lendchain.bean.HomeMarketKLine;
import com.lend.lendchain.bean.HomeSupport;
import com.lend.lendchain.bean.InvestList;
import com.lend.lendchain.bean.InvestSummary;
import com.lend.lendchain.bean.LoanPairs;
import com.lend.lendchain.bean.Login;
import com.lend.lendchain.bean.MessageList;
import com.lend.lendchain.bean.MyInvestList;
import com.lend.lendchain.bean.MyLoanList;
import com.lend.lendchain.bean.MyLoanSummary;
import com.lend.lendchain.bean.MyWalletList;
import com.lend.lendchain.bean.RMB2Dollar;
import com.lend.lendchain.bean.RechargeWithDraw;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SendLvRecord;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.bean.TransferRecord;
import com.lend.lendchain.bean.UserInfo;
import com.lend.lendchain.bean.VersionControl;
import com.lend.lendchain.network.NetConst;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by yangfan on 17/04/24.
 */
public interface AppApi {

    /**
     * banner
     *
     * @return
     */
    @GET(NetConst.BANNER)
    Observable<BannerModel> banner();

    /**
     * 首页推荐
     *
     * @return
     */
    @GET("trade/invest/recommends")
    Observable<ResultBean<List<HomeSupport>>> homeSupport();
    /**
     * 首页行情市值
     *
     * @return
     */
    @GET("price/market")
    Observable<ResultBean<List<HomeMarket>>> homeMarket();
    /**
     * 币种汇率接口
     *
     * @return
     */
    @GET("price/latest")
    Observable<ResultBean<RMB2Dollar>> coinTransferRate(@QueryMap Map<String, Object> account);
    /**
     * 首页K线接口
     *
     * @return
     */
    @GET("price/fullPrice")
    Observable<ResultBean<List<HomeMarketKLine>>> KLinePrice24h(@QueryMap Map<String, Object> account);
    /**
     * 投资推荐列表
     *
     * @return
     */
    @GET("trade/invest/mobile/index")
    Observable<ResultBean<List<InvestList>>> investList(@QueryMap Map<String, Object> account);
    /**
     * 投资推荐详情
     *
     * @return
     */
    @GET("trade/borrow/detail")
    Observable<ResultBean<InvestSummary>> investSummary(@QueryMap Map<String, Object> account);
    /**
     * 发送邮箱验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/mail/send")
    Observable<ResultBean> sendEmailCode(@FieldMap Map<String, Object> account);
    /**
     * 注册
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/account/signup")
    Observable<ResultBean> regiest(@FieldMap Map<String, Object> account);
    /**
     * 登录
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/oauth/token?grant_type=password")
    Observable<ResultBean<Login>> login(@FieldMap Map<String, Object> account);
    /**
     * 重置密码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/account/resetpwd")
    Observable<ResultBean> resetPwd(@FieldMap Map<String, Object> account);
    /**
     * 修改密码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/user/updatepwd")
    Observable<ResultBean> updatePwd(@FieldMap Map<String, Object> account);
    /**
     * 获取用户信息
     *
     * @return
     */
    @GET("trade/user/profile")
    Observable<ResultBean<UserInfo>> getUserInfo(@QueryMap Map<String, Object> account);
    /**
     * 查询kyc被拒理由
     *
     * @return
     */
    @GET("trade/user/kyc/desc")
    Observable<ResultBean<String>> kycRefuseReason(@QueryMap Map<String, Object> account);
    /**
     * 发送短信验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/sms/send-code")
    Observable<ResultBean> sendSMSCode(@FieldMap Map<String, Object> account);
    /**
     * 手机认证
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/sms/auth-phone")
    Observable<ResultBean> phoneCertify(@FieldMap Map<String, Object> account);
    /**
     * 获取谷歌验证码
     *
     * @return
     */
    @GET("trade/mfa/generate")
    Observable<ResultBean<SimpleBean>> getGoogleCode(@QueryMap Map<String, Object> account);
    /**
     * 发送短信验证码(不需要手机号)
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/sms/send")
    Observable<ResultBean> sendSMSCodeNoPhone(@FieldMap Map<String, Object> account);
    /**
     * 谷歌认证
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/mfa/auth")
    Observable<ResultBean> googleCertfy(@FieldMap Map<String, Object> account);
    /**
     * 获取用户某一币种数量
     *
     * @return
     */
    @GET("trade/asset/amount")
    Observable<ResultBean<SimpleBean>> getCoinCount(@QueryMap Map<String, Object> account);
    /**
     * 我的钱包资产列表
     *
     * @return
     */
    @GET("trade/asset/index")
    Observable<ResultBean<List<MyWalletList>>> myWalletList(@QueryMap Map<String, Object> account);
    /**
     * 货币属性获得我的充值地址
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/crypto/profile")
    Observable<ResultBean<SimpleBean>> coinAttribute(@FieldMap Map<String, Object> account);
    /**
     * 投资
     *
     * @return
     */
    @FormUrlEncoded
    @POST("/trade/invest/create")
    Observable<ResultBean> investCreat(@FieldMap Map<String, Object> account);
    /**
     * 借款类型
     *
     * @return
     */
    @GET("trade/borrow/types")
    Observable<ResultBean<List<SimpleBean>>> loanTypes();
    /**
     * 借款币种
     *
     * @return
     */
    @GET("trade/crypto/borrow-pairs")
    Observable<ResultBean<List<LoanPairs>>> loanPairs();
    /**
     * 获取币种价格
     *
     * @return
     */
    @GET("trade/crypto/price")
    Observable<ResultBean<SimpleBean>> getCoinPrice(@QueryMap Map<String, Object> account);
    /**
     * kyc认证接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/user/kyc")
    Observable<ResultBean> kycCertify(@FieldMap Map<String, Object> account);
    /**
     * 上传图片
     *
     * @return
     */
    @Multipart
    @POST("trade/api/upload")
    Observable<ResultBean<String>> uploadPic(@PartMap Map<String, RequestBody> account);
    /**
     * 借款创建抵押标
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/borrow/create")
    Observable<ResultBean> createLoan(@FieldMap Map<String, Object> account);
    /**
     * 我的投资列表
     *
     * @return
     */
    @GET("trade/user/invests")
    Observable<ResultBean<List<MyInvestList>>> myInvestList(@QueryMap Map<String, Object> account);
    /**
     * 我的充值记录
     *
     * @return
     */
    @GET("trade/deposit/index")
    Observable<ResultBean<List<RechargeWithDraw>>> myReChargeList(@QueryMap Map<String, Object> account);
    /**
     * 我的提现记录
     *
     * @return
     */
    @GET("trade/withdraw/index")
    Observable<ResultBean<List<RechargeWithDraw>>> myWithDrawList(@QueryMap Map<String, Object> account);
    /**
     * 我的提现记录
     *
     * @return
     */
    @GET("trade/user/borrows")
    Observable<ResultBean<List<MyLoanList>>> myLoanList(@QueryMap Map<String, Object> account);
    /**
     * 我的借款详情
     *
     * @return
     */
    @GET("trade/borrow/detail")
    Observable<ResultBean<MyLoanSummary>> myLoanSummary(@QueryMap Map<String, Object> account);
    /**
     * 追加抵押物
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/append/mortgate")
    Observable<ResultBean> addMortgage(@FieldMap Map<String, Object> account);
    /**
     * 追加抵押物
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/user/nickname")
    Observable<ResultBean> setNickName(@FieldMap Map<String, Object> account);
    /**
     * 发送邮箱验证码(不需要邮箱)
     *
     * @return
     */
    @GET("trade/mail/send/me")
    Observable<ResultBean> sendEmailCodeNoEmail(@QueryMap Map<String, Object> account);
    /**
     * 提现操作
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/withdraw/create")
    Observable<ResultBean> withDrawCreate(@FieldMap Map<String, Object> account);
    /**
     * 平台内转账作
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/asset/transfer")
    Observable<ResultBean> transferCreate(@FieldMap Map<String, Object> account);
    /**
     * 获取币种列表(充值加速)
     *
     * @return
     */
    @GET("trade/crypto/list")
    Observable<ResultBean<List<CoinList>>> getCoinList(@QueryMap Map<String, Object> account);
    /**
     * 获取币种列表(充值加速)
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/deposit/supplement")
    Observable<ResultBean> rechargeSpeedCreate(@FieldMap Map<String, Object> account);
    /**
     * 还款
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/borrow/payback")
    Observable<ResultBean> payBack(@FieldMap Map<String, Object> account);
    /**
     * app版本更新
     *
     * @return
     */
    @FormUrlEncoded
    @POST("trade/system/notice")
    Observable<ResultBean<VersionControl>> versionControl(@FieldMap Map<String, Object> account);
    /**
     * LV发放明细
     *
     * @return
     */
    @GET("trade/lv/detail")
    Observable<ResultBean<SendLvRecord>> sendLvRecord(@QueryMap Map<String, Object> account);
    /**
     * 我的转账记录
     *
     * @return
     */
    @GET("trade/transfer/list")
    Observable<ResultBean<List<TransferRecord>>> myTransferRecord(@QueryMap Map<String, Object> account);
    /**
     * 币种图片列表
     *
     * @return
     */
    @GET("trade/crypto/icon/list")
    Observable<ResultBean<List<CoinIconList>>> coinIconList();
    /**
     * 我的消息列表
     *
     * @return
     */
    @GET("trade/notice/getList")
    Observable<ResultBean<MessageList>> messageList(@QueryMap Map<String, Object> account);
    /**
     * 我的消息列表
     *
     * @return
     */
    @GET("trade/notice/get")
    Observable<ResultBean<MessageList.Item>> messageDetail(@QueryMap Map<String, Object> account);

}
