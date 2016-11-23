package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Events extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

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
                Intent EditBusinessCard1Intent = new Intent(Events.this, EventDetails.class);
                Events.this.startActivity(EditBusinessCard1Intent);
            }
        });
        bEventDetail2 = (Button) findViewById(R.id.bEventDetail2);

        bEventDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard2Intent = new Intent(Events.this, EventDetails.class);
                Events.this.startActivity(EditBusinessCard2Intent);
            }
        });
        bEventDetail3 = (Button) findViewById(R.id.bEventDetail3);

        bEventDetail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard3Intent = new Intent(Events.this, EventDetails.class);
                Events.this.startActivity(EditBusinessCard3Intent);
            }
        });
        bEventDetail4 = (Button) findViewById(R.id.bEventDetail4);

        bEventDetail4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard4Intent = new Intent(Events.this, EventDetails.class);
                Events.this.startActivity(EditBusinessCard4Intent);
            }
        });
        bEventDetail5 = (Button) findViewById(R.id.bEventDetail5);

        bEventDetail5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCard5Intent = new Intent(Events.this, EventDetails.class);
                Events.this.startActivity(EditBusinessCard5Intent);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 檢查是否有可用的藍牙裝置
        if (mBluetoothAdapter == null) {
            // 若無可用裝置時執行
            Toast.makeText(this, "Bluetooth not supported on this Device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 如果藍牙目前不可用，請求使用者開啟藍芽功能。
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

    }

    // 使用onActivityResult 接收其他 Activity回傳的資料
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 接收請求開啟藍芽功能的結果
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 使用者授權藍牙後執行
            Toast.makeText(this, "使用者已授權藍牙使用", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            // 使用者拒絕授權藍牙後執行
            Toast.makeText(this, "使用者拒絕授權藍牙使用", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }
}

