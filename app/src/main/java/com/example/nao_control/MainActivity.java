package com.example.nao_control;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;

import android.net.Uri;
import android.os.Handler;
import android.speech.RecognizerIntent;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private TextView txvResult;
    private String sever_ip = "";
    receive_socket receive = new receive_socket();
    public static final String EXTRA_MESSAGE = "com.example.Nao_control.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txvResult = (TextView) findViewById(R.id.txvResult);

        receive.execute();
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        }
        else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ArrayList<String> result;

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                    user_Sorket socket= new user_Sorket();
                    socket.setMessage(result.get(0));
                    socket.setIp(this.sever_ip);
                    socket.execute();
                }
                break;
        }
    }

    public void Button_click(View v){
        EditText editIp = (EditText)findViewById(R.id.editText);
        this.sever_ip = editIp.getText().toString();
        txvResult.setText(this.sever_ip);

        txvResult.setText(receive.get_json());

    }

    public void  Button2_click(View v){
        JSONObject jsonObject;
        Intent intent = null;
        String Thekey = "";
        String Thevalue = "";

        Intent intent_text = new Intent(this, text_present.class);
        Intent intent_speech = new Intent(this,testToSpeech.class);
        //Intent intent_voice = new Intent(this, text_to_voice.class);
        //Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();

        try {

            String json = receive.get_json();
            jsonObject = new JSONObject(json);
            txvResult.setText(json);

            Toast.makeText(this, jsonObject.getString(jsonObject.names().get(0).toString()), Toast.LENGTH_SHORT).show();

            if (jsonObject.names().get(2).toString().equals("url") & jsonObject.getString(jsonObject.names().get(2).toString()).substring(0,4).equals("http")){
                Toast.makeText(this, "do not suuport", Toast.LENGTH_SHORT).show();

                Thevalue = jsonObject.getString(jsonObject.names().get(2).toString());
                Uri uri = Uri.parse(Thevalue);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));

            }
            else if(jsonObject.names().get(1).toString().equals("speech") & !jsonObject.getString(jsonObject.names().get(1).toString()).equals("")){
                Toast.makeText(this, jsonObject.getString(jsonObject.names().get(1).toString()), Toast.LENGTH_SHORT).show();

                Thevalue = jsonObject.getString(jsonObject.names().get(1).toString());
                intent_speech.putExtra(EXTRA_MESSAGE, Thevalue);
                startActivity(intent_speech);
            }
            else if(jsonObject.names().get(0).toString().equals("text") & !jsonObject.getString(jsonObject.names().get(0).toString()).equals("")){

                Thevalue = jsonObject.getString(jsonObject.names().get(0).toString());
                intent_text.putExtra(EXTRA_MESSAGE, Thevalue);
                startActivity(intent_text);
            }




        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
