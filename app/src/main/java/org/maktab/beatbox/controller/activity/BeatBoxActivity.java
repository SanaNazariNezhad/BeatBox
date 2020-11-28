package org.maktab.beatbox.controller.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;

import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.maktab.beatbox.controller.fragment.BeatBoxFragment;
import org.maktab.beatbox.repository.BeatBoxRepository;

import java.io.File;
import java.util.ArrayList;

public class BeatBoxActivity extends SingleFragmentActivity {
    private BeatBoxRepository mRepository;



    @Override
    public Fragment createFragment() {
        return BeatBoxFragment.newInstance("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appExternalStoragePermission();
    }

    public void appExternalStoragePermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        displayMusicsName();
                        mRepository.getInstance(getApplicationContext());
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }



}