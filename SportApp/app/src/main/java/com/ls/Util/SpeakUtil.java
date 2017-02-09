package com.ls.Util;

import android.app.Service;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by qwe on 2016/5/5.
 */
public class SpeakUtil implements TextToSpeech.OnInitListener {

    private static final String TAG = "SpeakUtil";

    private static SpeakUtil speakUtil = null;

    private TextToSpeech mtts;
    private Service service;

    private SpeakUtil(){}

    public static SpeakUtil getInstance(){
        if(speakUtil == null)
            speakUtil = new SpeakUtil();

        return speakUtil;
    }

    public void initTTS(){
        mtts = new TextToSpeech(service,this);
    }

    public void setService(Service service){
        this.service = service;
    }

    public void shutdownTTS(){
        mtts.shutdown();
    }

    @Override
    public void onInit(int status){
        if(status == TextToSpeech.SUCCESS){
            int result = mtts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
                Log.i(TAG, "TextToSpeech Initialized.");
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    public void say(String text){
        mtts.speak(text,TextToSpeech.QUEUE_ADD,null);
    }
}
