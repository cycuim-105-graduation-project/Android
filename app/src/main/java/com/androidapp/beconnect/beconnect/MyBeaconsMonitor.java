package com.androidapp.beconnect.beconnect;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Base64;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidapp.beconnect.beconnect.app.AppController;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import io.onebeacon.api.Beacon;
import io.onebeacon.api.BeaconsMonitor;
import io.onebeacon.api.Rangeable;
import io.onebeacon.api.spec.EddystoneUIDBeacon;

/** Example subclass for a BeaconsMonitor **/
class MyBeaconsMonitor extends BeaconsMonitor {

    EddystoneUIDBeacon EddystoneBeacon;
    int id;

    // 不可變 ID
    String namespaceId;
    String instanceId;
    String advertisedIdString;
    byte[] advertisedIdHex;
    String advertisedId;
    String macId;

    // 距離
    float  averageRssi;
    float  EstimatedDistance;
    String rangeName;

    String tag_string_req = "string_req";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context myBeaconsMonitor;
    ArrayList list = new ArrayList();


    public MyBeaconsMonitor(Context context) {
        super(context);
        myBeaconsMonitor = context;
    }

    @Override
    protected void onBeaconChangedRange(Rangeable rangeable) {
        super.onBeaconChangedRange(rangeable);
        log(String.format("Range changed to %s for %s", rangeable.getRange(), rangeable));
    }

    @Override
    protected void onBeaconChangedRssi(Beacon beacon) {
        super.onBeaconChangedRssi(beacon);
        if (beacon.getType() == Beacon.Type.EDDYSTONE_UID) {
            EddystoneBeacon    = (EddystoneUIDBeacon) beacon;
            id                 = EddystoneBeacon.getId();
            namespaceId        = EddystoneBeacon.getNamespaceId();
            macId              = EddystoneBeacon.getPrettyAddress();

            // EddystoneBeacon.getInstanceId() 沒有給我正確的 instance，從 address 自己撈
            instanceId         = EddystoneBeacon.getPrettyAddress().replace(":", "").toLowerCase();
            advertisedIdString = namespaceId.concat(instanceId);

            averageRssi        = EddystoneBeacon.getAverageRssi();
            EstimatedDistance  = EddystoneBeacon.getEstimatedDistance();
            rangeName          = EddystoneBeacon.getRangeName();

            getEncodeAdvertisedId(advertisedIdString);

            Log.d("macId:", macId);
            if (macId.equals(Values.CheckInNode)) {
                Values.nodeInRange = true;
                Log.d("In Range?", String.valueOf(Values.nodeInRange));
            }
            sendRequest(getEncodeAdvertisedId(advertisedIdString));
        }
    }

    @Override
    protected void onBeaconAdded(Beacon beacon) {
        super.onBeaconAdded(beacon);
        if (beacon.getType() == Beacon.Type.EDDYSTONE_UID) {
            EddystoneBeacon    = (EddystoneUIDBeacon) beacon;
            id                 = EddystoneBeacon.getId();
            namespaceId        = EddystoneBeacon.getNamespaceId();

            // EddystoneBeacon.getInstanceId() 沒有給我正確的 instance，從 address 自己撈
            instanceId         = EddystoneBeacon.getPrettyAddress().replace(":", "").toLowerCase();
            advertisedIdString = namespaceId.concat(instanceId);

//            log("fetch attachment_init");
//            sendRequest(getEncodeAdvertisedId(advertisedIdString));
        }

        // see Beacon.Type.* for more types, and io.onebeacon.api.spec.* for beacon type interfaces
    }

    // checkout the other available callbacks in the BeaconsManager base class

    //TODO 取得 encode advertisedId
    public String getEncodeAdvertisedId(String string) {

        try {
            advertisedIdHex = Hex.decodeHex(string.toCharArray());
            advertisedId    = Base64.encodeToString(advertisedIdHex, Base64.DEFAULT);

            Values.ID.add(advertisedId);

        } catch (DecoderException e) {
            e.printStackTrace();
        }

        return advertisedId;
    }

    public void pushNotification(String subject, String content) {
        // NotificationCompat (https://developer.android.com/guide/topics/ui/notifiers/notifications.html)
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(myBeaconsMonitor)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(subject)
                .setContentText(content);

        Intent resultIntent = new Intent(myBeaconsMonitor, News.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(myBeaconsMonitor);
        // Adds the back stack
        stackBuilder.addParentStack(News.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = mBuilder
            .setContentIntent(resultPendingIntent).build();
        NotificationManagerCompat.from(myBeaconsMonitor).notify(0, notification);
    }

    public void CheckAttachmentUpdate(String subject, String content, String type, String data) {
        String temp = content;

        if (!list.contains(temp)) {
            list.add(temp);
            pushNotification(subject, content);
            Snackbar.make(Values.container, "有新通知！" + content, Snackbar.LENGTH_LONG).show();
            Values.attachment.put(type, data);
        }
    }

    // TODO: sand request
    public void sendRequest(final String id) {
        String url = myBeaconsMonitor.getResources().getString(R.string.get_beacon_attachment);
        String jsonString = "{\"observations\": [{\"advertisedId\": {\"type\": \"EDDYSTONE\", \"id\": \"";
        jsonString       += id.replace("\n", "");
        jsonString       += "\"}}],\"namespacedTypes\": [\"*/*\"]}";

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
                Log.d("errer", error.toString());
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

                        CheckAttachmentUpdate(subject, content, type, data);
                    }
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

    private void log(String msg) {
        Log.d("MonitorService", msg);
    }
}