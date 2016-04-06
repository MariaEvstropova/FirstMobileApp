package com.evstropova.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.try_relative);

        ImageView iv = (ImageView) findViewById(R.id.imageView2);
        iv.setImageResource(R.drawable.image);
    }
}
