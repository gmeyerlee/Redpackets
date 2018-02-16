package com.tucis.jiacheng.redpackets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 * Created by Harrison on 2/14/18.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private Criteria criteria;
    private LocationManager mLocationManager;
    private Context context;
    private MyLocationListener listener;
    TextView textbox;
    Button start;
    Button end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        this.textbox=(TextView)findViewById(R.id.output_text);
        this.start=(Button)findViewById(R.id.start_service);
        this.end=(Button)findViewById(R.id.stop_service);
        start.setOnClickListener(this);
        end.setOnClickListener(this);
    }
    private void startreceiving(){
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_HIGH);//设置定位精准度
        criteria.setAltitudeRequired(false);//是否要求海拔
        criteria.setBearingRequired(true);//是否要求方向
        criteria.setCostAllowed(true);//是否要求收费
        criteria.setSpeedRequired(true);//是否要求速度
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);//设置电池耗电要求
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);//设置方向精确度
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);//设置速度精确度
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);//设置水平方向精确度
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);//设置垂直方向精确度
        //返回满足条件的，当前设备可用的location provider，当第二个参数为false时，返回当前设备所有provider中最符合条件的那个provider（但是不一定可用）。
        String mProvider  = mLocationManager.getBestProvider(criteria,true);
        listener = new MyLocationListener();
        this.startRequestLocationUpdates();
    }
    public void startRequestLocationUpdates(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, listener);
        }
        else{
            System.out.println("No permission");
            //need extra code to grant the permission
        }
    }

    //停止地理位置更新
    public void stopRequestLocationUpdates(){
        mLocationManager.removeUpdates(listener);
    }

    public Location getCurrentLocation() {
        return listener.current();
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                this.startreceiving();
                break;
            case R.id.stop_service:
                break;
            default:
                break;
        }
    }
    private class MyLocationListener implements LocationListener {
        Location mLastLocation;
        boolean mValid = false;

        @Override
        public void onLocationChanged(Location newLocation) {
            if (newLocation.getLatitude() == 0.0
                    && newLocation.getLongitude() == 0.0) {
                // Hack to filter out 0.0,0.0 locations
                return;
            }
            if (!mValid) {
                Log.d(TAG, "Got first location.");
            }
            mLastLocation.set(newLocation);
            Log.d(TAG, "the newLocation is " + newLocation.getLongitude() + "x" + newLocation.getLatitude());
            textbox.append("the newLocation is " + newLocation.getLongitude() + "x" + newLocation.getLatitude());
            mValid = true;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                    mValid = false;
                    break;
                }
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, " support current " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "no support current " + provider);
            mValid = false;
        }

        //获得当前地理位置
        public Location current() {
            return mValid ? mLastLocation : null;
        }
    }
}
