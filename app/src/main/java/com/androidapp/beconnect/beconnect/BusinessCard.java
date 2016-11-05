package com.androidapp.beconnect.beconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BusinessCard extends AppCompatActivity {

    Button bEditBusinessCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);

        bEditBusinessCard = (Button) findViewById(R.id.bEditBusinessCard);

        bEditBusinessCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCardIntent = new Intent(BusinessCard.this, EditBusinessCard.class);
                BusinessCard.this.startActivity(EditBusinessCardIntent);
            }
        });

    }
}
