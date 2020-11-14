package org.maktab.beatbox.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.maktab.beatbox.repository.BeatBoxRepository;

import java.util.UUID;

public class onClearFromRecentService extends IntentService {

    public static final String BBIS = "BBIS";
    private BeatBoxRepository mRepository;
    private UUID mPlayingSoundId;

    public onClearFromRecentService() {
        super(BBIS);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mRepository = BeatBoxRepository.getInstance(getApplicationContext());
        String action = intent.getAction();
        mPlayingSoundId = mRepository.getPlayingSound().getSoundId();

        if ("action_previous".equalsIgnoreCase(action)) {
            previousMethod();
        } else if ("action_play".equalsIgnoreCase(action)) {
            playMethod();
        } else if ("action_next".equalsIgnoreCase(action)) {
            nextMethod();
        }
    }

    private void nextMethod() {
        mRepository.nextSound(mRepository.getSound(mPlayingSoundId));
    }

    private void previousMethod() {
        mRepository.previousSound(mRepository.getSound(mPlayingSoundId));
    }

    private void playMethod() {
        if (mRepository.getMediaPlayer().isPlaying()) {
            mRepository.pause();
        } else {
            mRepository.playAgain();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }
}
