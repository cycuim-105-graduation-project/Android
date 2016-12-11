package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import java.util.HashMap;
import java.util.Map;

import static com.androidapp.beconnect.beconnect.Values.container;

public class TicketTwo extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    Button bOKTicket;

    SessionManager session;
    String url_logout;
    String tag_string_req = "string_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_two);

        session = new SessionManager(getApplicationContext());

        session.checkLogin();

        Values.container = (CoordinatorLayout) findViewById(R.id.snackbar);

        bOKTicket = (Button) findViewById(R.id.bOKTicket);

        if (Values.ifCheckIn[1] == true) {
            bOKTicket.setText("已報到");
            bOKTicket.setClickable(false);
            bOKTicket.setBackgroundColor(Color.TRANSPARENT);
            bOKTicket.setTextColor(Color.argb(100, 0, 0, 0));
        } else if (Values.ifCheckIn[1] == false && Values.nodeInRange == true) {
            bOKTicket.setText("報到");
            Values.ifCheckIn[1] = true;
            bOKTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TicketTwo.this, "報到成功，歡迎您！", Toast.LENGTH_SHORT).show();
                    Intent EventsIntent = new Intent(TicketTwo.this, Events.class);
                    TicketTwo.this.startActivity(EventsIntent);
                }
            });
        } else if (Values.ifCheckIn[1] == false && Values.nodeInRange == false) {
            bOKTicket.setText("未在報到入口範圍內");
            bOKTicket.setClickable(false);
            bOKTicket.setBackgroundColor(Color.TRANSPARENT);
            bOKTicket.setTextColor(Color.argb(100, 0, 0, 0));
        }

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
                                    Toast.makeText(TicketTwo.this, "Logout success!", Toast.LENGTH_LONG).show();
                                    session.logoutUser();
                                }

                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(TicketTwo.this, error.toString(), Toast.LENGTH_LONG).show();
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
}
