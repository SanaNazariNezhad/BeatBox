package org.maktab.beatbox.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;

public class Sound {
    private String mName;
    private String mAssetPath;
    private String mImageAssetPath;
    private Drawable mDrawable;

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
}
