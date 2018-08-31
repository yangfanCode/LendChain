package com.lend.lendchain.bean;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */


public class ResultBean<T> {
//    {
//        "message": "success",
//            "code": 2000,
//            "data": {
//        "country_code": "86",
//                "totalAmount": 1,
//                "phone": "2147483647",
//                "nickname": "",
//                "identif": {
//            "phone": "2147483647",
//                    "google": 0
//        },
//        "lastAmount": 1,
//                "profit": 1,
//                "email": "1****4@qq.com"
//    }
//    }

    public String code;
    public String message;
    public boolean success;
    public T data;

    // 登录数据 数据时使用
    public String access_token;
    public String token_type;
    public String refresh_token;
    public int expires_in;


    //是否请求成功
    public boolean isSuccess(){
        return "2000".equals(code)||"ok".equals(code);
    }
}
