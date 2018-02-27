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
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import 	java.net.URL;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static android.content.ContentValues.TAG;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static java.net.Proxy.Type.HTTP;

/**
 * Created by Harrison on 2/14/18.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private Criteria criteria;
    private LocationManager mLocationManager;
    private Context context;
    private MyLocationListener listener;
    private MyLocationListener listeners[]={new MyLocationListener(), new MyLocationListener()};
    private TextView textbox;
    private Button start;
    private Button end;
    private String MyId="no id";
    String address="69.249.186.83";
    private FileOutputStream myoutput=null;
    int port=10002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        textbox=(TextView)findViewById(R.id.output_text);
        start=(Button)findViewById(R.id.start_service);
        end=(Button)findViewById(R.id.stop_service);
        if(start==null){
            System.out.println("======================================================");
        }

        start.setOnClickListener(this);
        end.setOnClickListener(this);
    }
    private void startreceiving(){
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置定位精准度
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); MyId = tm.getDeviceId();
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1,  0f, listeners[0]);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,  0f, listeners[1]);
        }
        else{
            System.out.println("No permission");
            //need extra code to grant the permission
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }

    //停止地理位置更新
    public void stopRequestLocationUpdates(){
//        mLocationManager.removeUpdates(listeners[0]);
mLocationManager.removeUpdates(listeners[1]);
    }

    public Location getCurrentLocation() {
        // go in best to worst order
//        Location l1=listeners[0].current();
//        Location l2=listeners[1].current();
//        if(l1==null && l2!=null){
//            return l2;
//        }
//        if(l2==null && l1!=null){
//            return l1;
//        }
//        if(l1!=null&&l2!=null){
//            if(l1.getAccuracy()<l2.getAccuracy()){
//                return l1;
//            }
//            else{
//                return l2;
//            }
//        }
//        return null;
        return listeners[1].current();

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                String filename=getsensorFilename();
                try{
                    myoutput=new FileOutputStream(filename,true);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                this.startreceiving();

                break;
            case R.id.stop_service:
                stopRequestLocationUpdates();
                try {
                    myoutput.close();
                }catch(IOException e){
                    e.getStackTrace();
                }
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
            if(newLocation.getAccuracy()>10){
                return;
            }
            if (!mValid) {
                Log.d(TAG, "Got first location.");
            }

            System.out.println("#################   "+newLocation.getAccuracy());
            mLastLocation=new Location(newLocation);
            String message=newLocation.getTime()+" "+newLocation.getLongitude()+" "+newLocation.getLatitude()+" "+MyId+"\n";
            try {
                myoutput.write(message.getBytes());
                myoutput.flush();
            }catch(IOException e){
                e.getStackTrace();
            }
//            Log.d(TAG, "the newLocation is " + newLocation.getLongitude() + "x" + newLocation.getLatitude());
//            String msg= MyId+" the newLocation is " + newLocation.getLongitude() + "x" + newLocation.getLatitude()+"\n";
            textbox.append("\nthe newLocation is found " + newLocation.getAccuracy());
//            new SocketRequest(address, port, msg).start();
            Map<String, String> params = new HashMap<String, String>();
            params.put("longitude",  newLocation.getLongitude()+"");
            params.put("latitude",  newLocation.getLatitude()+"");
            params.put("ID",MyId);
            new DoPostRequest(params, "utf-8").start();
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

    public void senddata(Map<String, String> params, String encode){
        byte[] data = getRequestData(params, encode).toString().getBytes();
        try {
            URL url = new URL("http://10.0.0.67/");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);//设置连接超时时间
            httpURLConnection.setDoInput(true);//打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);//打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");//设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);//使用Post方式不能使用缓存
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
//                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
                for(Map.Entry<String, String> entry : params.entrySet())
                {
                    stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
                }
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
         }
        return stringBuffer;
    }
    private String getsensorFilename(){
        String filepath= Environment.getExternalStorageDirectory().getParent();
        File path=getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file=new File(path.getAbsolutePath(),"gps");
        if(!file.exists()){
            file.mkdirs();
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime=sdf.format(new Date());
        return (file.getAbsolutePath()+"/"+currentDateandTime+".txt");
    }

}
