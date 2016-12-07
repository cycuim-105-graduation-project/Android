package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private TextView tvIdentity;
    private TextView tvEmail;
    private TextView tvNickname;
    private TextView tvFirstname;
    private TextView tvLastname;
    private TextView tvCellphone;
    private TextView tvZipcode;
    private TextView tvAddress;
    private TextView tvCompany;
    private TextView tvCompanyAddress;
    private TextView tvJobTitle;
    private Button bEditProfile;
    private Button   bEditPassword;
    private Button   bLogout;

    SessionManager session;
    String url_validate_token;
    String url_logout;
    String tag_string_req = "string_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvIdentity       = (TextView) findViewById(R.id.tvIdentity);
        tvEmail          = (TextView) findViewById(R.id.tvEmail);
        tvNickname       = (TextView) findViewById(R.id.tvNickname);
        tvLastname       = (TextView) findViewById(R.id.tvLastname);
        tvFirstname      = (TextView) findViewById(R.id.tvFirstname);
        tvCellphone      = (TextView) findViewById(R.id.tvCellphone);
        tvZipcode        = (TextView) findViewById(R.id.tvZipcode);
        tvAddress        = (TextView) findViewById(R.id.tvAddress);
        tvCompany        = (TextView) findViewById(R.id.tvCompany);
        tvCompanyAddress = (TextView) findViewById(R.id.tvCompanyAddress);
        tvJobTitle       = (TextView) findViewById(R.id.tvJobTitle);
        bEditProfile     = (Button)   findViewById(R.id.bEditProfile);
        bEditPassword    = (Button)   findViewById(R.id.bEditPassword);
        bLogout          = (Button)   findViewById(R.id.bLogout);

        url_validate_token = getResources().getString(R.string.url_validate_token);
        url_logout         = getResources().getString(R.string.url_logout);

        session = new SessionManager(getApplicationContext());

        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();

        final String uid          = user.get(SessionManager.KEY_UID);
        final String access_token = user.get(SessionManager.KEY_ACCESS_TOKEN);
        final String client       = user.get(SessionManager.KEY_CLIENT);

        JsonObjectRequest jor = new JsonObjectRequest(url_validate_token, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    tvIdentity.setText(response.getJSONObject("data").getString("identity"));
                    tvEmail.setText(response.getJSONObject("data").getString("email"));
                    tvNickname.setText(response.getJSONObject("data").getString("nickname"));
                    tvFirstname.setText(response.getJSONObject("data").getString("firstname"));
                    tvLastname.setText(response.getJSONObject("data").getString("lastname"));
                    tvCellphone.setText(response.getJSONObject("data").getString("cellphone"));
                    tvZipcode.setText(response.getJSONObject("data").getString("zipcode"));
                    tvAddress.setText(response.getJSONObject("data").getString("address"));
                    tvCompany.setText(response.getJSONObject("data").getString("company"));
                    tvCompanyAddress.setText(response.getJSONObject("data").getString("company_address"));
                    tvJobTitle.setText(response.getJSONObject("data").getString("job_title"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("access-token", access_token);
                params.put("client",       client);
                params.put("uid",          uid);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jor, tag_string_req);

        bEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditUserProfileIntent = new Intent(ProfileActivity.this, EditUserProfile.class);
                ProfileActivity.this.startActivity(EditUserProfileIntent);
            }
        });

        bEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditUserPasswordIntent = new Intent(ProfileActivity.this, EditPassword.class);
                ProfileActivity.this.startActivity(EditUserPasswordIntent);
            }
        });

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest sr = new StringRequest(Request.Method.DELETE, url_logout,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(ProfileActivity.this, "Logout success!", Toast.LENGTH_LONG).show();
                                session.logoutUser();
                            }

                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ProfileActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
                    Intent Loginintent = new Intent(this, LoginActivity.class);
                    this.startActivity(Loginintent);
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
