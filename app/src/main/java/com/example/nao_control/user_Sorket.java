package com.example.nao_control;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.lang.Void;

import org.json.JSONArray;
import org.json.JSONObject;

public class user_Sorket extends AsyncTask<String, Void, String> {
    private String IP_add = "192.168.0.104";
    int port_num = 9559;
    private Socket client;
    PrintWriter printWriter;
    private String message;
    private JSONArray J_array = new JSONArray();
    private JSONObject Json = new JSONObject();
    String send_j = null;
    String send_j_a = null;
    @Override
    protected String doInBackground(String... params){
        try{


            if (send_j_a.equals("send_j_a")){
                send_j_a();
                send_j_a = null;

            }else if(send_j.equals("send_j")){
                send_j();

                send_j = null;
            }
            else{
                client = new Socket(IP_add, port_num);


                OutputStream out = client.getOutputStream();
                PrintWriter output = new PrintWriter(out);
                output.println(message);

                output.close();
                out.close();
                client.close();
            }


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
    public void setJsonArray(JSONArray j_array){
        J_array = j_array;
        send_j_a = "send_j_a";
    }
    public void setJson(JSONObject json){
        Json = json;
        send_j = "send_j";
    }
    private void send_j(){
        try {
            client = new Socket(IP_add, port_num);


            OutputStream out = client.getOutputStream();
            PrintWriter output = new PrintWriter(out);

            output.println(Json.toString());

            output.close();
            out.close();
            client.close();
            /*
            // 服务端创建socket，监听8888号端口
            ServerSocket serverSocket = new ServerSocket(port_num);
            System.out.println("SERVER -- Server start, waiting for connecting ...");
            // 等待Client与Server建立连接
            Socket socket = serverSocket.accept();
            System.out.println( "SERVER -- client " + socket.getInetAddress().getHostName() + " has connected...");

            // 向Client发送json消息，可以通过构建一个JSONObject对象

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "tom");
            jsonObject.put("age", 20);
            jsonObject.put("msg", "Helloworld");

            OutputStream outputStream = socket.getOutputStream();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(Json.toString() + "\n");

            // 发送
            bufferedWriter.flush();
            socket.shutdownOutput();
            System.out.println("SERVER -- jsonData has send, close socket...");
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void send_j_a(){
        try {
            client = new Socket(IP_add, port_num);


            OutputStream out = client.getOutputStream();
            PrintWriter output = new PrintWriter(out);
            for (int i =0;i<J_array.length();i++){

                message = message + (J_array.getJSONObject(i).toString() + "\n");
            }
            output.println(message);

            output.close();
            out.close();
            client.close();
            /*
            // 服务端创建socket，监听8888号端口
            ServerSocket serverSocket = new ServerSocket(port_num);
            System.out.println("SERVER -- Server start, waiting for connecting ...");
            // 等待Client与Server建立连接
            Socket socket = serverSocket.accept();
            System.out.println( "SERVER -- client " + socket.getInetAddress().getHostName() + " has connected...");

            // 向Client发送json消息，可以通过构建一个JSONObject对象
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "tom");
            jsonObject.put("age", 20);
            jsonObject.put("msg", "Helloworld");


            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            for (int i =0;i<J_array.length();i++){
                bufferedWriter.write(J_array.getJSONObject(i).toString() + "\n");
            }

            // 发送
            bufferedWriter.flush();
            socket.shutdownOutput();
            System.out.println("SERVER -- jsonData has send, close socket...");
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
