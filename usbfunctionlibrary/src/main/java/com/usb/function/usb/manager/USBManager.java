package com.usb.function.usb.manager;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import com.usb.function.usb.listener.UsbManagerListener;
import com.usb.function.version.manager.VersionUpgradeManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.usb.function.usb.utlis.UsbConstant.USBINSERTSUCCESSCODE;
import static com.usb.function.usb.utlis.UsbConstant.USBINSERTSUCCESSMSG;


/**
 * @Author: Tian_SiQiang
 * @CreationTime： 2019/1/24 13:54
 * @Introduction：U盘操作处理
 */
public class USBManager {

    public final static String TAG="USB";
    /**
     * 是否只获取U盘的一级目录
     */
    public final static boolean isRootDirectory = true;
    /**
     * U盘处理监听事件
     */
    private UsbManagerListener mUsbManagerListener;

    /**
     * U盘是否已经拔出
     */
    public boolean isUsbUnInsert = true;

    private Activity mContext;

    /**
     * U盘事件广播监听
     */
    private UsbBroadcastReceiver mFuctionBroadcastReceiver;


    private static volatile USBManager instance;

    private USBManager() {
        if (mFuctionBroadcastReceiver == null) {
            mFuctionBroadcastReceiver = new UsbBroadcastReceiver();
        }
    }

    /**
     * 单一实例
     */
    public static USBManager getInstance() {
        if (instance == null) {
            synchronized (USBManager.class) {
                if (instance == null) {
                    instance = new USBManager();
                }
            }
        }
        return instance;
    }


    /**
     * 注册加载U盘工具  包含了网络修改功能和版本更新功能
     *
     * @param mContext
     * @param mUsbManagerListener
     */
    public void registerReceiver(Activity mContext,UsbManagerListener mUsbManagerListener) {
        this.mUsbManagerListener = mUsbManagerListener;
        this.mContext = mContext;
        IntentFilter filter = new IntentFilter();
        //插入SD卡并且已正确安装（识别）时发出的广播
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);

        //拔未正确移除SD卡(正确移除SD卡的方法:设置--SD卡和设备内存--卸载SD卡)，但已把SD卡取出来时发出的广播
        //扩展介质（扩展卡）已经从 SD 卡插槽拔出，但是挂载点 (mount point) 还没解除 (unmount)
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);

        //已拔掉外部大容量储存设备发出的广播（比如SD卡，或移动硬盘）,不管有没有正确卸载都会发出此广播
        filter.addAction(Intent.ACTION_MEDIA_EJECT);

        //替换apk
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        //添加apk
        //filter.addAction(Intent.ACTION_PACKAGE_ADDED);

        //一个已存在的应用程序包已经从设备上移除，包括包名（正在被安装的包程序不能接收到这个广播）
        //filter.addAction(Intent.ACTION_PACKAGE_REMOVED);

        filter.addDataScheme("file");
        mContext.registerReceiver(mFuctionBroadcastReceiver, filter, "android.permission.READ_EXTERNAL_STORAGE", null);
    }


    /**
     * U盘加载完成
     *
     * @param mountPath
     */
    public void usbLoadingCompleted(final String mountPath) {
        getmScheduledExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                usbLoadingListenerManager(USBINSERTSUCCESSCODE, USBINSERTSUCCESSMSG);
                //加载网络配置文件

                //加载版本更新
                apkUpdateManager(mountPath);
            }
        });

    }


    /**
     * 版本更新管理实例化
     */
    private VersionUpgradeManager mVersionUpgradeManager;

    /**
     * 开始版本管理
     *
     * @param mountPath
     */
    private void apkUpdateManager(String mountPath) {
        if (mVersionUpgradeManager == null) {
            mVersionUpgradeManager = new VersionUpgradeManager();
        }
        mVersionUpgradeManager.startApkUpdateManager(mContext, mountPath, apkAndNetworkListener);
    }


    private UsbManagerListener apkAndNetworkListener = new UsbManagerListener() {
        @Override
        public void successListener(int type, String msg) {
            usbSuccessListenerManager(type, msg);
        }

        @Override
        public void loadingListener(int code, String msg) {
            usbLoadingListenerManager(code, msg);
        }

        @Override
        public void fallListener(String msg) {
            usbFallListenerManager(msg);
        }
    };

    /**
     * U盘处理逻辑加载完成
     *
     * @param type 0、配置网络参数 1、版本更新
     * @param msg
     */
    private void usbSuccessListenerManager(final int type, final String msg) {
        if (mUsbManagerListener != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUsbManagerListener.successListener(type, msg);
                }
            });
        }
    }

    /**
     * U盘加载过程中的各种状态 参考UsbConstant.java文件
     *
     * @param code
     * @param msg
     */
    private void usbLoadingListenerManager(final int code, final String msg) {
        if (mUsbManagerListener != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUsbManagerListener.loadingListener(code, msg);
                }
            });
        }
    }

    /**
     * u盘操作失败
     *
     * @param msg
     */
    private void usbFallListenerManager(final String msg) {
        if (mUsbManagerListener != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUsbManagerListener.fallListener(msg);
                }
            });
        }
    }

    /**
     * 拔出U盘处理
     */
    public void UsbCancel() {
        stopScheduledExecutorService();
        mVersionUpgradeManager = null;
    }

    /**
     * 线程管理
     */
    private ScheduledExecutorService mScheduledExecutorService;

    /**
     * 创建线程池
     *
     * @return
     */
    public ScheduledExecutorService getmScheduledExecutorService() {
        if (mScheduledExecutorService == null) {
            synchronized (USBManager.class) {
                if (mScheduledExecutorService == null) {
                    mScheduledExecutorService = new ScheduledThreadPoolExecutor(10);
                }
            }
        }
        return mScheduledExecutorService;
    }

    /**
     * 停止线程
     */
    public void stopScheduledExecutorService() {
        if (mScheduledExecutorService == null) {
            return;
        }
        try {
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdown();
            }
            if (!mScheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS)) {
                if (mScheduledExecutorService != null) {
                    mScheduledExecutorService.shutdownNow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdownNow();
            }
        } finally {
            mScheduledExecutorService = null;
        }
    }
}
