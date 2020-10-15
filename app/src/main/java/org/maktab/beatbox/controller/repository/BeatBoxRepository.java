package org.maktab.beatbox.controller.repository;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import org.maktab.beatbox.controller.model.Sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBoxRepository {

    public static final String TAG = "BeatBox";
    public static final int MAX_STREAMS = 5;
    private static String ASSET_FOLDER = "sample_musics";
    private static BeatBoxRepository sInstance;

    private Context mContext;
    private SoundPool mSoundPool;
    private MediaPlayer mMediaPlayer;
    private List<Sound> mSounds = new ArrayList<>();

    public static BeatBoxRepository getInstance(Context context) {
        if (sInstance == null)
            sInstance = new BeatBoxRepository(context);

        return sInstance;
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    private BeatBoxRepository(Context context) {
        mContext = context.getApplicationContext();
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .build();
        loadSounds();
    }

    //it runs on constructor at the start of repository
    public void loadSounds() {
        AssetManager assetManager = mContext.getAssets();
        try {
            String[] fileNames = assetManager.list(ASSET_FOLDER);
            for (String fileName : fileNames) {
                String assetPath = ASSET_FOLDER + File.separator + fileName;
                Sound sound = new Sound(assetPath);

                loadInSoundPool(assetManager, sound);

                mSounds.add(sound);
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void loadInSoundPool(AssetManager assetManager, Sound sound) throws IOException {
        AssetFileDescriptor afd = assetManager.openFd(sound.getAssetPath());
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mMediaPlayer.prepare();
       /* int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);*/
    }

    //it runs on demand when user want to hear the sound
    public void play(Sound sound) {
        if (sound == null)
            return;
/*
        int playState = mSoundPool.play(
                sound.getSoundId(),
                1.0f,
                1.0f,
                1,
                0,
                1.0f);

        if (playState == 0)
            Log.e(TAG, "this sound has not been played: " + sound.getName());*/

        mMediaPlayer.start();
    }

    public void releaseSoundPool() {
//        mSoundPool.release();
        mMediaPlayer.release();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void playAgain() {
        mMediaPlayer.start();
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);

    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
