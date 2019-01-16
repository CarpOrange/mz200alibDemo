package com.haoxueche.demo;

import android.os.Bundle;
import android.widget.TextView;

import com.haoxueche.mz200alib.activity.NfcActivity;

public class ICCardActivity extends NfcActivity {

    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iccard);
        initView();
    }

    @Override
    public void onDataRead(String data) {
        tvData.setText(data);
    }

    private void initView() {
        tvData = (TextView) findViewById(R.id.tvData);
    }
}
