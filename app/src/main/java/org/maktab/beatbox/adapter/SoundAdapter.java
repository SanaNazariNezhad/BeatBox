package org.maktab.beatbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.maktab.beatbox.R;
import org.maktab.beatbox.databinding.ListItemSoundBinding;
import org.maktab.beatbox.model.Sound;
import org.maktab.beatbox.viewmodel.SoundViewModel;

import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundHolder> {

    private List<Sound> mSounds;
    private Context mContext;

    public List<Sound> getSounds() {
        return mSounds;
    }

    public void setSounds(List<Sound> sounds) {
        mSounds = sounds;
    }

    public SoundAdapter(Context context, List<Sound> sounds) {
        mContext = context.getApplicationContext();
        mSounds = sounds;
    }

    @NonNull
    @Override
    public SoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSoundBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.list_item_sound,
                parent,
                false);

        return new SoundHolder(binding);
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

    class SoundHolder extends RecyclerView.ViewHolder {

        private ListItemSoundBinding mListItemSoundBinding;

        public SoundHolder(ListItemSoundBinding listItemSoundBinding) {
            super(listItemSoundBinding.getRoot());

            mListItemSoundBinding = listItemSoundBinding;
            mListItemSoundBinding.setSoundViewModel(new SoundViewModel(mContext));
        }

        public void bindSound(Sound sound) {
            mListItemSoundBinding.getSoundViewModel().setSound(sound);
            mListItemSoundBinding.executePendingBindings();
        }
    }
}
