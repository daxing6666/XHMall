package com.xinheng.util;


import android.os.Environment;
import com.xinheng.util.ApkUtils;
import java.io.File;

/**
 * [description about this class]
 * 整个apk静态变量的设置
 * @author jack
 */
public class Constants {

    //=============================缓存数据=================================
    public static int CACHE_SIZE = 10 * 1024 * 1024;//10MB
    //=============================文件操作相关==============================
    /**
     *整个应用程序文件夹创建的根目录位置(直接暴露在手机目录下,用户很容易可见)
     */
    public static String ROOT_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator +
            ApkUtils.getInstance().getApkName();
    /**
     *整个应用程序文件夹创建的缓存根目录位置(直接暴露在手机目录下,用户不易可见)
     * /storage/emulated/0/Android/data/包名/cache/包名
     */
    public static String CACHE_ROOT_FILE_PATH = ApkUtils.getInstance().getDiskCacheDirPath()+ File.separator +
            ApkUtils.getInstance().getApkPackageName();
    //=============================项目配置=================================
    /**
     * 自动登录
     */
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String AUTO_LOGIN = "auto_login";


    /***
     * http请求中添加的header key
     */
    public static final String SESSION_ID = "sessionId";
    /**
     * HTTP header中的cookie值KEY
     */
    public static final String COOKIE = "cookie";
    /***
     * http请求中添加的header key
     */
    public static final String USER_AGENT_KEY = "user-agent";
    /***
     * HTTP头部写的user_agent的值
     */
    public static final String USER_AGENT_VALUE = "UA_HKWY";
    /**
     * HTTP header中的cookie值VALUE的前缀
     */
    public static final String SID = "sid=";
    /***
     * http请求中添加的header key
     */
    public static final String USER_ID = "userId";


    /***
     * 字符串使用到的分隔符
     */
    public static final String SPLIT = "~";
    public static final String BASE_API_URL = "http://api.xinhengkeji.com";//外网服务器
    //    public static final String BASE_API_URL  =  "http://192.168.1.57";// 开发者服务器
    public static String USER_LOGIN = BASE_API_URL + "/public/patient/user/login";
}
