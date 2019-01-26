package com.usb.function.usb.utlis;

/**
 * 作者：TianSiQiang
 * 创建时间： 2018/2/8 11:10
 * 描述：
 */
public class UsbConstant {


   /**
    * U盘插入成功code
    */
   public final static int USBINSERTSUCCESSCODE=91101;
   /**
    * U盘插入成功msg
    */
   public final static String USBINSERTSUCCESSMSG="U盘装在完毕";


   /**
    * U盘卸载成功code
    */
   public final static int USBUNINSERTSUCCESSCODE=91102;
   /**
    * U盘卸载成功msg
    */
   public final static String USBUNINSERTSUCCESSMSG="U盘卸载成功";


   /**
    * U盘读取失败
    */
   public final static String USBREADFILEFALL="U盘读取失败";
   /**
    * 读取U盘文件code
    */
   public final static int USBREADFILECODE=91103;
   /**
    * 读取U盘文件msg
    */
   public final static String USBREADFILEMSG="正在读取U盘文件";

   /**
    * 读取网络配置文件code
    */
   public final static int USBREADNETWORKFILECODE=91104;
   /**
    * 读取网络配置文件msg
    */
   public final static String USBREADNETWORKFILEMSG="正在读取网络配置文件";

   /**
    * 配置文件读取失败
    */
   public final static String USBREADNETWORKFILEFALL="网络配置文件读取失败";

   /**
    * 解析网络配置文件code
    */
   public final static int USBJSONNETWORKFILECODE=91105;
   /**
    * 解析网络配置文件msg
    */
   public final static String USBJSONNETWORKFILEMSG="正在解析网络配置文件";

   /**
    * 解析文件读取失败
    */
   public final static String USBJSONDNETWORKFILEFALL="网络配置文件解析失败";



   /**
    * 获取软件安装包code
    */
   public final static int USBAPKFILECODE=91106;
   /**
    * 获取软件安装包msg
    */
   public final static String USBAPKFILEMSG="获取需要更新的软件安装包···";

   /**
    * 获取更新包成功code
    */
   public final static int USBAPKFILEGETSUCCESSCODE=91107;
   /**
    * 获取更新包成功msg
    */
   public final static String USBAPKFILEGETSUCCESSMSG="升级包获取成功，正在安装中···";
}
