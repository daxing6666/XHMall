package com.xinheng.frame.ui.view.refresh;
public interface MaterialHeadListener {

    void onComlete(MaterialRefreshLayout materialRefreshLayout);

    void onBegin(MaterialRefreshLayout materialRefreshLayout);

    void onPull(MaterialRefreshLayout materialRefreshLayout, float fraction);

    void onRelease(MaterialRefreshLayout materialRefreshLayout, float fraction);

    void onRefreshing(MaterialRefreshLayout materialRefreshLayout);
}
