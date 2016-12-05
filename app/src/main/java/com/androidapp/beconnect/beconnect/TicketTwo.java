package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;

public class TicketTwo extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    Button bOKTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_two);

        bOKTicket = (Button) findViewById(R.id.bOKTicket);

        bOKTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditBusinessCardIntent = new Intent(TicketTwo.this, MainActivity.class);
                TicketTwo.this.startActivity(EditBusinessCardIntent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mLogin:
                Intent Loginintent = new Intent(this, LoginActivity.class);
                this.startActivity(Loginintent);
                break;
            case R.id.mBusinessCard:
                Intent BusinessCardintent = new Intent(this, BusinessCard.class);
                this.startActivity(BusinessCardintent);
                break;
            case R.id.mEvents:
                Intent Eventsintent = new Intent(this, Events.class);
                this.startActivity(Eventsintent);
                break;
            case R.id.mTicket:
                Intent Ticketintent = new Intent(this, TicketOne.class);
                this.startActivity(Ticketintent);
                break;
            case R.id.mNews:
                Intent Newsintent = new Intent(this, News.class);
                this.startActivity(Newsintent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
