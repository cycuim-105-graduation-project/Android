package com.androidapp.beconnect.beconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button bLogin;
    Button bBusinessCard;
    Button bEvents;
    Button bTicket;
    Button bNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bLogin = (Button) findViewById(R.id.bLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(LoginIntent);
            }
        });

        bBusinessCard = (Button) findViewById(R.id.bBusinessCard);

        bBusinessCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BusinessCardIntent = new Intent(MainActivity.this, BusinessCard.class);
                MainActivity.this.startActivity(BusinessCardIntent);
            }
        });

        bEvents = (Button) findViewById(R.id.bEvents);

        bEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EventsIntent = new Intent(MainActivity.this, Events.class);
                MainActivity.this.startActivity(EventsIntent);
            }
        });
        bTicket = (Button) findViewById(R.id.bTicket);

        bTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TicketIntent = new Intent(MainActivity.this, Ticket.class);
                MainActivity.this.startActivity(TicketIntent);
            }
        });
        bNews = (Button) findViewById(R.id.bNews);

        bNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsIntent = new Intent(MainActivity.this, News.class);
                MainActivity.this.startActivity(NewsIntent);
            }
        });
    }
}
