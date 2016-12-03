package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPassword extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private TextView tvIdentity;
    private TextView tvEmail;
    private EditText etCurrentPassword;
    private EditText etPassword;
    private EditText etPasswordConfirmation;
    private Button   bEdit;

    SessionManager session;

    String url_validate_token;
    String url_edit_password;
    String tag_string_req = "string_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        tvIdentity             = (TextView) findViewById(R.id.tvIdentity);
        tvEmail                = (TextView) findViewById(R.id.tvEmail);
        etCurrentPassword      = (EditText) findViewById(R.id.etCurrentPassword);
        etPassword             = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirmation = (EditText) findViewById(R.id.etPasswordConfirmation);
        bEdit                  = (Button)   findViewById(R.id.bEdit);

        url_validate_token    = getResources().getString(R.string.url_validate_token);
        url_edit_password     = getResources().getString(R.string.url_edit_password);

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditPassword.this, "error", Toast.LENGTH_LONG).show();
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

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String current_password      = etCurrentPassword.getText().toString();
                final String password              = etPassword.getText().toString();
                final String password_confirmation = etPasswordConfirmation.getText().toString();

                StringRequest sr = new StringRequest(Request.Method.PUT, url_edit_password , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditPassword.this, "Update success", Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(EditPassword.this, LoginActivity.class);
                        EditPassword.this.startActivity(loginIntent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String json;
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            json = new String(response.data);
                            String errors = trimMessage(json);

                            Toast.makeText(EditPassword.this, errors, Toast.LENGTH_LONG).show();
                        }
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("current_password",      current_password);
                        params.put("password",              password);
                        params.put("password_confirmation", password_confirmation);
                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("client",       client);
                        params.put("uid",          uid);
                        params.put("access-token", access_token);
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

    public String trimMessage(String json){
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            JSONObject err = obj.getJSONObject("errors");
            JSONArray arr = err.getJSONArray("full_messages");
            trimmedString  = arr.toString(0).replace("[", "").replace("]", "").replace("\"", "").trim();
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return trimmedString;
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
                Intent Ticketintent = new Intent(this, Ticket.class);
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
