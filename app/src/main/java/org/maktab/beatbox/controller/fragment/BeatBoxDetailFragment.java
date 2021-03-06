package org.maktab.beatbox.controller.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.maktab.beatbox.R;
import org.maktab.beatbox.model.Sound;
import org.maktab.beatbox.repository.BeatBoxRepository;

import java.util.ArrayList;
import java.util.List;
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
    private List<Sound> mSounds;
    private BeatBoxRepository mRepository;
    private ImageView mImageView;
    private TextView mTextView;
    private String mState;
    private String mTotalTime;
    private MutableLiveData<String> mLiveDataTime;
    private MutableLiveData<Boolean> mLiveDataRepeatAll;
    private SeekBar mSeekBar;
    private TextView mTextViewTime, mTextViewTotalTime;
    private ImageButton mImageButton_next, mImageButton_prev, mImageButton_playing, mImageButtonShuffle,
            mImageButtonRepeat;
    private boolean mIsMusicPlaying;
    private boolean mIsRepeatAll;
    private List<Integer> mIndexList;
    private static boolean mWhichRepeatButton;
    private static boolean mWhichShuffleButton;

    public BeatBoxDetailFragment() {
        // Required empty public constructor
    }

    public static BeatBoxDetailFragment newInstance(UUID soundId, String state) {
        BeatBoxDetailFragment fragment = new BeatBoxDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_SOUND, soundId);
        args.putString(ARGS_STATE, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_STATE, mState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mState = savedInstanceState.getString(BUNDLE_STATE);
        }
        mSoundId = (UUID) getArguments().getSerializable(ARGS_SOUND);
        mState = getArguments().getString(ARGS_STATE);
        mRepository = BeatBoxRepository.getInstance(getActivity());
        mSound = mRepository.getSound(mSoundId);
        mIsMusicPlaying = mRepository.isMusicPlaying();
        mWhichRepeatButton = mRepository.isRepeat();
        mWhichShuffleButton = mRepository.isShuffle();
        mLiveDataTime = new MutableLiveData<>();
        mLiveDataRepeatAll = mRepository.getLiveDataIsPlaying();
        mIndexList = new ArrayList<>();
        mSounds = mRepository.getSounds();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beat_box_detail, container, false);
        findViews(view);
        seekBar();
        initView();
        setLiveDataObservers();
        listeners();
        return view;
    }

    private void listeners() {

        mImageButton_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.nextSound(mSound);
                mSound = mRepository.getPlayingSound();
                mSoundId = mSound.getSoundId();
                initView();
            }
        });
        mImageButton_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRepository.getMediaPlayer().isPlaying()) {
                    mImageButton_playing.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline));
                    mRepository.pause();
                } else {
                    mImageButton_playing.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline));
                    mRepository.playAgain();
                }
            }
        });
        mImageButton_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.previousSound(mRepository.getSound(mSoundId));
                mSound = mRepository.getPlayingSound();
                mSoundId = mSound.getSoundId();
                initView();
            }
        });
        mImageButtonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.setRepeat(!mWhichRepeatButton);
                mWhichRepeatButton = mRepository.isRepeat();
                if (mWhichRepeatButton) {
                    mIsRepeatAll = mRepository.isRepeatAll();
                    mRepository.setRepeatAll(!mIsRepeatAll);
                } else if (!mWhichRepeatButton) {
                    mRepository.repeatOne(mRepository.getSound(mSoundId));
                    mIsRepeatAll = mRepository.isRepeatAll();
                    mRepository.setRepeatAll(!mIsRepeatAll);
                    initView();
                }
            }
        });
        mImageButtonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.setShuffle(!mWhichShuffleButton);
                mWhichShuffleButton = mRepository.isShuffle();
                if (mWhichShuffleButton) {
                    mIndexList = mRepository.shuffle();
                } else if (!mWhichShuffleButton) {
                    mIndexList.clear();
                    initView();
                }
            }
        });
    }

    private void initView() {
        mIsMusicPlaying = mRepository.isMusicPlaying();
        mTextViewTotalTime.setText(mTotalTime);
        if (mSound.getBitmap() != null)
            mImageView.setImageBitmap(mSound.getBitmap());
        else
            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
        if (mState.equalsIgnoreCase("Tracks"))
            mTextView.setText(mSound.getTitle());
        else if (mState.equalsIgnoreCase("Artists"))
            mTextView.setText(mSound.getArtist());
        else
            mTextView.setText(mSound.getAlbum());

        setButtonIcon(mIsMusicPlaying, mImageButton_playing, R.drawable.ic_pause_circle_outline, R.drawable.ic_play_circle_outline);

        setButtonIcon(mWhichRepeatButton, mImageButtonRepeat, R.drawable.ic_repeat, R.drawable.ic_repeat_one);

        setButtonIcon(mWhichShuffleButton, mImageButtonShuffle, R.drawable.ic_shuffle, R.drawable.ic_arrow);

    }

    private void setButtonIcon(boolean isMusicPlaying, ImageButton imageButton_playing, int p, int p2) {
        if (isMusicPlaying)
            imageButton_playing.setImageDrawable(getResources().getDrawable(p));
        else
            imageButton_playing.setImageDrawable(getResources().getDrawable(p2));
    }

    private void findViews(View view) {
        mImageView = view.findViewById(R.id.image_beat_box_detail);
        mTextView = view.findViewById(R.id.text_music_detail);
        mSeekBar = view.findViewById(R.id.seekBar_detail);
        mTextViewTime = view.findViewById(R.id.txtView_Time_detail);
        mTextViewTotalTime = view.findViewById(R.id.txtView_Time_detail_Total);
        mImageButton_next = view.findViewById(R.id.imageBtn_next);
        mImageButton_prev = view.findViewById(R.id.imageBtn_previous);
        mImageButton_playing = view.findViewById(R.id.imageBtn_play);
        mImageButtonRepeat = view.findViewById(R.id.imageBtn_repeat);
        mImageButtonShuffle = view.findViewById(R.id.imageBtn_shuffle);
    }

    private void setLiveDataObservers() {
        mLiveDataTime.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String time) {
                initView();
                setTotalTime();
                mTextViewTime.setText(time);
                if (time.equals(mTextViewTotalTime.getText().toString()))
                    mLiveDataRepeatAll.postValue(false);

                setButtonIcon(mWhichRepeatButton, mImageButtonRepeat, R.drawable.ic_repeat, R.drawable.ic_repeat_one);
                if (mIndexList.size() != 0) {
                    if (time.equals(mTextViewTotalTime.getText().toString()))
                        mLiveDataRepeatAll.postValue(false);
                }
            }

        });
        mLiveDataRepeatAll.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPlaying) {

                if (!isPlaying && mRepository.isRepeatAll()) {
                    mRepository.nextSound(mSound);
                    mSound = mRepository.getPlayingSound();
                    mSoundId = mSound.getSoundId();
                    initView();
                }
                if (!isPlaying && mIndexList.size() != 0 && mWhichShuffleButton) {
                    int i = mRepository.getIndex();
                    mRepository.loadMusic(mSounds.get(mIndexList.get(i)).getSoundId());
                    mSound = mRepository.getPlayingSound();
                    mSoundId = mSound.getSoundId();
                    initView();
                    mRepository.setIndex(++i);
                }
            }
        });
    }

    private void seekBar() {
        mSeekBar.setMax(mRepository.getMediaPlayer().getDuration());
        mSeekBar.setProgress(mRepository.getMediaPlayer().getCurrentPosition());
        setTotalTime();

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

    private void setTotalTime() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mRepository.getMediaPlayer().getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mRepository.getMediaPlayer().getDuration()) - (minutes * 60);

        final String maxTime = minutes + ":" + seconds;
        mTotalTime = maxTime;
        mTextViewTotalTime.setText(mTotalTime);
    }
}