package com.haoxueche.mz200alib.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by xiezhongming on 17/9/22.
 *
 */

public class DipUtil {

    /**
     * dip转pix
     *
     * @param dp
     * @return
     */

    public static float dp2px(float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }


    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = ContextHolder.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ContextHolder.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
