package com.usb.function.usb.utlis;

import java.io.File;
import java.util.Comparator;


/**
 * @Author: Tian_SiQiang
 * @CreationTime： 2019/1/24 13:47
 * @Introduction：文件排序方式  根据文件修改时间进行比较的内部类
 */
public class CompratorByLastModified implements Comparator<File> {

    @Override
    public int compare(File f1, File f2) {
        long diff = f1.lastModified() - f2.lastModified();
        if (diff > 0) {
            return 1;
        } else if (diff == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
