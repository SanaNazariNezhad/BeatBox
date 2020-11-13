package org.maktab.beatbox.controller.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.maktab.beatbox.controller.activity.BeatBoxDetailActivity;
import org.maktab.beatbox.R;
import org.maktab.beatbox.model.Sound;
import org.maktab.beatbox.repository.BeatBoxRepository;

import java.util.List;
import java.util.UUID;

public class BeatBoxFragment extends Fragment {

    public static final String TAG = "BeatBoxFragment";
    public static final String BUNDLE_KEY_STATE = "Bundle_Key_State";
    public static final String BUNDLE_KEY_SEEK_BAR = "Bundle_Key_SeekBar";
    public static final String BUNDLE_KEY_PLAYING_SOUND = "Bundle_key_Playing_Sound";
    public static final int REQUEST_CODE_BEAT_BOX_DETAIL = 0;
    private RecyclerView mRecyclerView;
    private BeatBoxRepository mRepository;
    private List<Sound> mSounds;
    private UUID mPlayingSoundId;
    private ImageButton mImageButton_Playing, mImageButton_next,mImageButton_prev;
    private ImageView mImageViewSeekBar;
    private TextView mSeekBarSoundName;
    private MutableLiveData<Boolean> mLiveDataSeekBar;
    private MutableLiveData<Sound> mLiveDataPlayingSound;
    private String mState;
    private LinearLayout mLinearLayoutSeekBar;
    private static Boolean mFlagSeekBar;
    private boolean mIsMusicPlaying;

    public BeatBoxFragment() {
        // Required empty public constructor
    }

    public static BeatBoxFragment newInstance(String state) {
        BeatBoxFragment fragment = new BeatBoxFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_STATE, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if (savedInstanceState != null) {
            mFlagSeekBar = savedInstanceState.getBoolean(BUNDLE_KEY_STATE);
            mPlayingSoundId = (UUID) savedInstanceState.getSerializable(BUNDLE_KEY_PLAYING_SOUND);
        }
        setRetainInstance(true);
        mState = getArguments().getString(BUNDLE_KEY_STATE);
        mRepository = BeatBoxRepository.getInstance(getContext());
        mSounds = mRepository.getSounds();
        mLiveDataSeekBar = new MutableLiveData<>();
        mLiveDataPlayingSound = mRepository.getLiveDataPlayingSound();
        mIsMusicPlaying = mRepository.isMusicPlaying();
        if (mFlagSeekBar == null)
            mFlagSeekBar = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mRepository.release();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_SEEK_BAR, mFlagSeekBar);
        outState.putSerializable(BUNDLE_KEY_PLAYING_SOUND, mPlayingSoundId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beat_box, container, false);

