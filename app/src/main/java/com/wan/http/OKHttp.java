package com.wan.http;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Author wan
 * Created on 2017/6/16 0016.
 */

public class OKHttp {
    private static OkHttpClient okHttpClient;    //单例的OkHttpClient设置

    private OKHttp() {
        throw new AssertionError();
    }

    static {
        okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    //实际app中自己设置默认需要的 在application中设置
    public static void setDefaultClient(OkHttpClient client) {
        okHttpClient = client;
    }

    //获取相同配置（链接池等）新的builder,来配置特殊需求
    public static OkHttpClient.Builder getShareBuilder() {
        return okHttpClient.newBuilder();
    }

    public static Request getGetRequest(String url, CacheControl cacheControl) {
        return getRequest(url, null, cacheControl);
    }

    public static Request getPostRequest(String url, RequestBody body) {
        return getRequest(url, body, null);
    }

    private static Request getRequest(String url, RequestBody builder, CacheControl cacheControl) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (builder != null) {
            requestBuilder = requestBuilder.post(builder);
        }
        if (cacheControl != null) {
            requestBuilder = requestBuilder.cacheControl(cacheControl);
        }
        return requestBuilder.build();
    }

    //这样，所有的Call就可以用一个httpclient
    public static Call getCall(Request request) {
        return getCall(request, null);
    }

    public static Call getCall(Request request, OkHttpClient client) {
        return client == null ? okHttpClient.newCall(request) : client.newCall(request);
    }
}
