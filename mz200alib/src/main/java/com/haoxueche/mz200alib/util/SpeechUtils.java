package com.haoxueche.mz200alib.util;

import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by LiYuchen on 2016/12/15.
 * 987424501@qq.com
 * 文字转语音工具类
 */
public class SpeechUtils {

    private TextToSpeech tts;

    public static final String SPLIT_FLAG = "【停顿】";

    private static OnInitListener onInitListener;
    private boolean initSucc = false;
    private SpeechCallback callback;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnInitListener {
        void onInitComplete(boolean initResult);
    }

    private SpeechUtils() {
        tts = new TextToSpeech(ContextHolder.getInstance(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // 如果装载TTS引擎成功
                if (status == TextToSpeech.SUCCESS) {
                    // 设置使用中文朗读
                    int result = tts.setLanguage(Locale.CHINA);
                    // 如果不支持所设置的语言
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                        if (onInitListener != null) {
                            onInitListener.onInitComplete(false);
                        }
                    } else {
                        initSucc = true;
                        if (onInitListener != null) {
                            onInitListener.onInitComplete(true);
                        }
                    }
                    onInitListener = null;
                }
            }

        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                if (callback != null && callback.getId().equals(utteranceId)) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.callback();
                            callback = null;
                        }
                    });

                }
            }

            @Override
            public void onError(String utteranceId) {
                if (callback != null && callback.getId().equals(utteranceId)) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.callback();
                            callback = null;
                        }
                    });
                }
            }
        });
    }


    public void speak(String content) {
        this.speakWithPause(content, null);
    }

    public void speakImmediately(String content) {
        this.tts.speak(content,  TextToSpeech.QUEUE_FLUSH, null);
    }

    public void speak(String content, SpeechCallback callback) {
        this.callback = callback;
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callback.getId());
        this.speakWithPause(content, hashMap);
    }

    private void speakWithPause(String content, HashMap<String, String> hashMap) {
        String[] contents = content.split(SPLIT_FLAG);

        for(int i = 0; i < contents.length; ++i) {
            String item = contents[i];
            HashMap<String, String> map = null;
            if(i == contents.length - 1) {
                map = hashMap;
            }

            if(!TextUtils.isEmpty(item)) {
                this.tts.speak(item, TextToSpeech.QUEUE_ADD, map);
            }

            if(i != contents.length - 1) {
                this.tts.playSilence(850L, 1, map);
            }
        }

    }

    public void speakNotConnectedToServer() {
//        tts.speak(TerminalApp.getInstance().getString(R.string.not_connected_to_server), TextToSpeech.QUEUE_ADD, null);
    }

    public void speakNetWorkError() {
//        tts.speak(TerminalApp.getInstance().getString(R.string.network_error), TextToSpeech.QUEUE_ADD, null);
    }

    public void stop() {
        tts.stop();
    }

    public static SpeechUtils getInstance() {
        return SpeechUtilsHolder.instance;
    }

    private static class SpeechUtilsHolder {
        public static final SpeechUtils instance = new SpeechUtils();
    }

    public boolean isInitSucc() {
        return initSucc;
    }

    public void setInitSucc(boolean initSucc) {
        this.initSucc = initSucc;
    }

    public static abstract class SpeechCallback {
        private String id = System.currentTimeMillis() + "";

        public SpeechCallback() {
        }

        public abstract void callback();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public TextToSpeech getTts() {
        return tts;
    }

    public void setTts(TextToSpeech tts) {
        this.tts = tts;
    }

    public void setCallback(SpeechCallback callback) {
        this.callback = callback;
    }
}
