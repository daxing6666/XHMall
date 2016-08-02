package com.xinheng.util;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import java.util.List;

/**
 * 使用Gson 统一处理json字符串和对象之间的转化
 * FastJson速度最快，fastjson具有极快的性能，超越任其他的Java Json parser。
   FastJson功能强大，完全支持Java Bean、集合、Map、日期、Enum，支持范型，支持自省；无依赖。
   Fastjson API入口类是com.alibaba.fastjson.JSON，常用的序列化操作都可以在JSON类上的静态方法直接完成。
 * Created by jack
 */
public class GsonUtils {

    private final static GsonUtils instance = new GsonUtils();
    private static final String TAG = "GsonUtils";

    /**
     * 单例对象实例
     */
    public static GsonUtils getInstance(){
        return instance;
    }

    /**
     * 将json字符串转化为一个对象
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public <T> T jsonToClass(String json, Class<T> classOfT)
    {
        T t = null;
        try
        {
            t= JSON.parseObject(json, classOfT);
        }
        catch (Exception e) {
            Log.d(TAG, "json to class【" + classOfT + "】 解析失败  " + e.getMessage());
        }
        Log.d(TAG, "\nJSON 数据 【" + json + " 】");
        return t;
    }

    /**
     * 将json字符串转化为一个对象列表
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public <T> List<T> jsonToList(String json,  Class<T> classOfT){
        return JSON.parseArray(json, classOfT);
    }

    /**
     * 将一个对象转化成json字符串
     * @param object
     * @return
     */
    public String toJson(Object object)
    {
        return JSON.toJSONString(object);
    }
}
