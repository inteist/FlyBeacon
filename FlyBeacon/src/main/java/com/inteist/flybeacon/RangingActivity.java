package com.inteist.flybeacon;

import java.util.Collection;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.Region;
import com.radiusnetworks.ibeacon.RangeNotifier;

import android.app.Activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RangingActivity extends Activity implements IBeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

    private double prevDist = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        iBeaconManager.bind(this);
        TextView editText = (TextView) RangingActivity.this
                .findViewById(R.id.rangingText);
        editText.setText("? ft");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, false);
    }

    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                if (iBeacons.size() > 0) {
                    TextView editText = (TextView) RangingActivity.this
                            .findViewById(R.id.rangingText);

                    com.radiusnetworks.ibeacon.IBeacon beacon  = iBeacons.iterator().next();

                    String uuid = beacon.getProximityUuid();
                    if(uuid.compareTo("b9407f30-f5f8-466e-aff9-25556b57fe6d") == 0){
                        Log.d("FB", "BINGO:" + uuid);
                        logToDisplay(beacon.getAccuracy() * 3.3);
                    }
                    else {
                        Log.d("FB", "wrong UDID");
                    }


                }
            }

        });

        try {
            iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    private void logToDisplay(final double dist) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView editText = (TextView) RangingActivity.this
                        .findViewById(R.id.rangingText);
                Math.round(dist);

                if(Math.abs(prevDist - dist) > 4){
                    prevDist = dist;
                    editText.setText(Math.round(dist) + " ft");
                }

                if(dist < 3) {
                    RelativeLayout arrivedView = (RelativeLayout) findViewById(R.id.arrived_container);
                    arrivedView.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);

                }




            }
        });
    }


    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView editText = (TextView) RangingActivity.this
                        .findViewById(R.id.rangingText);
                editText.setText(line);

            }
        });
    }
}
