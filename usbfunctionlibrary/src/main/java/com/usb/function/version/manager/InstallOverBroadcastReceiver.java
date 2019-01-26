package com.usb.function.version.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.usb.function.usb.manager.USBManager.TAG;

/**
 * @Author: Tian_SiQiang
 * @CreationTime： 2018/12/26 13:47
 * @Introduction：广播监听
 */
public class InstallOverBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
       Log.e(TAG, "广播状态：" + action);
        switch (action) {
            case Intent.ACTION_MY_PACKAGE_REPLACED:
                String packageName = intent.getDataString();
               Log.e(TAG, "升级了" + packageName + "包名的程序");
                VersionUpgradeManager.startApp(context);
                break;
        }
    }
}
