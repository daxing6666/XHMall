package com.xinheng.frame.logic;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.xinheng.frame.interfaces.ILogic;
import com.xinheng.frame.net.OkHttpAsyncRequest;
import com.xinheng.util.RSAUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import de.greenrobot.event.EventBus;

/**
 * [description about this class]
 * 1:使用EventBus通讯订阅者与发送者
 * 2:每一个BaseOkHttpLogic对象默认情况下使用不同的EventBus,
 *   保证同一种类型的事件不会发送给多个订阅者(EventBus默认情况会发送个多个订阅者)
 *   这样会出现以下情况
 *   1、msg的what(自己定义的区分标示)相同的情况会被多个订阅者接受并处理
 *   2、msg的what(自己定义的区分标示)不相同的话会被多个订阅者接受, 但不会被处理(具体需要业务层控制)
 * @author jack
 */

public class BaseOkHttpLogic implements ILogic
{
    // 存储所有的订阅者
    private List<Object> subscribers = new ArrayList<Object>();
    // Default EventBus
    private EventBus mEventBus;
    private static Object lock = new Object(); // 锁对象

    public BaseOkHttpLogic(Object subscriber) {
        this(subscriber, new EventBus());
    }

    /**
     * Constructor with custom EventBus
     * @param eventBus
     */
    public BaseOkHttpLogic(Object subscriber, EventBus eventBus) {
        if (eventBus == null) {
            mEventBus = EventBus.getDefault();
        } else {
            mEventBus = eventBus;
        }
        register(subscriber);
    }

    @Override
    public void register(Object subscriber) {
        if (!subscribers.contains(subscriber)) {
            mEventBus.register(subscriber);
            subscribers.add(subscriber);
        }
    }

    @Override
    public void unregister(Object subscriber) {
        if (subscribers.contains(subscriber)) {
            mEventBus.unregister(subscriber);
            subscribers.remove(subscriber);
        }
    }

    @Override
    public void unregisterAll() {
        for (Object subscriber : subscribers) {
            mEventBus.unregister(subscriber);
        }
        subscribers.clear();
    }

    @Override
    public void cancel(Object tag) {

    }

    @Override
    public void cancelAll() {

    }

    /**
     * 使用自定义发送网络请求
     * @param request
     */
    protected void sendRequestForPost(OkHttpAsyncRequest request,String url, String json,boolean flagHeader) {
        synchronized (lock) {
            request.getResultForPost(url,json,flagHeader);
        }
    }

    /**
     * 使用自定义发送网络请求(包含文件)
     * @param request
     * @param url
     * @param postMap
     * @param files
     * @param flagHeader
     */
    protected void sendRequestForPostWithFile(OkHttpAsyncRequest request,String url,Map<String, String> postMap, List<File> files,boolean flagHeader) {
        synchronized (lock) {
            request.getResultForPostWithFile(url, postMap, files, flagHeader);
        }
    }


    /**
     * 负责封装结果内容, post给订阅者
     * @param action 任务标识
     * @param response 响应结果
     */
    @Override
    public void onResult(int action, Object response) {
        Message msg = new Message();
        msg.what = action;
        msg.obj = response;
        mEventBus.post(msg);
    }

    /**
     * 生成请求的body体
     * @param map
     * @param listMap
     * @return
     */
    public String toBody(Map<String, String> map,Map<String, ArrayList<String>> listMap){
        String body = "";
        StringBuffer buffer= new StringBuffer();
        buffer.append("{");
        if(map != null && map.size()>0){
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                buffer.append("\""+key+"\"");
                buffer.append(":");
                buffer.append("\""+val+"\"");
                buffer.append(",");
            }
        }
        if(listMap != null && listMap.size()>0)
        {
            Iterator iter = listMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                ArrayList<String> list = (ArrayList<String>) entry.getValue();
                buffer.append("\""+key+"\"");
                buffer.append(":");
                buffer.append("[");
                if(list != null && list.size() > 0){
                    for(int i = 0; i < list.size(); i ++){
                        buffer.append("\""+list.get(i)+"\"");
                        if(i!=list.size()-1){
                            buffer.append(",");
                        }
                    }
                    buffer.append("]");
                }else{
                    buffer.append("]");
                }
                buffer.append(",");
            }
            body = buffer.toString().substring(0,buffer.toString().length()-1) + "}";
        }else{
            if(buffer.toString().length() == 1){

                if(!TextUtils.isEmpty(body)){
                    return  RSAUtil.clientEncrypt(body);
                }
                return body;
            }else{
                body = buffer.toString().substring(0,buffer.toString().length()-1) + "}";
            }
        }
        if(!TextUtils.isEmpty(body)){
            Log.e("body", "" + body);
            return  RSAUtil.clientEncrypt(body);
        }
        return body;
    }
}
