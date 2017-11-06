package com.wan.http;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Author wan
 * Created on 2017/6/16 0016.
 */

public class JsonUtil {

    public static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType param = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(param.getActualTypeArguments()[0]);
    }

    //list的json转换
    public static <T> List<T> jsonCovert(String str, Class<T> obj) {
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, obj);
        return new Gson().fromJson(str, type);
    }
}
