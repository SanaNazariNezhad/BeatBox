package org.maktab.beatbox.controller.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.maktab.beatbox.R;
import org.maktab.beatbox.model.Sound;
import org.maktab.beatbox.repository.BeatBoxRepository;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BeatBoxDetailFragment extends Fragment {

    public static final String ARGS_SOUND = "Args_Sound";
    public static final String ARGS_STATE = "args_State";
    public static final String BUNDLE_STATE = "bundle_state";
    private UUID mSoundId;
    private Sound mSound;
    private BeatBoxRepository mRepository;
    private ImageView mImageView;
    private TextView mTextView;
    private String mState;
    private MutableLiveData<String> mLiveDataTime;
    private SeekBar mSeekBar;
    private TextView mTextViewTime,mTextViewTotalTime;

    public BeatBoxDetailFragment() {
        // Required empty public constructor
    }

    public static BeatBoxDetailFragment newInstance(UUID soundId,String state) {
        BeatBoxDetailFragment fragment = new BeatBoxDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_SOUND,soundId);
        args.putString(ARGS_STATE,state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_STATE,mState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            mState = savedInstanceState.getString(BUNDLE_STATE);
        }
        mSoundId = (UUID) getArguments().getSerializable(ARGS_SOUND);
        mState = getArguments().getString(ARGS_STATE);
        mRepository = BeatBoxRepository.getInstance(getActivity());
        mSound = mRepository.getSound(mSoundId);
        mLiveDataTime = new MutableLiveData<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beat_box_detail, container, false);
        findViews(view);
        initView();
        seekBar();
        setLiveDataObservers();
        return view;
    }

    private void initView() {
        if (mSound.getBitmap()!=null)
            mImageView.setImageBitmap(mSound.getBitmap());
        else
            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
        if (mState.equalsIgnoreCase("Tracks"))
            mTextView.setText(mSound.getTitle());
        else if (mState.equalsIgnoreCase("Artists"))
            mTextView.setText(mSound.getArtist());
        else
            mTextView.setText(mSound.getAlbum());

    }

    private void findViews(View view) {
        mImageView = view.findViewById(R.id.image_beat_box_detail);
        mTextView = view.findViewById(R.id.text_music_detail);
        mSeekBar = view.findViewById(R.id.seekBar_detail);
        mTextViewTime = view.findViewById(R.id.txtView_Time_detail);
        mTextViewTotalTime = view.findViewById(R.id.txtView_Time_detail_Total);
    }

    private void setLiveDataObservers() {
        mLiveDataTime.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String time) {
                mTextViewTime.setText(time);
            }
        });
    }

    private void seekBar() {
        mSeekBar.setMax(mRepository.getMediaPlayer().getDuration());
        mSeekBar.setProgress(mRepository.getMediaPlayer().getCurrentPosition());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mRepository.getMediaPlayer().getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mRepository.getMediaPlayer().getDuration()) - (minutes * 60);

        final String maxTime =minutes + ":" + seconds;
        mTextViewTotalTime.setText(maxTime);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mSeekBar.setProgress(mRepository.getMediaPlayer().getCurrentPosition());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mRepository.getMediaPlayer().getCurrentPosition());
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mRepository.getMediaPlayer().getCurrentPosition()) - (minutes * 60);
                String currentTime;
                if (minutes != 0) {
                    currentTime = minutes + ":" + seconds;
                } else {
                    currentTime = "" + seconds;
                }
                String time = currentTime;
                mLiveDataTime.postValue(time);
            }
        }, 0, 1000);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                    mRepository.getMediaPlayer().seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}