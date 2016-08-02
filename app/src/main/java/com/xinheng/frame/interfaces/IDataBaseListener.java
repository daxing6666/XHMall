package com.xinheng.frame.interfaces;

/**
 * 数据库在子线程CRUD之后,如果任需在此子线程做其他的操作,我们就可以通过此接口
 * 回调实现。主要作用:不要在主UI线程做过多的耗时操作
 */

public interface IDataBaseListener<T>
{
    void onSQLResult(T response, int tag);
}
