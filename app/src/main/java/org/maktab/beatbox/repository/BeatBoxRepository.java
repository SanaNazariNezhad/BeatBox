package org.maktab.beatbox.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
    private List<Sound> mSounds;
    private int mIndex;
    private Boolean mFlagPlay;
    private Uri mUri;

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
//        loadSounds();
        getSongFromExternal();
        mIndex = -1;
        mFlagPlay = false;
    }

    /*public Sound songFromFile(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        return new Sound(
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        );
    }*/

    public void getSongFromExternal() {
        ContentResolver cr = mContext.getContentResolver();
        AssetManager assetManager = mContext.getAssets();
        Sound sound = new Sound();
        mSounds = new ArrayList<>();

        mUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        /*String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
*/
        Cursor cursor = cr.query(mUri, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            int musicTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int musicArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int musicAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            while (cursor.moveToNext()) {
                String currentTitle = cursor.getString(musicTitle);
                String currentArtist = cursor.getString(musicArtist);
                String currentAlbum = cursor.getString(musicAlbum);
                sound.setTitle(currentTitle);
                sound.setArtist(currentArtist);
                sound.setAlbum(currentAlbum);
//                sound = new Sound(currentTitle,currentArtist,currentAlbum);
                try {
                    mSounds.add(sound);
                    loadInMediaPlayer(assetManager, sound);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
//                loadSounds();
            }
            ;
            cursor.close();
        }
    }

    public static Sound getSongForCursor(Cursor cursor) {
        Sound sound = new Sound();
        if ((cursor != null) && (cursor.moveToFirst())) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            String album = cursor.getString(3);
            int duration = cursor.getInt(4);
            int trackNumber = cursor.getInt(5);
            long artistId = cursor.getInt(6);
            long albumId = cursor.getLong(7);

            sound = new Sound(id, albumId, artistId, title, artist, album, duration, trackNumber);
        }

        if (cursor != null)
            cursor.close();
        return sound;
    }

    //it runs on constructor at the start of repository
    public void loadSounds() {
        AssetManager assetManager = mContext.getAssets();
        try {
            String[] fileNames = assetManager.list(ASSET_FOLDER);
            String[] fileNamesImage = assetManager.list(ASSET_FOLDER_IMAGE);
            for (int i = 0; i < fileNames.length; i++) {
                String assetPath = ASSET_FOLDER + File.separator + fileNames[i];
                String assetPathImage = ASSET_FOLDER_IMAGE + File.separator + fileNamesImage[i];
                Sound sound = new Sound(assetPath);
//                Sound mSound = getSongFromPath(assetPath,mContext);
//                String title = mSound.getTitle();
//                String artist = mSound.getArtist();
//                String album = mSound.getAlbum();
                sound.setImageAssetPath(assetPathImage);
                loadInMediaPlayer(assetManager, sound);
//                mSounds.add(sound);
            }

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
                if (sound.getTitle().equalsIgnoreCase(name)) {
                    loadInMediaPlayer(assetManager, sound);
                    play(sound);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void loadInMediaPlayer(AssetManager assetManager, Sound sound) throws IOException {
//        AssetFileDescriptor afd = assetManager.openFd(sound.getAssetPath());
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        String path = getPathFromURI(mContext,mUri);
        Log.d("Main","Path :" + path);
        File file = new File(path);
        Log.d("Main" , "Music exists : " + file.exists() + ", can read : " + file.canRead());
//        mMediaPlayer.setDataSource(path);
//        mMediaPlayer.setDataSource(mContext,mUri);
//        mMediaPlayer.prepare();
        mMediaPlayer = MediaPlayer.create(mContext,Uri.parse(path));
//        InputStream stream = assetManager.open(sound.getImageAssetPath());
        // load image as Drawable
//        Drawable drawable = Drawable.createFromStream(stream, null);
//        sound.setDrawable(drawable);
//        stream.close();
        /*AssetFileDescriptor afdImage = assetManager.openFd(sound.getImageAssetPath());
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(afdImage.getFileDescriptor(),afdImage.getStartOffset(),afdImage.getLength());
//        metadataRetriever.setDataSource(sound.getImageAssetPath());
        byte [] data = metadataRetriever.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        sound.setBitmap(bitmap);*/
    }

    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    //it runs on demand when user want to hear the sound
    public void play(Sound sound) {
        /*if (mIndex == -1) {
            for (int i = 0; i < mSounds.size(); i++) {
                if (mSounds.get(i).equals(sound)) {
                    mIndex = i;
                    break;
                }
            }
        } else
            mIndex += 1;*/
        if (sound == null)
            return;
        /*mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                *//*if (mIndex != mSounds.size() - 1) {
                    int number = mIndex + 1;
                }*//*
                play(mSounds.get(1));
            }
        });*/
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
