package com.xinheng.frame.interfaces;

/**
 * 批量任务接口(数据库事物安全操作)
 */

public interface ITransactionListener {
    void doTransaction();
}
