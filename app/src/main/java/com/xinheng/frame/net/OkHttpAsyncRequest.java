package com.xinheng.frame.net;

import android.content.Context;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.xinheng.AppDroid;
import com.xinheng.R;
import com.xinheng.frame.interfaces.ILogic;
import com.xinheng.frame.interfaces.IresponseParserListener;
import com.xinheng.frame.model.InfoResult;
import com.xinheng.logic.user.model.UserInfo;
import com.xinheng.logic.user.model.post.UserPostBean;
import com.xinheng.util.Constants;
import com.xinheng.util.GsonUtils;
import com.xinheng.util.MD5;
import com.xinheng.util.NetworkUtils;
import com.xinheng.util.RSAUtil;
import com.xinheng.util.SPDBHelper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * [description about this class]
 * okhttp一般异步请求
 * @author jack
 */
public class OkHttpAsyncRequest {

    private MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private Context context = AppDroid.getInstance().getApplicationContext();
    private int readTimeOut = 20;//数据读取超时时间，默认为30s
    private int writeTimeOut = 15; //写超时时间，默认为15s
    private int connectTimeOut = 60; //连接超时时间，默认为25s
    private OkHttpClient okHttpClient;
    private String error_msg = "";
    private String no_net_msg = "";
    private String back_login_msg = "";
    private InfoResult infoResult;//把网络请求结果封装成 infoResult
    private IresponseParserListener parserListener;//回调业务层做解析Bean封装
    private ILogic logic;//分发解析好的数据到业务层
    private Request request = null;
    public int tag;//请求的tag标识
    public String url;//请求的根地址

    public OkHttpAsyncRequest(int tag, String url){

    }

    public OkHttpAsyncRequest(int tag, String url, IresponseParserListener parserListener, ILogic logic){
        super();
        error_msg = "{\"result\": -1,\"message\": \""
                + context.getString(R.string.requesting_failure)+ "\",\"propertise\": {}}";
        no_net_msg = "{\"result\": -2,\"message\": \""
                + context.getString(R.string.network_unavailable)+ "\",\"propertise\": {}}";
        back_login_msg = "{\"result\": -4,\"message\": \""
                + context.getString(R.string.requesting_failure)+ "\",\"propertise\": {}}";
        this.tag = tag;
        this.url = url;
        this.parserListener = parserListener;
        this.logic = logic;
        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().connectTimeout(connectTimeOut, TimeUnit.SECONDS);
        okHttpClient.newBuilder().readTimeout(readTimeOut, TimeUnit.SECONDS);
        okHttpClient.newBuilder().writeTimeout(writeTimeOut, TimeUnit.SECONDS);
    }

