package org.maktab.beatbox.repository;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;

import org.maktab.beatbox.model.Sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BeatBoxRepository {

    public static final String TAG = "BeatBox";
    private static String ASSET_FOLDER = "sample_musics";
    private static String ASSET_FOLDER_IMAGE = "sample_musics_image";
    private static BeatBoxRepository sInstance;

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private List<Sound> mSounds = new ArrayList<>();
    private int mIndex;
    private Boolean mFlagPlay;

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
        loadSounds();
        mIndex = -1;
        mFlagPlay = false;
    }

    //it runs on constructor at the start of repository
    public void loadSounds() {
        AssetManager assetManager = mContext.getAssets();
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        try {
            String[] fileNames = assetManager.list(ASSET_FOLDER);
            String[] fileNamesImage = assetManager.list(ASSET_FOLDER_IMAGE);
            for (int i = 0; i < fileNames.length; i++) {
                String assetPath = ASSET_FOLDER + File.separator + fileNames[i];
                String assetPathImage = ASSET_FOLDER_IMAGE + File.separator + fileNamesImage[i];
                AssetFileDescriptor afd = mContext.getAssets().openFd(assetPath);
                metaRetriver.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

                String title = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                afd.close();
                Sound sound = new Sound(assetPath);
                sound.setTitle(title);
                sound.setArtist(artist);
                sound.setAlbum(album);
                sound.setImageAssetPath(assetPathImage);
                loadInMediaPlayer(assetManager, sound);
                mSounds.add(sound);
            }

            metaRetriver.release();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void loadMusic(String name) {
        mFlagPlay = true;
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        AssetManager assetManager = mContext.getAssets();
        try {
            for (Sound sound : mSounds) {
                if (sound.getName().equalsIgnoreCase(name)) {
                    loadInMediaPlayer(assetManager, sound);
                    play(sound);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void loadInMediaPlayer(AssetManager assetManager, Sound sound) throws IOException {
        AssetFileDescriptor afd = assetManager.openFd(sound.getAssetPath());
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mMediaPlayer.prepare();
        InputStream stream = assetManager.open(sound.getImageAssetPath());
        // load image as Drawable
        Drawable drawable = Drawable.createFromStream(stream, null);
        sound.setDrawable(drawable);
    }

    //it runs on demand when user want to hear the sound
    public void play(Sound sound) {

        if (sound == null)
            return;

        mMediaPlayer.start();


    }

    public void releaseSoundPool() {
        mMediaPlayer.release();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void playAgain() {
        if (mFlagPlay)
            mMediaPlayer.start();
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);

    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
