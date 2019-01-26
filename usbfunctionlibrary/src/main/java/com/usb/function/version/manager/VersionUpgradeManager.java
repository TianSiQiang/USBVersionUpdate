package com.usb.function.version.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;


import com.usb.function.usb.listener.UsbManagerListener;
import com.usb.function.usb.utlis.UsbUtils;
import com.usb.function.version.utils.VersionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.usb.function.usb.manager.USBManager.TAG;
import static com.usb.function.usb.manager.USBManager.isRootDirectory;
import static com.usb.function.usb.utlis.UsbConstant.USBAPKFILECODE;
import static com.usb.function.usb.utlis.UsbConstant.USBAPKFILEGETSUCCESSCODE;
import static com.usb.function.usb.utlis.UsbConstant.USBAPKFILEGETSUCCESSMSG;
import static com.usb.function.usb.utlis.UsbConstant.USBAPKFILEMSG;

/**
 * @Author: Tian_SiQiang
 * @CreationTime： 2018/12/25 9:52
 * @Introduction：版本更新
 */
public class VersionUpgradeManager {

    private UsbManagerListener mUsbManagerListener;

    private Activity mActivity;

    /**
     * 启动版本更新管理
     *
     * @param p_Activity
     * @param path
     */
    public void startApkUpdateManager(Activity p_Activity, String path, UsbManagerListener mUsbManagerListener) {
        this.mActivity = p_Activity;
        this.mUsbManagerListener = mUsbManagerListener;
        usbLoadingListener(USBAPKFILECODE, USBAPKFILEMSG);
        //获取外置U盘
        File file = new File(path);
        // 读取U盘中的文件
        File[] files = file.listFiles();
        //1、获取U盘中所有的.pak文件
        List<File> apkFileList = apkFileList(files);
        List<File> updateApk = new ArrayList<>();
        for (File g : apkFileList) {
            if (isWhetherToUpdate(mActivity, g)) {
                updateApk.add(g);
            }
        }
        if (updateApk != null && updateApk.size() > 0) {
            usbLoadingListener(USBAPKFILEGETSUCCESSCODE, USBAPKFILEGETSUCCESSMSG);
            File apk;
            if (updateApk.size() > 1) {
                apk = UsbUtils.getNewestFile(updateApk);
            } else {
                apk = updateApk.get(0);
            }
            //开始静默安装
            Log.e(TAG, "安装apk的路径：" + apk.getAbsolutePath());
            excutesucmd(apk.getAbsolutePath());
//       int i=     new SilentInstaller().installSilent(mActivity, apk.getAbsolutePath());
//           Log.e(TAG,"安装apk的结果："+i);
//            slientInstall(apk);
        } else {
            usbSuccessListener(1, "没有需要更新的apk");
        }

    }


    /**
     * 获取满足条件的apk
     *
     * @param mActivity
     * @param apkFile   APK文件
     * @return
     */
    private boolean isWhetherToUpdate(Activity mActivity, File apkFile) {
        boolean isUpdate = false;
        PackageManager pm;
        PackageInfo pkgInfo;
        ApplicationInfo appInfo;
        try {
            String apkFilePath = apkFile.getAbsolutePath();
            //获APK包的信息:版本号,名称,图标 等..
            pm = mActivity.getPackageManager();
            pkgInfo = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            if (pkgInfo != null) {
                appInfo = pkgInfo.applicationInfo;
                /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
                appInfo.sourceDir = apkFilePath;
                appInfo.publicSourceDir = apkFilePath;
                // 得到应用名  注释掉  要不然拔掉U盘程序会崩溃
                //String appName = pm.getApplicationLabel(appInfo).toString();
                // 得到包名
                String packageName = appInfo.packageName;
                // 得到版本信息
                String version = pkgInfo.versionName;
                int versioncode = pkgInfo.versionCode;
                //获取apk图标
                //Drawable icon2 = appInfo.loadIcon(pm);
                //drawable2 = icon2;
                String pkgInfoStr = String.format("PackageName:%s, Vesion:%s,versioncode:%s", packageName, version, versioncode);
                Log.e(TAG, "获取到U盘中apk的信息：" + pkgInfoStr);

                if (TextUtils.equals(mActivity.getPackageName(), packageName) && versioncode > VersionUtils.getAppVersionCode(mActivity)) {
                    isUpdate = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pm = null;
            pkgInfo = null;
            appInfo = null;
        }
        return isUpdate;
    }


    /**
     * 所有的.apk文件
     */
    private List<File> apkFileList = new ArrayList<>();

    /**
     * 获取所有.apk的文件
     *
     * @param files
     * @return
     */
    private List<File> apkFileList(File[] files) {
        if (apkFileList == null) {
            apkFileList = new ArrayList<>();
        } else {
            apkFileList.clear();
        }
        getApkFile(files);

        return apkFileList;
    }

    /**
     * 遍历.apk的文件
     *
     * @param files
     * @return
     */
    private void getApkFile(File[] files) {
        if (files != null) {
            String fileName;
            for (File file : files) {
                if (!isRootDirectory && file.isDirectory()) {
                    Log.e(TAG, "文件目录,继续读" + file.getName() + file.getPath());
                    getApkFile(file.listFiles());
                } else {
                    fileName = file.getName();
                    Log.e(TAG, "文件名:" + fileName);
                    Log.e(TAG, "最后修改时间:" + file.lastModified());
                    if (fileName.endsWith(".apk")) {
                        if (apkFileList == null) {
                            apkFileList = new ArrayList<>();
                        }
                        apkFileList.add(file);
                    }
                }
            }
        }
    }


    /**
     * 静默安装
     *
     * @param currenttempfilepath
     */
    public void excutesucmd(String currenttempfilepath) {
        Process process = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            // 请求root
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            // 调用安装
            out.write(("pm install -r " + currenttempfilepath + "\n").getBytes());
            in = process.getInputStream();
            int len = 0;
            byte[] bs = new byte[256];
            while (-1 != (len = in.read(bs))) {
                String state = new String(bs, 0, len);
                if (state.equals("success\n")) {
                    Log.e(TAG, "安装完成：");
                    //安装成功后的操作
                    usbSuccessListener(1, "软件更新成功");
                } else {
                    //安装失败后的操作
                    usbFallListener("软件更新失败：" + state);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 版本更新完成
     *
     * @param type 0、配置网络参数 1、版本更新
     * @param msg
     */
    private void usbSuccessListener(int type, final String msg) {
        if (mUsbManagerListener != null) {
            mUsbManagerListener.successListener(type, msg);
        }
    }

    /**
     * apk加载过程中的各种状态 参考UsbConstant.java文件
     *
     * @param code
     * @param msg
     */
    private void usbLoadingListener(final int code, final String msg) {
        if (mUsbManagerListener != null) {
            mUsbManagerListener.loadingListener(code, msg);
        }
    }

    /**
     * 版本更新失败
     *
     * @param msg
     */
    private void usbFallListener(final String msg) {
        if (mUsbManagerListener != null) {
            mUsbManagerListener.fallListener(msg);
        }
    }

    /**
     * 监测到升级后执行app的启动
     */
    public static void startApp(Context context) {
        // 根据包名打开安装的apk
        Log.e(TAG, "包名：" + context.getPackageName());
        Intent resolveIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(resolveIntent);
    }


}
