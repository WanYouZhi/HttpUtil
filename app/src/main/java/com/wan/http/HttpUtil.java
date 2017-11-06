package com.wan.http;

import android.app.Activity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Author wan
 * post 和get返回的都是主线程
 * Created on 2017/6/16 0016.
 */

public class HttpUtil {
    private static final Set<HttpBuilder> builders = new HashSet<>();//保存未返回的builder，留remove用

    private HttpUtil() {

    }

    public static HttpBuilder newBuilder(Activity activity) {
        return new HttpBuilder(activity);
    }

    public static class HttpBuilder {
        private WeakReference<Activity> activity;
        private String url;
        private Map<String, String> params = new HashMap<>();
        private OkHttpClient client;

        private HttpBuilder(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        public HttpBuilder url(String url) {
            this.url = url;
            return this;
        }

        public HttpBuilder addParam(String key, String value) {
            params.put(key, value);
            return this;
        }

        public HttpBuilder addParam(Map<String, String> map) {
            params.putAll(map);
            return this;
        }

        //用 Okhttp.getShareBuilder来获取相同链接池的client
        public HttpBuilder changeClient(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public <T> HttpBuilder post(HttpResult<T> callBack) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> item : params.entrySet()) {
                if (item.getKey() != null && item.getValue() != null)
                    builder.add(item.getKey(), item.getValue());
            }
            Call call = OKHttp.getCall(OKHttp.getPostRequest(url, builder.build()), client);
            enqueueCall(call, callBack);
            return this;
        }


        public <T> HttpBuilder get(HttpResult<T> callBack) {
            get(null, callBack);
            return this;
        }

        public <T> HttpBuilder get(CacheControl control, HttpResult<T> callBack) {
            String callUrl = getUrl(url, params);
            Call call = OKHttp.getCall(OKHttp.getGetRequest(callUrl, control), client);
            enqueueCall(call, callBack);
            return this;
        }

        Call call;

        private <T> void enqueueCall(Call call, final HttpResult<T> callBack) {
            this.call = call;
            call.enqueue(new JsonCallBack<T>() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    ThreadUtil.mainThread(new Runnable() {
                        @Override
                        public void run() { //回到主线程
                            callBack.onFailed(e.getMessage());
                        }
                    });
                    remove(HttpBuilder.this);//没网或无返回时去除
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!call.isCanceled() && builders.contains(HttpBuilder.this)) {
                        mType = callBack.mType; //重置mType 为了 List<testInfo>形式
                        super.onResponse(call, response);
                    }
                    remove(HttpBuilder.this);
                }

                @Override
                public void onSuccess(final T obj) {
                    ThreadUtil.mainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(obj);//保证不再线程切换时remove
                        }
                    });
                }
            });
            builders.add(this);
        }
    }


    public static String getUrl(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> item : params.entrySet()) {
            urlBuilder.addQueryParameter(item.getKey(), item.getValue());
        }
        return urlBuilder.build().toString();
    }

    public static void remove(Activity activity) {
        if (activity == null)
            return;
        List<HttpBuilder> removeBuilders = new ArrayList<>();//防止ConcurrentModification
        for (HttpBuilder builder : builders) {
            if (activity == builder.activity.get()) {
                removeBuilders.add(builder);
            }
        }
        for (HttpBuilder builder : removeBuilders) {
            remove(builder);
        }
    }

    public static void remove(HttpBuilder builder) {
        if (builder.call != null)
            builder.call.cancel();
        builders.remove(builder);
    }

    public abstract static class HttpResult<T> {
        Type mType;

        protected HttpResult() {
            mType = JsonUtil.getSuperclassTypeParameter(getClass());
        }

        public abstract void onSuccess(T obj);

        public void onFailed(String msg) {

        }
    }
}
