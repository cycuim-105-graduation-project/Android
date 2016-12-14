package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.onebeacon.api.OneBeacon;
import io.onebeacon.api.ScanStrategy;

public class Events extends AppCompatActivity implements ServiceConnection {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private List<detail> mItemList = null;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private MonitorService mService = null;
    SessionManager session;
    String url_get_event;
    String url_logout;
    String tag_string_req = "string_req";
    Button bEventDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Values.container = (CoordinatorLayout) findViewById(R.id.snackbar);

        session = new SessionManager(getApplicationContext());

        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mItemList = new ArrayList<>();

        url_get_event = getResources().getString(R.string.url_get_event);

        JsonArrayRequest jar = new JsonArrayRequest(url_get_event,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response", response.toString());

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject event = response.getJSONObject(i);
                                mItemList.add(new detail(event.getString("id"),
                                                        event.getString("name"),
                                                        event.getString("description"),
                                                        event.getString("feature_img_url"),
                                                        event.getString("start_at"),
                                                        event.getString("end_at"),
                                                        event.getString("registration_start_at"),
                                                        event.getString("registration_end_at"),
                                                        event.getString("quantity"),
                                                        event.getString("vacancy"),
                                                        event.getString("place")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        }
        );

        AppController.getInstance().addToRequestQueue(jar, tag_string_req);

        //Finally initializing our adapter
        adapter = new CardAdapter(mItemList, this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

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
            log("Bind failed! Manifest?");
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



        if (session.isLoggedIn()) {

            menu.getItem(0).setVisible(false);
        }
        else {
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(false);
            menu.getItem(6).setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (session.isLoggedIn()) {
            switch (item.getItemId()) {

                case R.id.mLogout:
                    // 拿使用者登入 ID, Access_token, key
                    HashMap<String, String> user = session.getUserDetails();

                    final String uid          = user.get(SessionManager.KEY_UID);
                    final String access_token = user.get(SessionManager.KEY_ACCESS_TOKEN);
                    final String client       = user.get(SessionManager.KEY_CLIENT);

                    url_logout = getResources().getString(R.string.url_logout);

                    // send logout request
                    StringRequest sr = new StringRequest(Request.Method.DELETE, url_logout,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(Events.this, "Logout success!", Toast.LENGTH_LONG).show();
                                    session.logoutUser();
                                }

                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(Events.this, error.toString(), Toast.LENGTH_LONG).show();
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("access-token", access_token);
                            params.put("uid",          uid);
                            params.put("client",       client);

                            return params;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(sr, tag_string_req);
                    break;
                case R.id.mProfile:
                    Intent Profileintent = new Intent(this, ProfileActivity.class);
                    this.startActivity(Profileintent);
                    break;
                case R.id.mBusinessCard:
                    Intent BusinessCardintent = new Intent(this, BusinessCard.class);
                    this.startActivity(BusinessCardintent);
                    break;
                case R.id.mEvents:
                    Intent Eventsintent = new Intent(this, Events.class);
                    this.startActivity(Eventsintent);
                    break;
//                case R.id.mTicket:
//                    Intent Ticketintent = new Intent(this, TicketOne.class);
//                    this.startActivity(Ticketintent);
//                    break;
                case R.id.mNews:
                    Intent Newsintent = new Intent(this, News.class);
                    this.startActivity(Newsintent);
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
            return true;
        }
        else {
            switch (item.getItemId()) {

                case R.id.mLogin:
                    Intent Loginintent = new Intent(this, LoginActivity.class);
                    this.startActivity(Loginintent);
                    break;
            }
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
        log("Service connected");

        // make the service to stick around by actually starting it
        startService(new Intent(this, MonitorService.class));

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
        log("Service disconnected");
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

    private void log(String msg) {
        Log.d("Events", msg);
    }

}

