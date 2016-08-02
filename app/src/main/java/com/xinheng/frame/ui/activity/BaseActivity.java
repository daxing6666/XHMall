package com.xinheng.frame.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import com.xinheng.R;
import com.xinheng.frame.logic.BaseOkHttpLogic;
import com.xinheng.frame.model.InfoResult;
import com.xinheng.ui.user.ActivityLogin;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import de.greenrobot.event.EventBus;

/**
 * [description about this class]
 * 基类
 * @author jack
 */
public abstract class BaseActivity extends AppCompatActivity {

    public Toast toast = null;
    private List<BaseOkHttpLogic> logics = new ArrayList<BaseOkHttpLogic>(); //存储BaseLogic
    private boolean isDestroyed; // Activity是否已销毁
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResId());//把设置布局文件的操作交给继承的子类
        ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View parentView = contentFrameLayout.getChildAt(0);
        if (parentView != null && Build.VERSION.SDK_INT >= 14) {
            parentView.setFitsSystemWindows(true);
        }
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
        isDestroyed = true;
        for(BaseOkHttpLogic logic : logics) {
            unregister(logic);
        }
    }

    /**
     * 注册BaseLogic, Activity销毁时自动取消解绑
     * @param logic
     * @param <T>
     * @return
     */
    protected <T extends BaseOkHttpLogic> T registerLogic(BaseOkHttpLogic logic) {
        logics.add(logic);
        return (T)logic;
    }

    /**
     * 解绑当前订阅者
     * @param iLogics
     */
    protected void unregister(BaseOkHttpLogic... iLogics) {
        for(BaseOkHttpLogic iLogic : iLogics) {
            if (iLogic != null) {
                iLogic.cancelAll();
                iLogic.unregister(this);
            }
        }
    }

    /**
     * EventBus订阅者事件通知的函数, UI线程
     *
     * @param msg
     */
    public void onEventMainThread(Message msg)
    {
        if (!isDestroyed && !isFinishing()) {
            if (msg.obj instanceof InfoResult) {
                InfoResult infoResult = (InfoResult)msg.obj;
                if(infoResult.getStateResult().equals("-4")){
                    Intent intent = new Intent(this, ActivityLogin.class);
                    startActivity(intent);
                }else{
                    onResponse(msg);
                }

            }
        }
    }

    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * (就一般的应用而言只显示错误信息就可以了,所以此方法接口调用成功不显示提示,接口调用失败显示失败信息)
     * @param msg
     * @return
     */
    protected boolean checkResponse(Message msg){
        return checkResponse(msg,null,null,false);
    }
    /**
     * @param msg
     * @param successDesc  接口调用成功之后提示语
     * @param errorDesc    接口调用失败之后提示语
     * @param showErrorMsg 是否显示错误信息
     * @return
     */
    protected boolean checkResponse(Message msg, String successDesc, String errorDesc, boolean showErrorMsg){

        if(msg.obj instanceof InfoResult){

            InfoResult result = (InfoResult)msg.obj;
            if(result.isSuccess()){
                {
                    if(!TextUtils.isEmpty(successDesc)){
                        showToast(successDesc);
                    }
                    return true;
                }
            }else{
                if(showErrorMsg){
                    if(!TextUtils.isEmpty(errorDesc)){
                        showToast(errorDesc);
                    }else if(!TextUtils.isEmpty(result.getDesc())){
                        showToast(result.getDesc());
                    }else{
                        showToast(getString(R.string.requesting_failure));
                    }
                }
                return false;
            }
        }else{
            if(showErrorMsg){
                if(!TextUtils.isEmpty(errorDesc)){
                    showToast(errorDesc);
                }else{
                    showToast(getString(R.string.requesting_failure));
                }
            }
            return false;
        }
    }

    /**
     * 跳转到某个Activity
     * @param activity
     * @param targetActivity
     */
    public void switchTo(Activity activity,Class<? extends Activity> targetActivity){
        switchTo(activity, new Intent(activity, targetActivity));
    }

    public void switchTo(Activity activity,Intent intent){
        activity.startActivity(intent);
    }

    /**
     * 跳转到某个Activity携带参数
     * @param activity
     * @param targetActivity
     * @param params
     */
    public void switchTo(Activity activity,Class<? extends Activity> targetActivity,Map<String,Object> params){
        if( null != params ){
            Intent intent = new Intent(activity,targetActivity);
            for(Map.Entry<String, Object> entry : params.entrySet()){
                setValueToIntent(intent, entry.getKey(), entry.getValue());
            }
            switchTo(activity, intent);
        }
    }

    public void setValueToIntent(Intent intent, String key, Object val) {
        if (val instanceof Boolean)
            intent.putExtra(key, (Boolean) val);
        else if (val instanceof Boolean[])
            intent.putExtra(key, (Boolean[]) val);
        else if (val instanceof String)
            intent.putExtra(key, (String) val);
        else if (val instanceof String[])
            intent.putExtra(key, (String[]) val);
        else if (val instanceof Integer)
            intent.putExtra(key, (Integer) val);
        else if (val instanceof Integer[])
            intent.putExtra(key, (Integer[]) val);
        else if (val instanceof Long)
            intent.putExtra(key, (Long) val);
        else if (val instanceof Long[])
            intent.putExtra(key, (Long[]) val);
        else if (val instanceof Double)
            intent.putExtra(key, (Double) val);
        else if (val instanceof Double[])
            intent.putExtra(key, (Double[]) val);
        else if (val instanceof Float)
            intent.putExtra(key, (Float) val);
        else if (val instanceof Float[])
            intent.putExtra(key, (Float[]) val);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * 返回当前Activity布局文件的id
     * @return
     */
    public abstract int getLayoutResId();
    public abstract void onResponse(Message msg);
}
