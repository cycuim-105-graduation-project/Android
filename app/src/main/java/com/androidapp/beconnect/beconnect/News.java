package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.beconnect.beconnect.app.AppController;
import com.google.common.collect.Iterables;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class News extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private ListView lvNewsList;
    final List<Attachment> attachment_list = new ArrayList<>();
    private CustomAdapter ListViewadapter;

    String tag_string_req = "string_req";
    String url_logout;
    private static final String TAG = MainActivity.class.getSimpleName();

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        lvNewsList = (ListView) findViewById(R.id.lvNewsList);
        ArrayList myList = new ArrayList();
        session = new SessionManager(getApplicationContext());

        fetchAttachment();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // 延遲 5 秒
                        Thread.sleep( 5000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            attachment_list.clear();
                            fetchAttachment();
                        }
                    });
                }
            }
        }).start();

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

    public void fetchAttachment() {
        Set<String> keys = Values.attachment.keySet();
        try {
            for (String key : keys) {
                String type = key;
                Collection<String> data = Values.attachment.get(key);

                for (int i = 0; i < data.size(); i++) {
                    JSONObject attachmentJson = new JSONObject(Iterables.get(data, i));
                    String category   = (String) attachmentJson.get("category");
                    String subject    = (String) attachmentJson.get("subject");
                    String content    = (String) attachmentJson.get("content");
                    String attachment = (String) attachmentJson.get("attachment");

                    attachment_list.add(new Attachment(type, category, subject, content, attachment));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListViewadapter = new CustomAdapter(News.this, attachment_list);
        lvNewsList.setAdapter(ListViewadapter);
    }

    // 自定義 ListView (http://huli.logdown.com/posts/280137-android-custom-listview)
    public class Attachment {

        private String type;
        private String category;
        private String subject;
        private String content;
        private String attachment;

        public Attachment(String type, String category, String subject, String content, String attachment) {
            this.type       = type;
            this.category   = category;
            this.subject    = subject;
            this.content    = content;
            this.attachment = attachment;
        }

        public void setType(String type){
            this.type = type;
        }
        public void setCategory(String category){
            this.category = category;
        }
        public void setSubject(String subject){
            this.subject = subject;
        }
        public void setContent(String content){
            this.content = content;
        }
        public void setAttachment(String attachment){
            this.attachment = attachment;
        }

        public String getType(){
            return type;
        }
        public String getCategory(){
            return category;
        }
        public String getSubject(){
            return subject;
        }
        public String getContent(){
            return content;
        }
        public String getAttachment(){
            return attachment;
        }
    }

    public class CustomAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        private List<Attachment> attachments;

        public CustomAdapter(Context context, List<Attachment> attachment_list) {
            myInflater = LayoutInflater.from(context);
            this.attachments = attachment_list;
        }

        /*private view holder class*/
        private class ViewHolder {
            TextView tvType;
            TextView tvCategory;
            TextView tvSubject;
            TextView tvContent;
            TextView tvAttachment;

            public ViewHolder(TextView type, TextView category, TextView subject, TextView content, TextView attachment){
                this.tvType       = type;
                this.tvCategory   = category;
                this.tvSubject    = subject;
                this.tvContent    = content;
                this.tvAttachment = attachment;
            }
        }

        @Override
        public int getCount() {
            return attachments.size();
        }

        @Override
        public Object getItem(int arg0) {
            return attachments.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return attachments.indexOf(getItem(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = myInflater.inflate(R.layout.activity_news_list_item, null);
                holder = new ViewHolder(
                        (TextView) convertView.findViewById(R.id.tvType),
                        (TextView) convertView.findViewById(R.id.tvCategory),
                        (TextView) convertView.findViewById(R.id.tvSubject),
                        (TextView) convertView.findViewById(R.id.tvContent),
                        (TextView) convertView.findViewById(R.id.tvAttachment)
                );
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Attachment Attachment = (Attachment) getItem(position);
            holder.tvType.setText(Attachment.getType());
            holder.tvCategory.setText(Attachment.getCategory());
            holder.tvSubject.setText(Attachment.getSubject());
            holder.tvContent.setText(Attachment.getContent());
            if (Attachment.getAttachment().equals("無")) {
                holder.tvAttachment.setVisibility(View.GONE);
            } else {
                holder.tvAttachment.setText(Attachment.getAttachment());
            }

            return convertView;
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
                                    Toast.makeText(News.this, "Logout success!", Toast.LENGTH_LONG).show();
                                    session.logoutUser();
                                }

                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(News.this, error.toString(), Toast.LENGTH_LONG).show();
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
