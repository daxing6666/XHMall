package com.xinheng;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Interpolator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity2 extends AppCompatActivity {

    private Button button;
    private ImageView imageView;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_main2);
        button = (Button) findViewById(R.id.btn);
        imageView = (ImageView) findViewById(R.id.image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ObjectAnimator.ofFloat(imageView, "translationY", 0.0F, 200.0F)
//                        // 设置执行时间(1000ms)
//                        .setDuration(1000)
//                                // 开始动画
//                        .start();
                ObjectAnimator yBouncer = ObjectAnimator.ofFloat(imageView, "y",
                        imageView.getY(), 600.0f);
                yBouncer.setDuration(1500);
                // 设置插值器(用于调节动画执行过程的速度)
                yBouncer.setInterpolator(new BounceInterpolator());
                // 设置重复次数(缺省为0,表示不重复执行)
                yBouncer.setRepeatCount(1);
                // 设置重复模式(RESTART或REVERSE),重复次数大于0或INFINITE生效
                yBouncer.setRepeatMode(ValueAnimator.REVERSE);
                // 设置动画开始的延时时间(200ms)
                yBouncer.setStartDelay(200);
                // 开始动画
                yBouncer.start();
           }
        });

    }
}