        findViews(view);
        initViews();
        setLiveDataObservers();
        listeners();
        setupAdapter();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView");
    }


    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_beat_box);
        mImageButton_prev = view.findViewById(R.id.imageBtn_prev_seekbar);
        mImageButton_next = view.findViewById(R.id.imageBtn_next_seekbar);
        mImageButton_Playing = view.findViewById(R.id.imageBtn_pause_seekbar);
        mImageViewSeekBar = view.findViewById(R.id.imageSeekBar);
        mLinearLayoutSeekBar = view.findViewById(R.id.layout_seekBar);
        mSeekBarSoundName = view.findViewById(R.id.text_seekBar_Sound_name);
    }

    private void initViews() {
        if (!mFlagSeekBar)
            mLinearLayoutSeekBar.setVisibility(View.GONE);

        mSeekBarSoundName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mSeekBarSoundName.setSingleLine(true);
        mSeekBarSoundName.setSelected(true);
        mSeekBarSoundName.setMarqueeRepeatLimit(-1);
        int rowNumber = getResources().getInteger(R.integer.row_number);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), rowNumber));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        if (mIsMusicPlaying)
            mImageButton_Playing.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        else
            mImageButton_Playing.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow));

    }

    private void listeners() {

        mImageButton_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.nextSound(mRepository.getSound(mPlayingSoundId));
            }
        });
        mImageButton_Playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRepository.getMediaPlayer().isPlaying()) {
                    mImageButton_Playing.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow));
                    mRepository.pause();
                } else {
                    mImageButton_Playing.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    mRepository.playAgain();
                }
            }
        });
        mImageButton_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.previousSound(mRepository.getSound(mPlayingSoundId));
            }
        });

        mLinearLayoutSeekBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBeatBoxDetail = BeatBoxDetailActivity.newIntent(getActivity(), mPlayingSoundId, mState);
                startActivityForResult(intentBeatBoxDetail, REQUEST_CODE_BEAT_BOX_DETAIL);

            }
        });
    }

    private void setLiveDataObservers() {
        mLiveDataSeekBar.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visibility) {
                if (visibility)
                    mLinearLayoutSeekBar.setVisibility(View.VISIBLE);
            }
        });
        mLiveDataPlayingSound.observe(this, new Observer<Sound>() {
            @Override
            public void onChanged(Sound sound) {
                if (mState.equalsIgnoreCase("Tracks"))
                    mSeekBarSoundName.setText(sound.getTitle());
                else if (mState.equalsIgnoreCase("Artists"))
                    mSeekBarSoundName.setText(sound.getArtist());
                else
                    mSeekBarSoundName.setText(sound.getAlbum());
                mPlayingSoundId = sound.getSoundId();
                if (sound.getBitmap() != null)
                    mImageViewSeekBar.setImageBitmap(sound.getBitmap());
                else
                    mImageViewSeekBar.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));

            }
        });
    }

    private void setupAdapter() {
        List<Sound> sounds = mRepository.getSounds();
        SoundAdapter adapter = new SoundAdapter(sounds);
        mRecyclerView.setAdapter(adapter);
    }

    private class SoundHolder extends RecyclerView.ViewHolder {

        private ImageButton mButton;
        private TextView mTextMusicName;
        private Sound mSound;

        public SoundHolder(@NonNull View itemView) {
            super(itemView);

            mButton = itemView.findViewById(R.id.button_beat_box);
            mTextMusicName = itemView.findViewById(R.id.txt_music_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlagSeekBar = true;
                    mLiveDataSeekBar.postValue(mFlagSeekBar);
                    mLinearLayoutSeekBar.setVisibility(View.VISIBLE);
                    mRepository.loadMusic(mSound.getSoundId());
                    mImageButton_Playing.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    mPlayingSoundId = mSound.getSoundId();
                    if (mState.equalsIgnoreCase("Tracks")) {
                        mSeekBarSoundName.setText(mSound.getTitle());
                    } else if (mState.equalsIgnoreCase("Artists")) {
                        mSeekBarSoundName.setText(mSound.getArtist());
                    } else {
                        mSeekBarSoundName.setText(mSound.getAlbum());
                    }
                    if (mSound.getBitmap() != null) {
                        mImageViewSeekBar.setImageBitmap(mSound.getBitmap());
                    } else {
                        mImageViewSeekBar.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
                    }
                }
            });
        }

        public void bindSound(Sound sound) {
            mSound = sound;
            if (mState.equalsIgnoreCase("Tracks"))
                mTextMusicName.setText(mSound.getTitle());
            else if (mState.equalsIgnoreCase("Artists"))
                mTextMusicName.setText(mSound.getArtist());
            else if (mState.equalsIgnoreCase("Albums"))
                mTextMusicName.setText(mSound.getAlbum());

            if (sound.getBitmap() != null)
                mButton.setImageBitmap(sound.getBitmap());
            else
                mButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        private List<Sound> mSounds;

        public List<Sound> getSounds() {
            return mSounds;
        }

        public void setSounds(List<Sound> sounds) {
            mSounds = sounds;
        }

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @NonNull
        @Override
        public SoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_sound, parent, false);

            return new SoundHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundHolder holder, int position) {
            Sound sound = mSounds.get(position);
            holder.bindSound(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }

}
