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
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            String bodyString = body == null ? "" : body.string().trim();
            try {
                Object a = bodyString.startsWith("{") || bodyString.startsWith("[") ? new Gson().fromJson(bodyString, mType) : bodyString;
                onSuccess((T) a);
            } catch (Exception e) {
                onFailure(call, new IOException("Json转换失败"));
            }
        } else {
            onFailure(call, new IOException("网络开小差了，请检查网络！"));
        }
    }

    public abstract void onSuccess(T obj);
}
