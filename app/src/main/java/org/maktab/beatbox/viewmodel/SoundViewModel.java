package org.maktab.beatbox.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import org.maktab.beatbox.model.Sound;
import org.maktab.beatbox.repository.BeatBoxRepository;

public class SoundViewModel extends BaseObservable {
    private Sound mSound;
    private BeatBoxRepository mRepository;

    public Sound getSound() {
        return mSound;
    }

    public void setSound(Sound sound) {
        mSound = sound;
//        notifyChange();
        notifyPropertyChanged(BR.title);
    }

    public SoundViewModel(Context context) {
        mRepository = BeatBoxRepository.getInstance(context);
    }

    @Bindable
    public String getTitle() {
        return mSound.getName();
    }

    public void playSound() {
        mRepository.play(mSound);
    }
}

