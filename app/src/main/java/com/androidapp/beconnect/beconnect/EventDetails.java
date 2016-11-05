package com.androidapp.beconnect.beconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EventDetails extends AppCompatActivity {

    Button bSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        bSignUp = (Button) findViewById(R.id.bSignUp);

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCardIntent = new Intent(EventDetails.this, Ticket.class);
                EventDetails.this.startActivity(EditBusinessCardIntent);
            }
        });

    }
}
