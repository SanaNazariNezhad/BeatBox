package org.maktab.beatbox.view.activity;

import androidx.fragment.app.Fragment;

import org.maktab.beatbox.view.fragment.BeatBoxFragment;

public class BeatBoxActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return BeatBoxFragment.newInstance();
    }

}