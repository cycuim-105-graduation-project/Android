package com.androidapp.beconnect.beconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class NewsDetails extends AppCompatActivity {

    Button bOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        bOK = (Button) findViewById(R.id.bOK);

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewsDetailsCardIntent = new Intent(NewsDetails.this, News.class);
                NewsDetails.this.startActivity(NewsDetailsCardIntent);
            }
        });

    }
}
