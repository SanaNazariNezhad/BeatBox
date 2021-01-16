package org.maktab.beatbox.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class Sound{
    private String mName;
    private Uri mSoundUri;
    private Bitmap mBitmap;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private UUID mSoundId;

    public UUID getSoundId() {
        return mSoundId;
    }

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

    public Uri getSoundUri() {
        return mSoundUri;
    }

    public void setSoundUri(Uri soundUri) {
        mSoundUri = soundUri;
    }

    /*public Sound(String soundUri) {

        mSoundUri = soundUri;
        String[] sections = soundUri.split(File.separator);
        String fileNameWithExtension = sections[sections.length - 1];
        int lastDotIndex = fileNameWithExtension.lastIndexOf(".");

        mName = fileNameWithExtension.substring(0, lastDotIndex);
        mSoundId = UUID.randomUUID();
    }*/

    public Sound() {
        mSoundId = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sound sound = (Sound) o;
        return Objects.equals(mName, sound.mName) &&
                Objects.equals(mSoundUri, sound.mSoundUri) &&
                Objects.equals(mBitmap, sound.mBitmap) &&
                Objects.equals(mTitle, sound.mTitle) &&
                Objects.equals(mArtist, sound.mArtist) &&
                Objects.equals(mAlbum, sound.mAlbum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mSoundUri, mBitmap, mTitle, mArtist, mAlbum);
    }
}
