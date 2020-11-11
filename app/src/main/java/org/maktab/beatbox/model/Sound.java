package org.maktab.beatbox.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.Objects;

public class Sound {
    private String mName;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private long mId,mAlbumId,mArtistId;
    private int mDuration,mTrackNumber;
    private String mAssetPath;
    private String mImageAssetPath;
    private Drawable mDrawable;

    public Sound(String title, String artist, String album) {
        mTitle = title;
        mArtist = artist;
        mAlbum = album;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public Sound(long id, long albumId, long artistId, String title, String artist, String album, int duration, int trackNumber) {
        mId = id;
        mAlbumId = albumId;
        mArtistId = artistId;
        mTitle = title;
        mArtist = artist;
        mAlbum = album;
        mDuration = duration;
        mTrackNumber = trackNumber;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public long getId() {
        return mId;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public long getArtistId() {
        return mArtistId;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getTrackNumber() {
        return mTrackNumber;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public String getImageAssetPath() {
        return mImageAssetPath;
    }

    public void setImageAssetPath(String imageAssetPath) {
        mImageAssetPath = imageAssetPath;
    }

    //it is the id of sound loaded in sound pool.
    private Integer mSoundId;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public void setAssetPath(String assetPath) {
        mAssetPath = assetPath;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }


    public Sound(String assetPath) {

        mAssetPath = assetPath;
        String[] sections = assetPath.split(File.separator);
        String fileNameWithExtension = sections[sections.length - 1];
        int lastDotIndex = fileNameWithExtension.lastIndexOf(".");

        mName = fileNameWithExtension.substring(0, lastDotIndex);
    }

    public Sound() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sound sound = (Sound) o;
        return Objects.equals(mName, sound.mName) &&
                Objects.equals(mAssetPath, sound.mAssetPath) &&
                Objects.equals(mImageAssetPath, sound.mImageAssetPath) &&
                Objects.equals(mDrawable, sound.mDrawable) &&
                Objects.equals(mSoundId, sound.mSoundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mAssetPath, mImageAssetPath, mDrawable, mSoundId);
    }
}
