package com.androidapp.beconnect.beconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class News extends AppCompatActivity {

    Button bDetails1;
    Button bDetails2;
    Button bDetails3;
    Button bDetails4;
    Button bDetails5;
    Button bDetails6;
    Button bDetails7;
    Button bDetails8;
    Button bDetails9;
    Button bDetails10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        bDetails1 = (Button) findViewById(R.id.bDetails1);

        bDetails1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails2 = (Button) findViewById(R.id.bDetails2);

        bDetails2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails3 = (Button) findViewById(R.id.bDetails3);

        bDetails3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails4 = (Button) findViewById(R.id.bDetails4);

        bDetails4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails5 = (Button) findViewById(R.id.bDetails5);

        bDetails5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails6 = (Button) findViewById(R.id.bDetails6);

        bDetails6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails7 = (Button) findViewById(R.id.bDetails7);

        bDetails7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails8 = (Button) findViewById(R.id.bDetails8);

        bDetails8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails9 = (Button) findViewById(R.id.bDetails9);

        bDetails9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });

        bDetails10 = (Button) findViewById(R.id.bDetails10);

        bDetails10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsIntent = new Intent(News.this, NewsDetails.class);
                News.this.startActivity(NewsDetailsIntent);
            }
        });
    }
}
