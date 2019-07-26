package com.example.nao_control;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class receive_socket extends AsyncTask<String, Void, String> {private String IP_add = "192.168.0.102";
    int port_num = 9556;
    private Socket socket;

    private static String received_json;
    @Override
    protected String doInBackground(String... params){
        BufferedReader br;
        InputStreamReader isr;
        while (true) {
            try {

                ServerSocket serverSocket = new ServerSocket(port_num);

                Socket socket = serverSocket.accept();
                JSONObject jsonObject = new JSONObject();

                OutputStream outputStream = socket.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(jsonObject.toString() + "\n");

                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);
                String str = br.readLine();
                //received_json = jsonObject.toString();
                received_json = str;
                bufferedWriter.flush();

                socket.shutdownOutput();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Void... values) {}


    public void setIp(String sever_ip){
        IP_add = sever_ip;
    }
    public String get_json(){

        return received_json;
    }
}