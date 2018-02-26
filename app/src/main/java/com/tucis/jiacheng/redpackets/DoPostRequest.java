package com.tucis.jiacheng.redpackets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by tug07865 on 2/18/2018.
 */

public class DoPostRequest extends Thread{
    private Map<String, String> params;
    private String encode;
    private static final String URL_PATH = "http://96.126.120.53:8080/MyServer/MyServer";
    private static URL url;





    public DoPostRequest(Map<String, String> params, String encode) {
        // TODO Auto-generated constructor stub
        this.params=params;
        this.encode=encode;
        try {
            url = new URL(URL_PATH);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        byte[] data = getRequestData(params, encode).toString().getBytes();
        try {
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
            System.out.println(response+"");

            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                String myreply=dealResponseResult(inptStream);
                System.out.println( "The length of data is : "+ myreply.length());                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            System.out.println("Exception!!!!!!!!!!!!!!!!!!!!!!");
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
public static String dealResponseResult(InputStream inputStream) {
String resultData = null;      //存储处理结果
ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
byte[] data = new byte[1024];
int len = 0;
try {
while((len = inputStream.read(data)) != -1) {
byteArrayOutputStream.write(data, 0, len);
}
} catch (IOException e) {
    e.printStackTrace();
}
resultData = new String(byteArrayOutputStream.toByteArray());
return resultData;
}

}
