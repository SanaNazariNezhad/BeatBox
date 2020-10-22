package org.maktab.beatbox.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import org.maktab.beatbox.R;
import org.maktab.beatbox.adapter.SoundAdapter;
import org.maktab.beatbox.model.Sound;
import org.maktab.beatbox.repository.BeatBoxRepository;
import org.maktab.beatbox.databinding.FragmentBeatBoxBinding;
import org.maktab.beatbox.databinding.ListItemSoundBinding;
import org.maktab.beatbox.viewmodel.BeatBoxViewModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BeatBoxFragment extends Fragment {

    public static final String TAG = "BeatBoxFragment";
    private BeatBoxViewModel mBeatBoxViewModel;
    private FragmentBeatBoxBinding mBinding;

    public BeatBoxFragment() {
        // Required empty public constructor
    }

    public static BeatBoxFragment newInstance() {
        BeatBoxFragment fragment = new BeatBoxFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setRetainInstance(true);
        mBeatBoxViewModel = new BeatBoxViewModel(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mBeatBoxViewModel.releaseSoundPool();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_beat_box,
                container,
                false);

        initViews();
        listeners();
        seekBar();
        setupAdapter();

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView");
    }

    private void initViews() {
        int rowNumber = getResources().getInteger(R.integer.row_number);
        mBinding.recyclerViewBeatBox.setLayoutManager(new GridLayoutManager(getContext(), rowNumber));
    }

    private void listeners() {

        mBinding.imageBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBeatBoxViewModel.playAgain();
            }
        });
        mBinding.imageBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBeatBoxViewModel.pause();
            }
        });
    }

    private void seekBar() {
        mBinding.seekBar.setMax(mBeatBoxViewModel.getMediaPlayer().getDuration());
        mBinding.seekBar.setProgress(mBeatBoxViewModel.getMediaPlayer().getCurrentPosition());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mBeatBoxViewModel.getMediaPlayer().getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mBeatBoxViewModel.getMediaPlayer().getDuration()) - (minutes * 60);

        final String maxTime ="/" + minutes + ":" + seconds;
        mBinding.txtViewTime.setText("0" + maxTime);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                mBinding.seekBar.setProgress(mBeatBoxViewModel.getMediaPlayer().getCurrentPosition());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mBeatBoxViewModel.getMediaPlayer().getCurrentPosition());
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mBeatBoxViewModel.getMediaPlayer().getCurrentPosition()) - (minutes * 60);
                String currentTime;
                if (minutes != 0) {
                    currentTime = minutes + ":" + seconds;
                }
                else {
                    currentTime = "" + seconds;
                }
                mBinding.txtViewTime.setText(currentTime + maxTime);
            }
        },0,1000);
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                if (b)
                    mBeatBoxViewModel.getMediaPlayer().seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupAdapter() {
        List<Sound> sounds = mBeatBoxViewModel.getSounds();
        SoundAdapter adapter = new SoundAdapter(getContext(),sounds);
        mBinding.recyclerViewBeatBox.setAdapter(adapter);
    }
}
