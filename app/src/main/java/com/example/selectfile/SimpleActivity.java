package com.example.selectfile;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.example.selectfile.pdf.SignView;
import com.example.selectfile.view.MyBigView;

import java.io.IOException;
import java.io.InputStream;

public class SimpleActivity extends Activity {

    private SignView signView;
    int reqWidth = 1080;
    int reqHeight = 1920;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

//        Bitmap bg = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg)
//                .copy(Bitmap.Config.ARGB_8888, true);
//
//        //Bitmap bm = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.RGB_565);
//
//        signView = findViewById(R.id.sign_view);
//
//        signView.setBgBitmap(bg);

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("bg.jpg");
            MyBigView myBigView  = findViewById(R.id.mg_big_view);
            myBigView.setImage(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}