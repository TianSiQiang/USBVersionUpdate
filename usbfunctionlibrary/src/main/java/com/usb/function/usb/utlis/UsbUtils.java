package com.usb.function.usb.utlis;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Tian_SiQiang
 * @CreationTime： 2019/1/24 13:44
 * @Introduction：UsbU盘函数
 */
public class UsbUtils {



    /**
     * 获取最新文件
     *
     * @param fileList
     * @return
     */
    public static File getNewestFile(List<File> fileList) {
        if (fileList == null || fileList.size() <= 0) {
            return null;
        }
        Collections.sort(fileList, new CompratorByLastModified());
        return fileList.get(fileList.size()-1);
    }

}
