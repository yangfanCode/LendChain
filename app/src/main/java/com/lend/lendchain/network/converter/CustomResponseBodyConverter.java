package com.lend.lendchain.network.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.lend.lendchain.bean.Code;
import com.lend.lendchain.utils.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by yangfan on 17/04/24.
 */
final class CustomResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    CustomResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String response = value.string();
//            String response = CommonUtil2.fromtoJson(value.string());
            LogUtils.LogE(CustomResponseBodyConverter.class, response);
            //判断登录失效 会有类型不对的错误所以截取json只留code 返回resultbean处理 只针对2002登录失效处理
            Code code = new Gson().fromJson(response, Code.class);
            if (code != null) {
                if ("2002".equals(code.code)) {
                    response="{\"code\":2002}";
                }
            }
            MediaType contentType = value.contentType();
            Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
            InputStream inputStream = new ByteArrayInputStream(response.getBytes());
            Reader reader = new InputStreamReader(inputStream, charset);
            JsonReader jsonReader = gson.newJsonReader(reader);

            return adapter.read(jsonReader);
        } catch (Exception e) {
            LogUtils.LogE(CustomResponseBodyConverter.class, e.getMessage());
            return null;
        } finally {
            value.close();
        }
    }
}