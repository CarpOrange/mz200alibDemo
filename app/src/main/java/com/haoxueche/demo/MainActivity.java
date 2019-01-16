package com.haoxueche.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void icTest(View view) {
        Intent intent = new Intent(this, ICCardActivity.class);
        startActivity(intent);
    }

    public void idTest(View view) {
        Intent intent = new Intent(this, IdCardActivity.class);
        startActivity(intent);
    }

    public void camera2Test(View view) {
        Intent intent = new Intent(this, FaceVerifyNewActivity.class);
        startActivity(intent);
    }
}
