package com.androidapp.beconnect.beconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Ticket extends AppCompatActivity {

    Button bOKTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        bOKTicket = (Button) findViewById(R.id.bOKTicket);

        bOKTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCardIntent = new Intent(Ticket.this, MainActivity.class);
                Ticket.this.startActivity(EditBusinessCardIntent);
            }
        });

    }
}
