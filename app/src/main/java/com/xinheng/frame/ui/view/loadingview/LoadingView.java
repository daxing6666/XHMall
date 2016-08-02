package com.xinheng.frame.ui.view.loadingview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.xinheng.R;

/**
 * [description about this class]
 * 加载数据页面
 * @author jack
 */

public class LoadingView extends FrameLayout {

    private RelativeLayout rlLoading;//正在加载页面
    private RelativeLayout rlNoNet;//没有网络页面
    private RelativeLayout rlError;//加载失败页面

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        rlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        rlNoNet = (RelativeLayout) findViewById(R.id.rl_no_net);
        rlError = (RelativeLayout) findViewById(R.id.rl_error);
    }

    private void showLoading(){
        rlLoading.setVisibility(View.VISIBLE);
        rlNoNet.setVisibility(View.INVISIBLE);
        rlError.setVisibility(View.INVISIBLE);
    }

    private void showErrorLoading(){
        rlLoading.setVisibility(View.INVISIBLE);
        rlNoNet.setVisibility(View.INVISIBLE);
        rlError.setVisibility(View.VISIBLE);
    }

    private void showNoNetLoading(){
        rlLoading.setVisibility(View.INVISIBLE);
        rlNoNet.setVisibility(View.VISIBLE);
        rlError.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(){
        setVisibility(View.GONE);
    }
}
