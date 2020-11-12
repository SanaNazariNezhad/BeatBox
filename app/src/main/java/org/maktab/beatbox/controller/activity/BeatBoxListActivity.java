package org.maktab.beatbox.controller.activity;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;

import org.maktab.beatbox.controller.fragment.BeatBoxFragment;
import org.maktab.beatbox.controller.fragment.BeatBoxListFragment;

public class BeatBoxListActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, BeatBoxListActivity.class);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return BeatBoxListFragment.newInstance();
    }
}