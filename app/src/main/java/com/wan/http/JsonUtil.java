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

    private static final Gson gson = new Gson();

    public static <T> T fromGenericJson(String json, Class<T> obj) {
        try {
            return (T) gson.fromJson(json, getSubclassGeneric(obj));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param subclass 只有子类才能拿到泛型信息，一般使用匿名内部类
     * @return
     */
    public static Type getSubclassGeneric(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            //直接用父类拿到的是Object，这里抛出异常
            throw new JsonSyntaxException("Missing type parameter.");
        }
        ParameterizedType param = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(param.getActualTypeArguments()[0]);
    }


    /**
     * List 的java转换
     *
     * @param str
     * @param obj
     * @return
     */
    public static <T> List<T> listConvert(String str, Class<T> obj) {
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, obj);
        return gson.fromJson(str, type);
    }

    public static <T> String toJson(T obj) {
        return gson.toJson(obj);
    }
}
