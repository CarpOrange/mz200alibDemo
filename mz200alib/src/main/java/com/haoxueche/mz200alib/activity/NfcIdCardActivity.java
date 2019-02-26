package com.haoxueche.mz200alib.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.haoxueche.mz200alib.util.IDCardListener;
import com.haoxueche.mz200alib.util.MessageUtil;
import com.haoxueche.winterlog.L;


/**
 * Created by Lyc(987424501@qq.com) on 2017/10/17.
 * 用到Nfc的界面
 */

public abstract class NfcIdCardActivity extends AppCompatActivity implements IDCardListener {
    private static final String TAG = "NfcIdCardActivity";

    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    private String cardNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i( "onCreate");
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this,  getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef  = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {ndef,    };

        // 读标签之前先确定标签类型。这里以大多数的NfcA为例
        mTechLists  = new String[][] { new String[] {NfcB.class.getName()} };
    }

    //指定一个用于处理NFC标签的窗口
    @Override
    protected void onResume() {
        super.onResume();
        L.i( "onResume");
        if(nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.enableForegroundDispatch(this,  mPendingIntent, mFilters, mTechLists);
            L.i( "onResume1");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.disableForegroundDispatch(this);
            L.i( "onPause");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        L.i( "onNewIntent");
        String intentActionStr = intent.getAction();// 获取到本次启动的action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentActionStr)// NDEF类型
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intentActionStr)// 其他类型
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intentActionStr)) {// 未知类型
            //在intent中读取Tag id
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] bytesId = tag.getId();// 获取id数组

            cardNo = MessageUtil.bytesToHexStringReversal(bytesId);
            onIdRead(cardNo);
        }
    }
}
