package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowPatient extends AppCompatActivity {

    private TextView name;
    private Button btn_StatePatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);
        setTitle(R.string.showPatient);

        InitElements();
        Actionelements();
    }

    private void InitElements()
    {
        btn_StatePatient = findViewById(R.id.btnState);
    }

    private void Actionelements()
    {
        btn_StatePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_toImageProcessing();
            }
        });
    }

    private void go_toImageProcessing()
    {
        Intent go_ImageP = new Intent(ShowPatient.this,imageProcessing.class);
        startActivity(go_ImageP);
        this.finish();
    }
}
