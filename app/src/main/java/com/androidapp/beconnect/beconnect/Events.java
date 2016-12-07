package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.onebeacon.api.OneBeacon;
import io.onebeacon.api.ScanStrategy;

public class Events extends AppCompatActivity implements ServiceConnection {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    Button bEventDetail1;
    Button bEventDetail2;

    private MonitorService mService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        bEventDetail1 = (Button) findViewById(R.id.bEventDetail1);

        bEventDetail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EventDetail1Intent = new Intent(Events.this, EventDetailsOne.class);
                Events.this.startActivity(EventDetail1Intent);
            }
        });
        bEventDetail2 = (Button) findViewById(R.id.bEventDetail2);

        bEventDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EventDetail2Intent = new Intent(Events.this, EventDetailsTwo.class);
                Events.this.startActivity(EventDetail2Intent);
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

        if (!bindService(new Intent(this, MonitorService.class), (ServiceConnection) this, BIND_AUTO_CREATE)) {
            setTitle("Bind failed! Manifest?");
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
    @Override
    protected void onDestroy() {
        // Activity is gone, set scan mode to use lowest possible power usage
        OneBeacon.setScanStrategy(ScanStrategy.LOW_POWER);
        if (null != mService) {
            // optionally stop the service if running in background is not desired
//            stopService(new Intent(this, MonitorService.class));
            unbindService(this);
            mService = null;
        }
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((MonitorService.LocalServiceBinder) service).getService();
        setTitle("Service connected");

        // make the service to stick around by actually starting it
        startService(new Intent(this, MonitorService.class));

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
        setTitle("Service disconnected");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity is visible, scan with most reliable results
        OneBeacon.setScanStrategy(ScanStrategy.LOW_LATENCY);
    }

    @Override
    protected void onPause() {
        // Activity is not in foreground, make a trade-off between battery usage and scan latency
        OneBeacon.setScanStrategy(ScanStrategy.BALANCED);
        super.onPause();
    }

}

