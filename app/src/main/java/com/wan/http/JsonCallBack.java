package com.wan.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author wan
 * 1.返回json转成的对象
 * Created on 2017/6/17 0017.
 */

public abstract class JsonCallBack<T> implements Callback {
    Type mType;

    public JsonCallBack() {
        mType = JsonUtil.getSuperclassTypeParameter(getClass());
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        String msg = "获取服务器返回失败";
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            String bodyString = body == null ? "" : body.string().trim();
            if (bodyString.startsWith("{") || bodyString.startsWith("[")) {
                try {
                    Object a = new Gson().fromJson(bodyString, mType);
                    onSuccess((T) a);
                    return;
                } catch (Exception e) {
                    msg = "Json转换失败";
                }
            } else {
                msg = "返回格式非json";
            }
        }
        onFailure(call, new IOException(msg));
    }

    public abstract void onSuccess(T obj);
}
