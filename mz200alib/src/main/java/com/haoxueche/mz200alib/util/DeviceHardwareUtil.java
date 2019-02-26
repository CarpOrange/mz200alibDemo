package com.haoxueche.mz200alib.util;

import android.os.Build;

/**
 * Created by Lyc(987424501@qq.com) on 2018/10/26.
 * 设备硬件相关
 */

/**
 序列号起始		    序列号结束	    生产日期            版本特点
 TM80F8BA4D5815	～	TM80F8BA570D8C	2018/8/21	   硬件支持电瓶电压检测等新功能
 TM80F5A2778839	～	TM80F5A28BEA5E	2018/5/21
 TM80E9CE2C7A6A	～	TM80E9CE3CD431	2017/9/27
 TM80E89640EF96	～	TM80E896476278	2017/8/12
 */


public class DeviceHardwareUtil {
    /**
     * 对应 TM80F8BA4D5815，将16进制转为10进制
     */
    public static final long SERIAL_OF_PRODUCTION_DATE_AFTER_2018_8_21 = 141805765875733L;

    public static final long SERIAL; //设备序列号

    public static final Boolean NEED_SET_GPIO_76_LOW_WHEN_SHUT_DOWN; //关机时是否需要将gpio76置低，此需求由硬件部蒋科成提出

    static {
        String serialStr = Build.SERIAL;
        if(serialStr != null && serialStr.startsWith("TM")) {
            serialStr = serialStr.replace("TM", "");
            SERIAL = Long.parseLong(serialStr, 16);
        } else {
            SERIAL = 0L;
        }

        if(SERIAL >= SERIAL_OF_PRODUCTION_DATE_AFTER_2018_8_21) {
            NEED_SET_GPIO_76_LOW_WHEN_SHUT_DOWN = true;
        } else {
            NEED_SET_GPIO_76_LOW_WHEN_SHUT_DOWN = false;
        }
    }
}
