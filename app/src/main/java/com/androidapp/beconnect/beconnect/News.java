package com.androidapp.beconnect.beconnect;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class News extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private ListView lvNewsList;
    final List<Attachment> attachment_list = new ArrayList<>();
    private CustomAdapter ListViewadapter;

    String tag_string_req = "string_req";
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        lvNewsList = (ListView) findViewById(R.id.lvNewsList);
        ArrayList myList = new ArrayList();

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

        Iterator iterator = Values.ID.iterator();
        while (iterator.hasNext()) {
            myList.add(iterator.next());
        }

        for (int i = 0; i < myList.size(); i++) {
            sendRequest((String) myList.get(i));
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

    // TODO: sand request
    public void sendRequest(String id) {
        String url = getResources().getString(R.string.get_beacon_attachment);
        String jsonString = "{\"observations\": [{\"advertisedId\": {\"type\": \"EDDYSTONE\", \"id\": \"";
               jsonString += id.replace("\n", "");
               jsonString += "\"}}],\"namespacedTypes\": [\"*/*\"]}";

        Log.d("jsonString", jsonString);

        final String finalJsonString = jsonString;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json;
                NetworkResponse response = error.networkResponse;
                Log.d("errer", error.toString());
                Toast.makeText(News.this, String.valueOf(error), Toast.LENGTH_LONG).show();

                if (response != null && response.data != null) {
                    json = new String(response.data);
                    String errors = trimMessage(json);

                    Toast.makeText(News.this, errors, Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public byte[] getBody() {
                try {
                    return finalJsonString.getBytes(PROTOCOL_CHARSET);
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            finalJsonString, PROTOCOL_CHARSET);
                    return null;
                }
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    JSONObject obj = new JSONObject(jsonString);

                    JSONArray attachments = obj.getJSONArray("beacons").getJSONObject(0).getJSONArray("attachments");

                    for (int i = 0; i < attachments.length(); i++) {
                        String namespacedType = (String) attachments.getJSONObject(i).get("namespacedType");
                        String attachmentEncode = (String) attachments.getJSONObject(i).get("data");

                        // Decode attachments
                        byte[] temp = Base64.decode(attachmentEncode, Base64.DEFAULT);
                        String data = new String(temp, StandardCharsets.UTF_8);

                        // Replace substring
                        String type = namespacedType.substring(22);

                        // parse response
                        JSONObject attachmentJson = new JSONObject(data);
                        String category   = (String) attachmentJson.get("category");
                        String subject    = (String) attachmentJson.get("subject");
                        String content    = (String) attachmentJson.get("content");
                        String attachment = (String) attachmentJson.get("attachment");

                        // Add type, attachment to list
                        attachment_list.add(new Attachment(type, category, subject, content, attachment));
                    }

                    // To avoid 'Only the original thread that created a view hierarchy can touch its views'
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ListViewadapter = new CustomAdapter(News.this, attachment_list);

                            lvNewsList.setAdapter(ListViewadapter);
                        }
                    });


                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
    }

    // 讓回傳 JSON 易讀
    public String trimMessage(String json) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray arr  = obj.getJSONArray("errors");
            trimmedString  = arr.toString().replace("[", "").replace("]", "").replace("\"", "");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return trimmedString;
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

        public CustomAdapter(Context context, List<Attachment> attachment_list){
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
            if (convertView==null) {
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

            Attachment Attachment = (Attachment)getItem(position);
            holder.tvType.setText(Attachment.getType());
            holder.tvCategory.setText(Attachment.getCategory());
            holder.tvSubject.setText(Attachment.getSubject());
            holder.tvContent.setText(Attachment.getContent());
            holder.tvAttachment.setText(Attachment.getAttachment());

            return convertView;
        }
    }
}
