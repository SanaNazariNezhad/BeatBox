package org.maktab.beatbox.controller.model;

import java.io.File;

public class Sound {
    private String mName;
    private String mAssetPath;

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
        /*
            Example:
            assetPath: sample_sounds/65_cjipie.wav
            name = 65_cjipie
        */

        mAssetPath = assetPath;
        String[] sections = assetPath.split(File.separator);
        String fileNameWithExtension = sections[sections.length - 1];
        int lastDotIndex = fileNameWithExtension.lastIndexOf(".");

        mName = fileNameWithExtension.substring(0, lastDotIndex);
    }

    public Sound() {
    }
}
