package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.android.volley.toolbox.StringRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private EditText etIdentity;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword_confirmation;
    private Button bRegister;
    private TextView tvLoginLink;

    private static final String EMAIL_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    String url;
    String tag_string_req = "string_req";

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = new SessionManager(getApplicationContext());

        url = getResources().getString(R.string.url_register);

        etIdentity               = (EditText) findViewById(R.id.etIdentity);
        etEmail                  = (EditText) findViewById(R.id.etEmail);
        etPassword               = (EditText) findViewById(R.id.etPassword);
        etPassword_confirmation  = (EditText) findViewById(R.id.etPassword_confirmation);
        bRegister                = (Button)   findViewById(R.id.bRegister);
        tvLoginLink              = (TextView) findViewById(R.id.tvLoginLink);

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(loginIntent);
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String identity              = etIdentity.getText().toString();
                final String email                 = etEmail.getText().toString();
                final String password              = etPassword.getText().toString();
                final String password_confirmation = etPassword_confirmation.getText().toString();

                hideKeyboard();

                if (!validateEmail(email)) {
                    etEmail.setError("Not a valid email address!");
                } else if (!validatePassword(password)) {
                    etPassword.setError("Not a valid password!");
                } else if (!validatePasswordConfirmation(password, password_confirmation)) {
                    etPassword_confirmation.setError("Not equals to password!");
                } else {
                    etEmail.setError(null);
                    etPassword.setError(null);
                    etPassword_confirmation.setError(null);

                    StringRequest sr = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(RegisterActivity.this, "please check your mailbox to confirm this account", Toast.LENGTH_LONG).show();
                            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            RegisterActivity.this.startActivity(loginIntent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String json;
                            NetworkResponse response = error.networkResponse;
                            if(response != null && response.data != null){
                                json = new String(response.data);
                                String errors = trimMessage(json);

                                Toast.makeText(RegisterActivity.this, errors, Toast.LENGTH_LONG).show();
                            }
                        }
                    }){

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("identity",              identity);
                            params.put("email",                 email);
                            params.put("password",              password);
                            params.put("password_confirmation", password_confirmation);
                            return params;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(sr, tag_string_req);
                }
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
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }
    public boolean validatePasswordConfirmation(String password, String password_confirmation) {
        return (password.equals(password_confirmation));
    }

    public String trimMessage(String json){
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            JSONObject err = obj.getJSONObject("errors");
            JSONArray  arr = err.getJSONArray("full_messages");
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
