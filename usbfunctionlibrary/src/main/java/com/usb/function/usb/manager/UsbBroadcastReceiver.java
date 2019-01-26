package com.usb.function.usb.manager;

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
public class UsbBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
       Log.e(TAG, "广播状态：" + action);

        switch (action) {
            case Intent.ACTION_MEDIA_MOUNTED:
               Log.e(TAG, "U盘插入成功");
                String mountPath = intent.getData().getPath();
                Log.e(TAG, "路径：" + mountPath);
                USBManager.getInstance().isUsbUnInsert = false;
                //开始处理
                USBManager.getInstance().usbLoadingCompleted(mountPath);


//                USBManager.getInstance().getUsbFile(mountPath, USBManager.getInstance().modifyingNetworkParametersTxtFileName);
                break;
            case Intent.ACTION_MEDIA_BAD_REMOVAL:
                if (!USBManager.getInstance().isUsbUnInsert) {
                   Log.e(TAG, "U盘1已拔出");
                    USBManager.getInstance().UsbCancel();
                }
                USBManager.getInstance().isUsbUnInsert = true;
                break;
            case Intent.ACTION_MEDIA_EJECT:
                if (!USBManager.getInstance().isUsbUnInsert) {
                   Log.e(TAG, "U盘2已拔出");
                    USBManager.getInstance().UsbCancel();
                }
                USBManager.getInstance().isUsbUnInsert = true;
                break;
        }
    }
}
