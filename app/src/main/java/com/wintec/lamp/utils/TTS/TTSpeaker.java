package com.wintec.lamp.utils.TTS;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.wintec.lamp.base.Const;

import java.util.Locale;

public class TTSpeaker {
    private static TTSpeaker instance;
    private static TextToSpeech mTextToSpeech;

    public static TTSpeaker getInstance(Context context) {
        try {
            synchronized (TTSpeaker.class) {
                if (instance == null) {
                    instance = new TTSpeaker(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public TTSpeaker(Context context) {
        mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    // 设置朗读语言
                    mTextToSpeech.setLanguage(Locale.CHINA);
//                    if ((supported != TextToSpeech.LANG_AVAILABLE)
//                            && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)){
//
//                    }
                }
            }
        });
    }

    public void speak(String content) {
        try {
            if (content == null || content.equals("")) {
                return;
            }
            content = content.replaceAll(" ", "");
            // todo 是否播报
            if (Const.getSettingValue(Const.KEY_VOICE) == "") {
                mTextToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
//            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
