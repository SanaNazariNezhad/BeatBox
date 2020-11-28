package org.maktab.beatbox.repository;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.maktab.beatbox.model.Sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BeatBoxRepository {

    public static final String TAG = "BeatBox";
    private static String ASSET_FOLDER = "sample_musics";
    private static BeatBoxRepository sInstance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private List<Sound> mSounds = new ArrayList<>();
    private int mIndex;
    private Boolean mFlagPlay;
    private MutableLiveData<Sound> mLiveDataPlayingSound;
    private MutableLiveData<Boolean> mLiveDataIsPlaying;
    private Sound mPlayingSound;
    private boolean isMusicPlaying;
    private boolean isRepeatOne;
    private boolean isRepeatAll;
    private boolean isRepeat;
    private String[] itemsAll;

    public boolean isShuffle() {
        return isShuffle;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    private boolean isShuffle;

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public boolean isRepeatOne() {
        return isRepeatOne;
    }


    public MutableLiveData<Boolean> getLiveDataIsPlaying() {
        return mLiveDataIsPlaying;
    }

    public boolean isRepeatAll() {
        return isRepeatAll;
    }

    public void setRepeatAll(boolean repeatAll) {
        isRepeatAll = repeatAll;
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public Sound getPlayingSound() {
        return mPlayingSound;
    }

    public MutableLiveData<Sound> getLiveDataPlayingSound() {
        return mLiveDataPlayingSound;
    }

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
        mFlagPlay = false;
        mIndex = 0;
        mLiveDataPlayingSound = new MutableLiveData<>();
        mLiveDataIsPlaying = new MutableLiveData<>();
        isMusicPlaying = false;
        isRepeatOne = false;
        isRepeatAll = false;
        isRepeat = false;
        isShuffle = false;
    }

    public ArrayList<File> readOnlyAudioMusics(File file) {

        ArrayList<File> arrayList = new ArrayList<>();
        File[] allFiles = file.listFiles();

        for (File individualFile : allFiles) {

            if (individualFile.isDirectory() && individualFile.isHidden()){
                arrayList.addAll(readOnlyAudioMusics(individualFile));
            }
            else {
                if (individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac")
                        || individualFile.getName().endsWith(".wav") || individualFile.getName().endsWith(".wma")){
                    arrayList.add(individualFile);
                }
            }

        }
        return arrayList;
    }

    //it runs on constructor at the start of repository
    public void loadSounds() {
//        AssetManager assetManager = mContext.getAssets();
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        Bitmap mBitmap;
        try {
            final ArrayList<File> musics = readOnlyAudioMusics(Environment.getExternalStorageDirectory());
//            String[] fileNames = assetManager.list(ASSET_FOLDER);
            for (int i = 0; i < musics.size(); i++) {
//                String assetPath = ASSET_FOLDER + File.separator + fileNames[i];
                String path = musics.get(i).getAbsolutePath();
//                AssetFileDescriptor afd = mContext.getAssets().openFd(path);
                metaRetriever.setDataSource(path);

                String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                byte[] data = metaRetriever.getEmbeddedPicture();
                if (data != null) {
                    mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                } else {
                    mBitmap = null;
                }

//                afd.close();
                Sound sound = new Sound(path);
                sound.setTitle(title);
                sound.setArtist(artist);
                sound.setAlbum(album);
                sound.setBitmap(mBitmap);
                loadInMediaPlayer(path, sound);
                mSounds.add(sound);
            }

            metaRetriever.release();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void loadMusic(UUID uuid) {
        mFlagPlay = true;
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
//        AssetManager assetManager = mContext.getAssets();
        try {
            for (Sound sound : mSounds) {
                if (sound.getSoundId().equals(uuid)) {
                    loadInMediaPlayer(sound.getAssetPath(), sound);
                    play(sound);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public Sound getSound(UUID uuid) {
        Sound result = null;
        for (Sound sound : mSounds) {
            if (sound.getSoundId().equals(uuid))
                result = sound;
        }
        return result;
    }

    public int getSoundIndex(UUID uuid) {
        int index = -1;
        for (int i = 0; i < mSounds.size(); i++) {
            if (mSounds.get(i).getSoundId().equals(uuid))
                index = i;
        }
        return index;
    }

    public void nextSound(Sound sound) {
        int index = getSoundIndex(sound.getSoundId());
        if (index == (mSounds.size() - 1)) {
            loadMusic(mSounds.get(0).getSoundId());
            mPlayingSound = mSounds.get(0);
        } else {
            loadMusic(mSounds.get((index + 1)).getSoundId());
            mPlayingSound = mSounds.get((index + 1));
        }
        isMusicPlaying = true;
    }

    public void previousSound(Sound sound) {
        int index = getSoundIndex(sound.getSoundId());
        if (index == 0) {
            loadMusic(mSounds.get((mSounds.size() - 1)).getSoundId());
            mPlayingSound = mSounds.get((mSounds.size() - 1));
        } else {
            loadMusic(mSounds.get((index - 1)).getSoundId());
            mPlayingSound = mSounds.get((index - 1));
        }
        isMusicPlaying = true;
    }

    public void repeatOne(Sound sound) {
        if (!isRepeatOne) {
            if (!mMediaPlayer.isPlaying())
                loadMusic(sound.getSoundId());
            mMediaPlayer.setLooping(true);
        } else {
            mMediaPlayer.setLooping(false);
        }
        isRepeatOne = !isRepeatOne;

    }

    public void repeatAll(Sound sound) {
        int index = getSoundIndex(sound.getSoundId());
        if (index == mSounds.size() - 1)
            index = 0;
        while (index < mSounds.size() && isRepeatAll) {
            loadMusic(mSounds.get(index).getSoundId());
            if (index == mSounds.size() - 1)
                index = 0;
            else
                index += 1;
        }


    }

    public List<Integer> shuffle() {
        Random random = new Random();
        List<Integer> soundIndex = new ArrayList<>();
        soundIndex.add(random.nextInt(mSounds.size() - 0) + 0);
// you have also handle min to max index
        while (soundIndex.size() != mSounds.size()) {
            int index = random.nextInt(mSounds.size() - 0) + 0;
            for (int i = 0; i < soundIndex.size(); i++) {
                if (index == soundIndex.get(i))
                    break;
            }
            soundIndex.add(index);
        }
        return soundIndex;
    }

    private void loadInMediaPlayer(String path, Sound sound) throws IOException {
//        AssetFileDescriptor afd = assetManager.openFd(sound.getAssetPath());
        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
    }
    //it runs on demand when user want to hear the sound

    public void play(Sound sound) {

        if (sound == null)
            return;

        mMediaPlayer.start();
        mLiveDataPlayingSound.postValue(sound);
        mLiveDataIsPlaying.postValue(true);
        mPlayingSound = sound;
        isMusicPlaying = true;
    }

    public void release() {
        mMediaPlayer.release();
    }

    public void pause() {
        mMediaPlayer.pause();
        isMusicPlaying = false;
    }

    public void playAgain() {
        if (mFlagPlay)
            mMediaPlayer.start();
        isMusicPlaying = true;
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);

    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
