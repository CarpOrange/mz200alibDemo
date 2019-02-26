package com.haoxueche.mz200alib.acc;

import com.haoxueche.mz200alib.util.GpioUtil;
import com.temolin.hardware.GPIO_Pin;

/**
 * Acc管理类
 * 1、监听Acc状态
 * 2、通知监听者Acc状态
 */
public class AccManager {
    private final String TAG = "AccManager";
    public static final int INDEX_GPIO_ACC = 79;

    private final GPIO_Pin accGPIO = new GPIO_Pin(INDEX_GPIO_ACC);

    private AccManager() {
        accGPIO.setModeINPUT();
    }

    public static AccManager getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * @return acc是否开启， true表示acc开启,反之acc未开启
     */
    public boolean isAccOn() {
        int value =  accGPIO.getInputVal();
        return value == GpioUtil.GPIO_LOW;
    }

    private static class InstanceHolder {
        private static final AccManager instance = new AccManager();
    }


}