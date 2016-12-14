package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mitour on 2016/12/12.
 */

public class Agendas extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    String url_get_event_agendas;
    String url_Check_in_event_agendas;
    String tag_string_req = "string_req";
    String url_logout;
    SessionManager session;

    Button bCheckIn;

    final List<Agenda> agenda_list = new ArrayList<>();

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendas);

        Values.container = (CoordinatorLayout) findViewById(R.id.snackbar);

        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.rvAgendaList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // volley
        session = new SessionManager(getApplicationContext());
        url_get_event_agendas = getResources().getString(R.string.url_get_event_agendas);
        url_get_event_agendas = String.format(url_get_event_agendas, getIntent().getStringExtra("EXTRA_SESSION_ID"));

        JsonArrayRequest jar =
            new JsonArrayRequest(url_get_event_agendas,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("response", response.toString());
                    String id;
                    String date;
                    String time;
                    String name;
                    String description;
                    String indoor_level;
                    String speaker_name;
                    String speaker_description;
                    String speaker_image;
                    String start_at_date;
                    String start_at_time;
                    String end_at_time;
                    JSONObject agendaObject;
                    JSONObject dateObject;
                    JSONArray timeArray;
                    JSONObject timeObject;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            agendaObject = response.getJSONObject(i);
                            for (int j = 0; j < agendaObject.length(); j++) {
                                date = (String) agendaObject.names().get(j);
                                Log.d("date1111", date);
                                dateObject = agendaObject.getJSONObject(date);

                                for (int x = 0; x < dateObject.length(); x++){
                                    time = (String) dateObject.names().get(x);
                                    Log.d("time1111", time);
                                    timeArray = dateObject.getJSONArray(time);

                                    for (int y = 0; y < timeArray.length(); y++) {
                                        timeObject = timeArray.getJSONObject(y);
                                        Log.d("timeObject", timeObject.toString());
                                        id = timeObject.getString("id");
                                        name = timeObject.getString("name");
                                        description = timeObject.getString("description");
                                        indoor_level = timeObject.getString("indoor_level");
                                        speaker_name = timeObject.getJSONObject("speaker").getString("name");
                                        speaker_description = timeObject.getJSONObject("speaker").getString("description");
                                        speaker_image = timeObject.getJSONObject("speaker").getString("image");
                                        start_at_date = timeObject.getString("start_at_date");
                                        start_at_time = timeObject.getString("start_at_time");
                                        end_at_time = timeObject.getString("end_at_time");
                                        agenda_list.add(new Agenda(id, date, time, name, description, indoor_level, speaker_name, speaker_description, speaker_image, start_at_date, start_at_time, end_at_time));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", error.toString());
                }
            }
        );

        AppController.getInstance().addToRequestQueue(jar, tag_string_req);

        adapter = new CustomAdapter(this, agenda_list);
        recyclerView.setAdapter(adapter);

        // 藍芽
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

    public boolean ifCanCheckIn(String start_at_date, String start_at_time, String end_at_time, String indoor_level) {
        boolean TF = false;
        boolean speechIsOn = false;
        boolean ifRightPlace = false;

        // 確認是否是對的地點
        if (indoor_level.equals(Values.place)) {
            ifRightPlace = true;
        } else {
            ifRightPlace = false;
        }

        // 計算演講是否進行中
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date getDate = new Date();
        String currentDateTime = formatDateTime.format(getDate);
        String startAtDateTime = start_at_date.concat(" ").concat(start_at_time);
        String endAtDateTime   = start_at_date.concat(" ").concat(end_at_time);

        try {
            Date currentDateType = formatDateTime.parse(currentDateTime);
            Date startDateType   = formatDateTime.parse(startAtDateTime);
            Date endDateType     = formatDateTime.parse(endAtDateTime);

            Long currentUnixType = currentDateType.getTime();
            Long startUnixType   = startDateType.getTime();
            Long endUnixType     = endDateType.getTime();

            // 計算到分鐘差
            boolean ifStart = (currentUnixType - startUnixType   / 1000 * 60) > 0;
            boolean ifEnd   = (endUnixType     - currentUnixType / 1000 * 60) > 0;

            if (ifStart && ifEnd) {
                speechIsOn = true;
            } else {
                speechIsOn = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (ifRightPlace && speechIsOn) {
            TF = true;
        } else if (ifRightPlace && !speechIsOn) {
            // wrong timing
        } else if (!ifRightPlace && speechIsOn) {
            // wrong place
        } else {
            TF = false;
        }

        return TF;
    }

    // 自定義 ListView (http://huli.logdown.com/posts/280137-android-custom-listview)
    public static class Agenda {

        private String id;
        private String date;
        private String time;
        private String name;
        private String description;
        private String indoor_level;
        private String speaker_name;
        private String speaker_description;
        private String speaker_image;
        private String start_at_date;
        private String start_at_time;
        private String end_at_time;

        public Agenda(String id, String date, String time, String name, String description, String indoor_level, String speaker_name, String speaker_description, String speaker_image, String start_at_date, String start_at_time, String end_at_time) {
            this.id = id;
            this.date = date;
            this.time = time;
            this.name = name;
            this.description = description;
            this.indoor_level = indoor_level;
            this.speaker_name = speaker_name;
            this.speaker_description = speaker_description;
            this.speaker_image = speaker_image;
            this.start_at_date = start_at_date;
            this.start_at_time = start_at_time;
            this.end_at_time = end_at_time;
        }

        public void setDate(String date){
            this.date = date;
        }
        public void setTime(String time){
            this.time = time;
        }

        public String getId(){
            return id;
        }
        public String getDate(){
            return date;
        }
        public String getTime(){
            return time;
        }
        public String getName(){
            return name;
        }
        public String getDescription(){
            return description;
        }
        public String getIndoor_level(){
            return indoor_level;
        }
        public String getSpeaker_name(){
            return speaker_name;
        }
        public String getSpeaker_description(){
            return speaker_description;
        }
        public String getSpeaker_image(){
            return speaker_image;
        }
        public String getStart_at_date(){
            return start_at_date;
        }
        public String getStart_at_time(){
            return start_at_time;
        }
        public String getEnd_at_time(){
            return end_at_time;
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ImageLoader imageLoader;
        private Context context;
        private List<Agenda> mItemList = null;

        public CustomAdapter(Context context, List<Agenda> agenda_list){
            this.mItemList = agenda_list;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_agendas_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Agenda items = mItemList.get(position);
            holder.tvDate.setText(items.getDate());
            holder.tvTime.setText(items.getTime());
            holder.tvName.setText(items.getName());
            holder.tvDescription.setText(items.getDescription());
            holder.tvIndoor_level.setText(items.getIndoor_level());
            holder.tvSpeakerName.setText(items.getSpeaker_name());
            holder.tvSpeakerDescription.setText(items.getSpeaker_description());
            holder.bCheckIn.setId(Integer.parseInt(items.getId()));

            imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
            imageLoader.get(items.speaker_image, ImageLoader.getImageListener(holder.ivSpeakerImage, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

            holder.ivSpeakerImage.setImageUrl(items.speaker_image, imageLoader);
            final String EventAgendaId = getIntent().getStringExtra("EXTRA_SESSION_ID").concat("-").concat(items.getId());

            if (Values.checkIn.contains(EventAgendaId)) {
                holder.bCheckIn.setText("已簽到");
            } else {

                if (ifCanCheckIn(items.getStart_at_date(), items.getStart_at_time(), items.getEnd_at_time(), items.getIndoor_level())) {
                    holder.bCheckIn.setText("可以簽到");
                    if (session.isLoggedIn()) {
                        url_Check_in_event_agendas = getResources().getString(R.string.url_Check_in_event_agendas);
                        url_Check_in_event_agendas = String.format(url_Check_in_event_agendas, items.getId());
                        HashMap<String, String> user = session.getUserDetails();

                        final String uid = user.get(SessionManager.KEY_UID);
                        final String access_token = user.get(SessionManager.KEY_ACCESS_TOKEN);
                        final String client = user.get(SessionManager.KEY_CLIENT);

                        holder.bCheckIn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StringRequest sr = new StringRequest(Request.Method.POST, url_Check_in_event_agendas, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(Agendas.this, "已簽到 enjoy it!", Toast.LENGTH_LONG).show();
                                        Values.checkIn.add(EventAgendaId);

                                        Intent intent = new Intent(Agendas.this, Events.class);
                                        Agendas.this.startActivity(intent);
                                        finish();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("error when checkin", error.toString());
                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("access-token", access_token);
                                        params.put("uid", uid);
                                        params.put("client", client);
                                        return params;
                                    }
                                };
                                AppController.getInstance().addToRequestQueue(sr, tag_string_req);
                            }
                        });
                    } else {
                        holder.bCheckIn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(Agendas.this, "請先登入", Toast.LENGTH_LONG).show();
                                //                            Snackbar.make(container, "請先登入", Snackbar.LENGTH_LONG).setAction("前往登入", new View.OnClickListener() {
                                //                                @Override
                                //                                public void onClick(View v) {
                                //                                    Intent loginIntent = new Intent(Agendas.this, LoginActivity.class);
                                //                                    Agendas.this.startActivity(loginIntent);
                                //                                }
                                //                            }).show();
                            }
                        });
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView tvDate;
            public TextView tvTime;
            public TextView tvName;
            public TextView tvDescription;
            public TextView tvIndoor_level;
            public TextView tvSpeakerName;
            public TextView tvSpeakerDescription;
            public NetworkImageView ivSpeakerImage;
            public Button bCheckIn;

            public ViewHolder(View itemView){
                super(itemView);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
                tvIndoor_level = (TextView) itemView.findViewById(R.id.tvIndoor_level);
                tvSpeakerName = (TextView) itemView.findViewById(R.id.tvSpeakerName);
                tvSpeakerDescription = (TextView) itemView.findViewById(R.id.tvSpeakerDescription);
                ivSpeakerImage = (NetworkImageView) itemView.findViewById(R.id.ivSpeakerImage);
                bCheckIn = (Button) itemView.findViewById(R.id.bCheckIn);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    // menu
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
                                    Toast.makeText(Agendas.this, "Logout success!", Toast.LENGTH_LONG).show();
                                    session.logoutUser();
                                }

                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(Agendas.this, error.toString(), Toast.LENGTH_LONG).show();
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
