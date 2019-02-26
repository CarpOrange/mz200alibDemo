package com.haoxueche.demo.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.haoxueche.demo.R;
import com.haoxueche.mz200alib.activity.NfcIdCardActivity;

public class IdCardActivity extends NfcIdCardActivity {

    /**
     * Demo
     */
    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);
        initView();
    }

    @Override
    public void onIdRead(String id) {
        tvData.setText(id);
    }

    private void initView() {
        tvData = (TextView) findViewById(R.id.tvData);
    }
}
