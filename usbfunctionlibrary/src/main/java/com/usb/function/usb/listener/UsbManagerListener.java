package com.usb.function.usb.listener;

/**
 * @Author: Tian_SiQiang
 * @CreationTime： 2018/12/25 10:18
 * @Introduction：U盘事件监听
 */
public interface UsbManagerListener {


    /**
     * U盘处理逻辑加载完成
     * @param type 0、配置网络参数 1、版本更新
     * @param msg
     */
    void successListener(int type, String msg);

    /**
     * U盘加载过程中的各种状态 参考UsbConstant.java文件
     * @param code
     * @param msg
     */
    void loadingListener(int code, String msg);

    /**
     * u盘操作失败
     * @param msg
     */
    void fallListener(String msg);
}
