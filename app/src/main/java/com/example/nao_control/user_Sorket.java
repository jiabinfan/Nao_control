package com.example.nao_control;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.lang.Void;

public class user_Sorket extends AsyncTask<String, Void, String> {
    private String IP_add = "192.168.0.102";
    int port_num = 9552;
    private Socket client;
    PrintWriter printWriter;
    private String message;
    @Override
    protected String doInBackground(String... params){
        try{

            client = new Socket(IP_add, port_num);

            //printWriter = new PrintWriter(client.getOutputStream(),true);
            //PrintStream ps = new PrintStream(sock.getOutputStream());
            //ps.println();
            //printWriter.write(message);
            OutputStream out = client.getOutputStream();
            PrintWriter output = new PrintWriter(out);

            output.println(message);
            output.close();
            out.close();
            client.close();
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
    //DataOutputStream DOS = new DataOutputStream(sock.getOutputStream());
    //DOS.writeUTF(result.get(0));
    //sock.close();
    public void setMessage(String user_input){

        message = user_input;
    }
    public void setIp(String sever_ip){
        IP_add = sever_ip;
    }
}
