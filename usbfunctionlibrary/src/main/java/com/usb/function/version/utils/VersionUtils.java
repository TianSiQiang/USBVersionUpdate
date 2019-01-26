package com.usb.function.version.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionUtils {

    /**
     * 取APP版本号
     *
     * @return
     */
    public static int getAppVersionCode(Activity p_Activity) {
        try {
            PackageManager mPackageManager = p_Activity.getPackageManager();
            PackageInfo _info = mPackageManager.getPackageInfo(p_Activity.getPackageName(), 0);
            return _info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 取APP版本名
     *
     * @return
     */
    public static String getAppVersionName(Activity p_Activity) {
        try {
            PackageManager mPackageManager = p_Activity.getPackageManager();
            PackageInfo _info = mPackageManager.getPackageInfo(p_Activity.getPackageName(), 0);
            return _info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
