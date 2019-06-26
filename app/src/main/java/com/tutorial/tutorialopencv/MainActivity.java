package com.tutorial.tutorialopencv;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_activity);
        setTitle(R.string.app_name);

        int timeToSplash = 3500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent MainMenu = new Intent(MainActivity.this, Main_Menu.class);
                startActivity(MainMenu);
                finish();

            }
        }, timeToSplash);

    }






}
