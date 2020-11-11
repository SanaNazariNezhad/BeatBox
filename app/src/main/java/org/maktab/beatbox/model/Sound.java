package org.maktab.beatbox.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.Objects;

public class Sound {
    private String mName;
    private String mAssetPath;
    private Bitmap mBitmap;
    private String mTitle;
    private String mArtist;
    private String mAlbum;


    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

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
                Objects.equals(mBitmap, sound.mBitmap) &&
                Objects.equals(mTitle, sound.mTitle) &&
                Objects.equals(mArtist, sound.mArtist) &&
                Objects.equals(mAlbum, sound.mAlbum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mAssetPath, mBitmap, mTitle, mArtist, mAlbum);
    }
}
