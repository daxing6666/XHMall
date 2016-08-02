package com.xinheng.ui.user;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import com.xinheng.R;
import com.xinheng.frame.ui.activity.BaseActivity;
import com.xinheng.frame.ui.view.rippleview.RippleView;
import com.xinheng.logic.user.logic.UserLogic;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * [description about this class]
 * 登录
 * @author jack
 * @date 2016/7/14 11:16
 */
public class ActivityLogin extends BaseActivity {

    @Bind(R.id.btnLogin)
    Button btnLogin;
    @Bind(R.id.rippleview)
    RippleView rippleView;
    private UserLogic userLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }

    private void init() {
        ButterKnife.bind(this);
        userLogic = new UserLogic(this);
        registerLogic(userLogic);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                showToast("jack");
                //userLogic.userlogin(R.id.userlogin, "15850217017", "a111111");

                AnimationSet animationSet = new AnimationSet(true);
                AlphaAnimation alpha = new AlphaAnimation(1,0);
                alpha.setDuration(2000);
                alpha.setDetachWallpaper(true);
                alpha.setFillAfter(true);
                animationSet.setInterpolator(new DecelerateInterpolator());
                animationSet.addAnimation(alpha);
                btnLogin.startAnimation(animationSet);
            }
        });
    }

    /**
     * 点击事件
     * @param view
     */
    @OnClick({R.id.btnLogin})
    public void OnClick(View view) {
        switch (view.getId()) {
            //登录
            case R.id.btnLogin:
                break;
            default:break;
        }
    }

    @Override
    public void onResponse(Message msg) {

    }

}
