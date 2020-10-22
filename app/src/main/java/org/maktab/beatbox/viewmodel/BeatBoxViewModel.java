package org.maktab.beatbox.viewmodel;

import android.content.Context;
import android.media.MediaPlayer;

import org.maktab.beatbox.model.Sound;
import org.maktab.beatbox.repository.BeatBoxRepository;

import java.util.List;

public class BeatBoxViewModel {

    private BeatBoxRepository mRepository;

    public BeatBoxViewModel(Context context) {
        mRepository = BeatBoxRepository.getInstance(context);
    }

    public List<Sound> getSounds() {
        return mRepository.getSounds();
    }

    public void releaseSoundPool() {
        mRepository.releaseSoundPool();
    }

    public void pause() {
        mRepository.pause();
    }

    public void playAgain() {
        mRepository.playAgain();
    }

    public void seekTo(int position) {
        mRepository.seekTo(position);

    }

    public MediaPlayer getMediaPlayer() {
        return mRepository.getMediaPlayer();
    }
}