    /**
     * POST提交Json数据
     * @param url 请求地址
     * @param json 请求body体
     * @param flagHeader 是否设置请求头
     * @return
     */
    public void getResultForPost(final String url, final String json, final boolean flagHeader){
        infoResult = null;
        final IresponseParserListener parserListener = this.parserListener;
        final Context context = AppDroid.getInstance().getApplicationContext();
        if (NetworkUtils.getInstance().isNetworkConnected(context)){
            if(checkHttpAndHttpsStart(url)){
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
                /**
                 * 如果有请求头在这里进行设置
                 */
                if(flagHeader){
                    HashMap<String, String> headerMap = new HashMap<>();
                    if(AppDroid.getInstance().getUserInfo()!= null){
                        headerMap.put(Constants.SESSION_ID, AppDroid.getInstance().getUserInfo().getSessionId());
                        headerMap.put(Constants.USER_AGENT_KEY, Constants.USER_AGENT_VALUE);
                        headerMap.put(Constants.COOKIE, Constants.SID + AppDroid.getInstance().getUserInfo().getSessionId());
                        //userId要客户端加密
                        String encryptUserId = RSAUtil.clientEncrypt(AppDroid.getInstance().getUserInfo().getId());
                        headerMap.put(Constants.USER_ID, encryptUserId);
                    }
                    Headers headers = Headers.of(headerMap);
                    request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .tag(tag)
                            .headers(headers)
                            .build();

                }else{
                    request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .tag(tag)
                            .build();
                }
                okHttpClient.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        try {
                            infoResult = parserListener.doParse(error_msg);
                            logic.onResult(tag, infoResult);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if (response.isSuccessful()) {
                            try {
                                String result = response.body().string();
                                if(!TextUtils.isEmpty(result)){
                                    result = RSAUtil.clientDecrypt(result);
                                }
                                infoResult = parserListener.doParse(result);
                                /**
                                 * session过期
                                 */
                                if(infoResult.getStateResult().equals("-4")) {
                                    /**
                                     * 此种情况一般不可能出现的,这里判断有点多余,万一 ....
                                     */
                                    if(AppDroid.getInstance().getUserInfo()!=null){
                                        SPDBHelper spdbHelper = new SPDBHelper();
                                        boolean flag = spdbHelper.getBoolean(Constants.AUTO_LOGIN,false);
                                        if(flag){
                                            OkHttpClient client = new OkHttpClient();
                                            String account = spdbHelper.getString(Constants.ACCOUNT,"");
                                            String password = spdbHelper.getString(Constants.PASSWORD, "");
                                            password = new MD5().getMD5_32(password);
                                            UserPostBean userPostBeanu = new UserPostBean();
                                            userPostBeanu.setAccount(account);
                                            userPostBeanu.setPassword(password);
                                            String bodyMing = GsonUtils.getInstance().toJson(userPostBeanu);
                                            String bodyMi = RSAUtil.clientEncrypt(bodyMing);
                                            RequestBody bodReq = RequestBody.create(MediaType.parse("application/json"), bodyMi);
                                            Request request = new Request.Builder()
                                                    .url(Constants.USER_LOGIN)
                                                    .post(bodReq)
                                                    .build();
                                            client.newCall(request).enqueue(new Callback() {

                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    e.printStackTrace();
                                                    try {
                                                        //去登录界面的
                                                        infoResult = parserListener.doParse(back_login_msg);
                                                        logic.onResult(tag, infoResult);
                                                    } catch (Exception e1) {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    if (response.isSuccessful()) {
                                                        String result = response.body().string();
                                                        if(!TextUtils.isEmpty(result)){
                                                            result = RSAUtil.clientDecrypt(result);
                                                        }
                                                        JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(result);
                                                        InfoResult infoResult = new InfoResult();
                                                        infoResult.setSuccess(jsonObject.getString("result").equals("1"));
                                                        infoResult.setDesc(jsonObject.getString("message"));
                                                        infoResult.setStateResult(jsonObject.getString("result"));
                                                        JSONObject jsonObjectResult = jsonObject.getJSONObject("properties");
                                                        UserInfo userInfo = GsonUtils.getInstance().jsonToClass(jsonObjectResult.toString(),UserInfo.class);
                                                        AppDroid.getInstance().setUserInfo(userInfo);
                                                        getResultForPost(url,json,flagHeader);
                                                    }else{
                                                        //去登录界面的
                                                        try {
                                                            infoResult = parserListener.doParse(back_login_msg);
                                                            logic.onResult(tag, infoResult);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            });

                                        }else{
                                            //AppDroid中没有用户登录的信息
                                            //去登录界面的
                                            infoResult = parserListener.doParse(back_login_msg);
                                            logic.onResult(tag, infoResult);
                                        }
                                    }else{
                                        //AppDroid中没有用户登录的信息
                                        //去登录界面的
                                        infoResult = parserListener.doParse(back_login_msg);
                                        logic.onResult(tag, infoResult);
                                    }
                                }else{
                                    //没有过期,正常的网络数据返回
                                    logic.onResult(tag, infoResult);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                infoResult = parserListener.doParse(error_msg);
                                logic.onResult(tag, infoResult);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }else{
                try {
                    infoResult = parserListener.doParse(error_msg);
                    logic.onResult(tag, infoResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }else{
            try {
                infoResult = parserListener.doParse(no_net_msg);
                logic.onResult(tag, infoResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * POST提交Map数据和文件
     * @param url
     * @param postMap
     * @param files
     * @param flagHeader
     */
    public void getResultForPostWithFile(final String url, final Map<String, String> postMap, final List<File> files,final boolean flagHeader){
        infoResult = null;
        final IresponseParserListener parserListener = this.parserListener;
        final Context context = AppDroid.getInstance().getApplicationContext();
        if (NetworkUtils.getInstance().isNetworkConnected(context)){
            if(checkHttpAndHttpsStart(url)){
                RequestBody body = null;
                /**
                 * 如果有请求头在这里进行设置
                 */
                if(flagHeader){
                    HashMap<String, String> headerMap = new HashMap<>();
                    if(AppDroid.getInstance().getUserInfo()!= null){
                        headerMap.put(Constants.SESSION_ID, AppDroid.getInstance().getUserInfo().getSessionId());
                        headerMap.put(Constants.USER_AGENT_KEY, Constants.USER_AGENT_VALUE);
                        headerMap.put(Constants.COOKIE, Constants.SID + AppDroid.getInstance().getUserInfo().getSessionId());
                        //userId要客户端加密
                        String encryptUserId = RSAUtil.clientEncrypt(AppDroid.getInstance().getUserInfo().getId());
                        headerMap.put(Constants.USER_ID, encryptUserId);
                    }
                    Headers headers = Headers.of(headerMap);
                    MultipartBody.Builder multipartBodyBuilder= new  MultipartBody.Builder();
                    if (null != postMap) {
                        for (String key : postMap.keySet()) {
                            String value = postMap.get(key);
                            if (TextUtils.isEmpty(value)) {
                                value = "";
                            }
                            multipartBodyBuilder.addFormDataPart(key, value);
                        }
                    }
                    if(null != files){
                        for(File file : files){
                            multipartBodyBuilder.addFormDataPart("files", file.getName(),
                                    RequestBody.create(MEDIA_TYPE_PNG, file));
                        }
                    }
                    body = multipartBodyBuilder.build();
                    request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .tag(tag)
                            .headers(headers)
                            .build();


                }else{
                    MultipartBody.Builder multipartBodyBuilder= new  MultipartBody.Builder();
                    if (null != postMap) {
                        for (String key : postMap.keySet()) {
                            String value = postMap.get(key);
                            if (TextUtils.isEmpty(value)) {
                                value = "";
                            }
                            multipartBodyBuilder.addFormDataPart(key, value);
                        }
                    }
                    if(null != files){
                        for(File file : files){
                            multipartBodyBuilder.addFormDataPart("files", file.getName(),
                                    RequestBody.create(MEDIA_TYPE_PNG, file));
                        }
                    }
                    body = multipartBodyBuilder.build();
                    request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .tag(tag)
                            .build();
                }
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        try {
                            infoResult = parserListener.doParse(error_msg);
                            logic.onResult(tag, infoResult);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if (response.isSuccessful()) {
                            try {
                                String result = response.body().string();
                                if(!TextUtils.isEmpty(result)){
                                    result = RSAUtil.clientDecrypt(result);
                                }
                                infoResult = parserListener.doParse(result);

                                /**
                                 * session过期
                                 */
                                if(infoResult.getStateResult().equals("-4")){
                                    /**
                                     * 此种情况一般不可能出现的,这里判断有点多余,万一 ....
                                     */
                                    if(AppDroid.getInstance().getUserInfo()!=null){
                                        SPDBHelper spdbHelper = new SPDBHelper();
                                        boolean flag = spdbHelper.getBoolean(Constants.AUTO_LOGIN,false);
                                        if(flag){
                                            OkHttpClient client = new OkHttpClient();
                                            String account = spdbHelper.getString(Constants.ACCOUNT,"");
                                            String password = spdbHelper.getString(Constants.PASSWORD, "");
                                            password = new MD5().getMD5_32(password);
                                            UserPostBean baseBean = new UserPostBean();
                                            baseBean.setAccount(account);
                                            baseBean.setPassword(password);
                                            String bodyMing = GsonUtils.getInstance().toJson(baseBean);
                                            String bodyMi = RSAUtil.clientEncrypt(bodyMing);
                                            RequestBody bodReq = RequestBody.create(MediaType.parse("application/json"), bodyMi);
                                            Request request = new Request.Builder()
                                                    .url(Constants.USER_LOGIN)
                                                    .post(bodReq)
                                                    .build();
                                            client.newCall(request).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    e.printStackTrace();
                                                    //去登录界面的
                                                    try {
                                                        infoResult = parserListener.doParse(back_login_msg);
                                                    } catch (Exception e1) {
                                                        e1.printStackTrace();
                                                    }
                                                    logic.onResult(tag, infoResult);
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    if (response.isSuccessful()) {
                                                        String result = response.body().string();
                                                        if(!TextUtils.isEmpty(result)){
                                                            result = RSAUtil.clientDecrypt(result);
                                                        }
                                                        JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(result);
                                                        InfoResult infoResult = new InfoResult();
                                                        infoResult.setSuccess(jsonObject.getString("result").equals("1"));
                                                        infoResult.setDesc(jsonObject.getString("message"));
                                                        infoResult.setStateResult(jsonObject.getString("result"));
                                                        JSONObject jsonObjectResult = jsonObject.getJSONObject("properties");
                                                        UserInfo userInfo = GsonUtils.getInstance().jsonToClass(jsonObjectResult.toString(),UserInfo.class);
                                                        AppDroid.getInstance().setUserInfo(userInfo);
                                                        getResultForPostWithFile(url, postMap,files, flagHeader);
                                                    }
                                                }
                                            });


                                        }else{
                                            //AppDroid中没有用户登录的信息
                                            //去登录界面的
                                            infoResult = parserListener.doParse(back_login_msg);
                                            logic.onResult(tag, infoResult);
                                        }
                                    }else{
                                        //AppDroid中没有用户登录的信息
                                        //去登录界面的
                                        infoResult = parserListener.doParse(back_login_msg);
                                        logic.onResult(tag, infoResult);
                                    }
                                }else{
                                    //正常的网络数据返回
                                    logic.onResult(tag, infoResult);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                infoResult = parserListener.doParse(error_msg);
                                logic.onResult(tag, infoResult);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }else{
                try {
                    infoResult = parserListener.doParse(no_net_msg);
                    logic.onResult(tag, infoResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        /**
         * 没有网络的情况下
         */
        else{
            try {
                infoResult = parserListener.doParse("{\"result\": -1,\"message\": \""
                        + context.getString(R.string.network_unavailable)+ "\",\"propertise\": {}}");
                infoResult.setStateResult("-2");
                logic.onResult(tag, infoResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检测url地址是否是以http或者https开头的
     * @param url
     * @return
     */
    public boolean checkHttpAndHttpsStart(String url){

        boolean flag = false;
        if(TextUtils.isEmpty(url)){
            return false;
        }else{
            Pattern pattern2 = Pattern
                    .compile("(http|ftp|https):\\/\\/([\\w.]+\\/?)\\S*");
            Matcher matcher2 = pattern2.matcher(url);
            return matcher2.find();
        }
    }
}
