package com.haoxueche.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.haoxueche.demo.R;
import com.haoxueche.mz200alib.util.SystemUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SystemUtil.getImei(this);

    }

    public void icTest(View view) {
        Intent intent = new Intent(this, ICCardActivity.class);
        startActivity(intent);
    }

    public void idTest(View view) {
        Intent intent = new Intent(this, IdCardActivity.class);
        startActivity(intent);
    }

}
