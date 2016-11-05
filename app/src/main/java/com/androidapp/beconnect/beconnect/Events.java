package com.androidapp.beconnect.beconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Events extends AppCompatActivity {

    Button bEventDetail1;
    Button bEventDetail2;
    Button bEventDetail3;
    Button bEventDetail4;
    Button bEventDetail5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        bEventDetail1 = (Button) findViewById(R.id.bEventDetail1);

        bEventDetail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard1Intent = new Intent(Events.this, MainActivity.class);
                Events.this.startActivity(EditBusinessCard1Intent);
            }
        });
        bEventDetail2 = (Button) findViewById(R.id.bEventDetail2);

        bEventDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard2Intent = new Intent(Events.this, MainActivity.class);
                Events.this.startActivity(EditBusinessCard2Intent);
            }
        });
        bEventDetail3 = (Button) findViewById(R.id.bEventDetail3);

        bEventDetail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard3Intent = new Intent(Events.this, MainActivity.class);
                Events.this.startActivity(EditBusinessCard3Intent);
            }
        });
        bEventDetail4 = (Button) findViewById(R.id.bEventDetail4);

        bEventDetail4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard4Intent = new Intent(Events.this, MainActivity.class);
                Events.this.startActivity(EditBusinessCard4Intent);
            }
        });
        bEventDetail5 = (Button) findViewById(R.id.bEventDetail5);

        bEventDetail5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard5Intent = new Intent(Events.this, MainActivity.class);
                Events.this.startActivity(EditBusinessCard5Intent);
            }
        });

    }
}

