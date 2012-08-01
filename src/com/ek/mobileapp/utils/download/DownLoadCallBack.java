package com.ek.mobileapp.utils.download;

public interface DownLoadCallBack
{
    /**
     * 更新进度条
     */
    void UpdateUIProgress(int cur);

    /**
     * 设置百分比
     * @param percent 当前下载的百分比
     */
    void ReturnPercent(String percent);

    /**
     * 设置进度条最大值
     * @param val
     */
    void SetMaxProgress(int val);

    /**
     * 下载完成
     */
    void Success();

    /**
     * 下载出错
     */
    void Error();
}