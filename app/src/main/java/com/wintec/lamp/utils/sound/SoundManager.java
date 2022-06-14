package com.wintec.lamp.utils.sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.wintec.lamp.R;

public class SoundManager {
    private static SoundManager mInstance = null;
    private static Context mContext;
    private static SoundPool mSoundPool;
    private static AudioManager mAudioManager;
    private static boolean mIsLoad = false;
    private static float audioMaxVolumn, volumnCurrent, volumnRatio;
    private static int mSoundId;

    public static SoundManager getInstance() {
        if (mInstance == null) {
            synchronized (SoundManager.class) {
                if (mInstance == null) {
                    mInstance = new SoundManager();
                }
            }
        }
        return mInstance;
    }

    public SoundManager() {
    }

    public void init(Context context) {
        mContext = context;
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                SoundPool.Builder builder = new SoundPool.Builder();
                builder.setMaxStreams(1);
                AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                builder.setAudioAttributes(attrBuilder.build());
                mSoundPool = builder.build();
            } else {
                mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            }
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    mIsLoad = true;
                    Log.i("test", "sp load");
                }
            });
            mSoundId = mSoundPool.load(mContext, R.raw.click, 1);
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            audioMaxVolumn = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volumnCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volumnRatio = volumnCurrent / audioMaxVolumn;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void playSound() {
        if (!mIsLoad) {
            return;
        }

        try {
            mSoundPool.play(mSoundId,
                    volumnRatio,
                    volumnRatio,
                    1,
                    0,
                    1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void release() {

    }
}
