package com.androidapp.beconnect.beconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by mitour on 2016/12/12.
 */

public class Agendas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendas);

        Log.d("id", getIntent().getStringExtra("EXTRA_SESSION_ID"));
    }
}
