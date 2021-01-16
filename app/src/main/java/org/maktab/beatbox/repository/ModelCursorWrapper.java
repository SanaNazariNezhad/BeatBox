package org.maktab.beatbox.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import org.maktab.beatbox.model.Sound;

public class ModelCursorWrapper extends CursorWrapper {
    private Context mContext;
    private MediaMetadataRetriever metaRetriever;

        public ModelCursorWrapper(Cursor cursor) {
            super(cursor);
        }

    public void setContext(Context context) {
        mContext = context;
    }

    public Sound getSong(Uri contentUri) {
        metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(mContext,contentUri);
        Bitmap mBitmap;

        String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        byte[] data = metaRetriever.getEmbeddedPicture();
        if (data != null) {
            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        } else {
            mBitmap = null;
        }
        Sound sound = new Sound();
        sound.setSoundUri(contentUri);
        sound.setTitle(title);
        sound.setArtist(artist);
        sound.setAlbum(album);
        sound.setBitmap(mBitmap);
        metaRetriever.release();
        return sound;
    }
}
