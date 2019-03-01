package com.haoxueche.mz200alib.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.haoxueche.winterlog.L;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xiezhongming on 17/8/8.
 */

public class InstallUtil {

    private static final String TAG = "ApkController";

    /**
     * 描述: 安装
     */
    public static boolean install(Context context, String apkPath) {
        File file = new File(apkPath);
        if (!file.exists()) {
            return false;
        }
        // 先判断手机是否有系统权限
        if (SystemUtil.isSystemApp(context.getApplicationInfo())) {
            // 系统APP，利用静默安装实现
            return installSilently(apkPath);
        } else {
            // 没有系统权限，用Intent进行安装
            installNeedAgree(context, file);
            return true;
        }
    }

    /**
     * 系统默认安装
     * @param context
     * @param file
     * @return
     */
    public static void installNeedAgree(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public static boolean installSilently(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            String command = "pm install -r " + apkPath;
            Process process = Runtime.getRuntime().exec(command);
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
//            String command = "pm install -r " + apkPath + "\n";
//            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
//            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d("TAG", "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Log.e("TAG", e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                L.e(e);
            }
        }
        return result;
    }

}
