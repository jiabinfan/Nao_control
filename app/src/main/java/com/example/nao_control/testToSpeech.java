package com.example.nao_control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class testToSpeech extends AppCompatActivity {
    private TextToSpeech mTTS;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private Button mButtonSpeak;
    private TextView mTextView;
    private String Mmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testtospeech);

        Intent intent = getIntent();
        TextView textView = findViewById(R.id.textView);
        final String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        textView.setText(message);
        Mmessage = message;
        mButtonSpeak = findViewById(R.id.button_speak);



        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i != TextToSpeech.ERROR) {
                            mTTS.setLanguage(Locale.US);
                            if (i == TextToSpeech.LANG_MISSING_DATA || i == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.d("TTS", "This Language is not supported");
                            }
                        } else {
                            Log.d("TTS", "Initilization Failed!");
                        }

                        mTTS.speak(Mmessage, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });

        mButtonSpeak.setEnabled(true);
        mTTS.setLanguage(Locale.US);
        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);
        mTextView = findViewById(R.id.textView);

        String text = mTextView.getText().toString();
        float pitch = (float) mSeekBarPitch.getProgress()/50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress()/50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        //mTTS.speak(message,TextToSpeech.QUEUE_FLUSH, null);

        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        //mTTS.speak(Mmessage,TextToSpeech.QUEUE_FLUSH, null);

    }
    private void speak() {
        //String text = mEditText.getText().toString();
        String text = mTextView.getText().toString();
        float pitch = (float) mSeekBarPitch.getProgress()/50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress()/50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);


        mTTS.speak(Mmessage,TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}
