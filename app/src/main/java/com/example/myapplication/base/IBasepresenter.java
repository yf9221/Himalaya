package com.example.myapplication.base;

public interface IBasepresenter<T> {
    /**
     * 注册UI通知接口
     * @param t
     */
    void registerViewCallback(T t);

    /**
     * 删除UI通知的接口
     * @param t
     */
    void unRegisterViewCallback(T t);
}


