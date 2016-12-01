package com.androidapp.beconnect.beconnect;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import io.onebeacon.api.Beacon;
import io.onebeacon.api.BeaconsMonitor;
import io.onebeacon.api.Rangeable;
import io.onebeacon.api.spec.EddystoneUIDBeacon;

/** Example subclass for a BeaconsMonitor **/
class MyBeaconsMonitor extends BeaconsMonitor {

    EddystoneUIDBeacon EddystoneBeacon;
    int id;

    // 不可變 ID
    String  namespaceId;
    String  instanceId;
    String  advertisedIdString;
    byte[]  advertisedIdHex;
    String  advertisedId;

    // 距離
    float  averageRssi;
    float  EstimatedDistance;
    String rangeName;

    public MyBeaconsMonitor(Context context) {
        super(context);
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

            // EddystoneBeacon.getInstanceId() 沒有給我正確的 instance，從 address 自己撈
            instanceId         = EddystoneBeacon.getPrettyAddress().replace(":", "").toLowerCase();
            advertisedIdString = namespaceId.concat(instanceId);

            averageRssi        = EddystoneBeacon.getAverageRssi();
            EstimatedDistance  = EddystoneBeacon.getEstimatedDistance();
            rangeName          = EddystoneBeacon.getRangeName();

            getEncodeAdvertisedId(advertisedIdString);

        }
    }

    @Override
    protected void onBeaconAdded(Beacon beacon) {
        super.onBeaconAdded(beacon);
        if (beacon.getType() == Beacon.Type.EDDYSTONE_UID) {
            EddystoneBeacon    = (EddystoneUIDBeacon) beacon;
            namespaceId        = EddystoneBeacon.getNamespaceId();
            instanceId         = EddystoneBeacon.getPrettyAddress();

        }

        // see Beacon.Type.* for more types, and io.onebeacon.api.spec.* for beacon type interfaces
    }

    // checkout the other available callbacks in the BeaconsManager base class

    //TODO 取得 encode advertisedId
    public String getEncodeAdvertisedId(String string) {

        try {
            advertisedIdHex = Hex.decodeHex(string.toCharArray());
            advertisedId    = Base64.encodeToString(advertisedIdHex, Base64.DEFAULT);
            Log.d("advertisedId: ", advertisedId);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return advertisedIdString;
    }

    private void log(String msg) {
        Log.d("MonitorService", msg);
    }
}