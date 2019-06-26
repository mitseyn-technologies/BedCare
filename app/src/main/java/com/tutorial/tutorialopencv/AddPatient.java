package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AddPatient extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_patient);
        setTitle(R.string.addPatient);

    }

    private void go_ToMenuPatient()
    {
        Intent to_MenuPatient = new Intent(AddPatient.this,Menu_Patient.class);
        startActivity(to_MenuPatient);
        this.finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMenuPatient();

    }
}
