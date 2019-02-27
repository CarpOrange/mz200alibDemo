package com.haoxueche.mz200alib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.haoxueche.mz200alib.util.DeviceHardwareUtil;
import com.haoxueche.mz200alib.util.SystemUtil;
import com.haoxueche.winterlog.L;


/**
 * 类名：ShutdownBroadcastReceiver 
 * 功能描述：在系统即将关闭时发出的广播的接收器 
 * @author android_ls 
 */  
public class ShutdownBroadcastReceiver extends BroadcastReceiver {
  
    private static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
      
    @Override  
    public void onReceive(Context context, Intent intent) {
        L.w( "ShutdownBroadcastReceiver");
        if (ACTION_SHUTDOWN.equals(intent.getAction())) {
            //关机时置低，硬件部蒋科成提的，新设备的改动,可以让设备关机后完全断电
            if(DeviceHardwareUtil.NEED_SET_GPIO_76_LOW_WHEN_SHUT_DOWN) {
                L.w( "NEED_SET_GPIO_76_LOW_WHEN_SHUT_DOWN");
                SystemUtil.setGpioLow(76);
            }
        }
    }  
}  