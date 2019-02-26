package com.haoxueche.mz200alib.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.haoxueche.mz200alib.util.ICCardListener;
import com.haoxueche.mz200alib.util.MessageUtil;
import com.haoxueche.mz200alib.util.T;
import com.haoxueche.winterlog.L;

import java.io.ByteArrayOutputStream;

import okio.BufferedSink;
import okio.Okio;
import okio.Sink;


/**
 * Created by Lyc(987424501@qq.com) on 2017/10/17.
 * 用到Nfc的界面
 */

public abstract class NfcActivity extends AppCompatActivity implements ICCardListener {
    private static final String TAG = "NfcActivity";
    //nfc适配器
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private String cardNo;
    /**
     * 这个是扇区密码，需要根据实际使用的卡的扇区密码修改
     */
    private String password = MessageUtil.byteArrToHex(MifareClassic.KEY_DEFAULT);

    private int bIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[]{ndef,};

        // 读标签之前先确定标签类型。这里以大多数的NfcA为例
        mTechLists = new String[][]{new String[]{NfcA.class.getName()}};
    }

    //指定一个用于处理NFC标签的窗口
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String intentActionStr = intent.getAction();// 获取到本次启动的action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentActionStr)// NDEF类型
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intentActionStr)// 其他类型
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intentActionStr)) {// 未知类型
            //在intent中读取Tag id
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] bytesId = tag.getId();// 获取id数组get

            cardNo = MessageUtil.bytesToHexString(bytesId);
            generatePassword();
            tag = patchTag(tag); //有的手机上没有这一步会崩溃

            byte[] cardData = readTag(tag);
            if (cardData != null) {
                String dataStr = MessageUtil.bytesToHexString(cardData);
                L.i("读卡完成:" + dataStr);
                /**
                 * 这里传的是把byte数组转为16进制字符串的数据，可根据实际业务修改接口，封装为实体类等。
                 */
                onDataRead(dataStr);
            } else {
                L.i("读卡出错:");
            }
        }
    }

    //TODO 给密码赋值
    private void generatePassword() {
    }

    /**
     * 这个方法只是为了兼容
     * @param oTag
     * @return
     */
    public Tag patchTag(Tag oTag) {
        if (oTag == null) {
            return null;
        }

        String[] sTechList = oTag.getTechList();

        Parcel oParcel, nParcel;

        oParcel = Parcel.obtain();
        oTag.writeToParcel(oParcel, 0);
        oParcel.setDataPosition(0);

        int len = oParcel.readInt();
        byte[] id = null;
        if (len >= 0) {
            id = new byte[len];
            oParcel.readByteArray(id);
        }
        int[] oTechList = new int[oParcel.readInt()];
        oParcel.readIntArray(oTechList);
        Bundle[] oTechExtras = oParcel.createTypedArray(Bundle.CREATOR);
        int serviceHandle = oParcel.readInt();
        int isMock = oParcel.readInt();
        IBinder tagService;
        if (isMock == 0) {
            tagService = oParcel.readStrongBinder();
        } else {
            tagService = null;
        }
        oParcel.recycle();

        int nfca_idx = -1;
        int mc_idx = -1;

        for (int idx = 0; idx < sTechList.length; idx++) {
            if (sTechList[idx] == NfcA.class.getName()) {
                nfca_idx = idx;
            } else if (sTechList[idx] == MifareClassic.class.getName()) {
                mc_idx = idx;
            }
        }

        if (nfca_idx >= 0 && mc_idx >= 0 && oTechExtras[mc_idx] == null) {
            oTechExtras[mc_idx] = oTechExtras[nfca_idx];
        } else {
            return oTag;
        }

        nParcel = Parcel.obtain();
        nParcel.writeInt(id.length);
        nParcel.writeByteArray(id);
        nParcel.writeInt(oTechList.length);
        nParcel.writeIntArray(oTechList);
        nParcel.writeTypedArray(oTechExtras, 0);
        nParcel.writeInt(serviceHandle);
        nParcel.writeInt(isMock);
        if (isMock == 0) {
            nParcel.writeStrongBinder(tagService);
        }
        nParcel.setDataPosition(0);

        Tag nTag = Tag.CREATOR.createFromParcel(nParcel);

        nParcel.recycle();

        return nTag;
    }

    /**
     * 读取数据
     *
     * @param tag
     * @return
     */
    private byte[] readTag(Tag tag) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Sink sink = Okio.sink(outputStream);
        try (BufferedSink bufferedSink = Okio.buffer(sink);
             MifareClassic mfc = MifareClassic.get(tag)) {

            for (String tech : tag.getTechList()) {
                System.out.println(tech);//显示设备支持技术
            }
            boolean auth;
            StringBuffer metaInfo = new StringBuffer();
            // String metaInfo = "";
            //Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();//获取TAG的类型
            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo.append("  卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
                    + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize()
                    + "B\n");
            /**
             * 这里读取的扇区起始位置需要根据实际业务调整，尽量不要读取用不到的扇区的数据，减少读取中可能出现的超时等错误的几率。
             */
            for (int j = 0; j < sectorCount; j++) {
                //Authenticate a sector with key A.
                auth = mfc.authenticateSectorWithKeyA(j, MessageUtil.hexToByteArr(password));
                //逐个获取密码
                if (!auth) {
                    //一种验证方式不通过换一种
                    auth = mfc.authenticateSectorWithKeyB(j, MessageUtil.hexToByteArr(password));
                    //逐个获取密码
                }
                if (auth) {
                    L.i("Sector " + j + ":验证成功\n");
                    //   metaInfo += "Sector " + j + ":验证成功\n";
                    // 读取扇区中的块
                    int bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    /**
                     * 这里读取的块起始位置需要根据实际业务调整，尽量不要读取用不到的扇区的数据，减少读取中可能出现的超时等错误的几率。
                     */
                    for (int i = 0; i < bCount; i++) {
                        L.i("bIndex==" + bIndex);
                        byte[] data = mfc.readBlock(bIndex);
                        bufferedSink.write(data);
                        bIndex++;
                    }
                } else {
                    L.i("Sector " + j + ":验证失败\n");
                    T.showSpeak("卡验证失败，请重新刷卡");
                    return null;
                }
            }
            byte[] cardData = bufferedSink.buffer().readByteArray();
            return cardData;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            L.e(e);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
