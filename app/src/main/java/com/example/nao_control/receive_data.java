package com.example.nao_control;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.URL;

public class receive_data extends AsyncTask<String, Void, String> {
    String received_json = "";
    HttpURLConnection connection = null;
    @Override
    protected String doInBackground(String... params){
        try{
            String sever_ip = "192.168.0.102";
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len=0;
            URL url = new URL(sever_ip);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setConnectTimeout(30*1000);
            //connection.setReadTimeout(5000);
            InputStream inStream = connection.getInputStream();
            while ((len = inStream.read(data))!= -1){
                outStream.write(data,0,len);
            }
            inStream.close();

            String key = new String(outStream.toByteArray());
            received_json = key;
            return key;

        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return "Executed";
    }
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    public String get_json(){
        return received_json;
    }
}
