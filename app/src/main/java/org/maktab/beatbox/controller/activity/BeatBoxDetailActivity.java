package org.maktab.beatbox.controller.activity;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;

import org.maktab.beatbox.controller.fragment.BeatBoxDetailFragment;

import java.util.UUID;

public class BeatBoxDetailActivity extends SingleFragmentActivity {
    private static UUID mSoundId;
    private static String mState;

    public static Intent newIntent(Context context,UUID uuid,String state) {
        mSoundId = uuid;
        mState = state;
        Intent intent = new Intent(context, BeatBoxDetailActivity.class);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return BeatBoxDetailFragment.newInstance(mSoundId,mState);
    }
}