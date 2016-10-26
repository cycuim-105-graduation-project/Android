package com.androidapp.beconnect.beconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

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

    }
}
