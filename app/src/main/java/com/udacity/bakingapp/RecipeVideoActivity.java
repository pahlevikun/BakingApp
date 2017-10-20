package com.udacity.bakingapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.bakingapp.pojo.Step;

import java.util.ArrayList;

public class RecipeVideoActivity extends AppCompatActivity {

    final static String KEY_POSITION = "POSITION";
    final static String SELECTED_POSITION = "SELECTED_POSITION";
    final static String ARRAY_STEP = "STEPLIST";
    int posisiSekarang = -1;
    long videoPost;

    private ArrayList<Step> stepList = new ArrayList<Step>();
    private ImageView imageView;
    private TextView textView;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private Handler handler;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.playerView);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textViewStep);
        handler = new Handler();
        bandwidthMeter = new DefaultBandwidthMeter();

        stepList = getIntent().getParcelableArrayListExtra("ARRAY");
        posisiSekarang = getIntent().getIntExtra("POSISIS", 0);
        Log.d("HASIL", stepList.size() + " " + posisiSekarang);
        setDescription(posisiSekarang);

    }

    public void setDescription(int descriptionIndex) {
        try {
            Log.d("HASILFRAG", "12 Set Description");
            posisiSekarang = descriptionIndex;
            Log.d("HASIL", "keempat " + stepList.get(descriptionIndex).getThumbnailURL().endsWith(".mp4") + " " + stepList.get(descriptionIndex).getVideoURL());
            if (descriptionIndex > -1) {
                textView.setText(stepList.get(descriptionIndex).getDescription());

                if (!stepList.get(descriptionIndex).getVideoURL().isEmpty()) {
                    imageView.setVisibility(View.GONE);
                    simpleExoPlayerView.setVisibility(View.VISIBLE);
                    uri = stepList.get(descriptionIndex).getVideoURL();
                    //initializePlayer();
                } else {
                    if (stepList.get(descriptionIndex).getThumbnailURL().endsWith(".mp4")) {
                        uri = stepList.get(descriptionIndex).getThumbnailURL();
                        imageView.setVisibility(View.GONE);
                        simpleExoPlayerView.setVisibility(View.VISIBLE);
                    } else {
                        imageView.setVisibility(View.VISIBLE);
                        simpleExoPlayerView.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer(Uri.parse(uri));
            Log.d("HASIL", "exoplayer onResume");
        }else{
            player.seekTo(videoPost);
            Log.d("POSISI", "RESUME " + videoPost);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        videoPost = player.getCurrentPosition();
        outState.putLong(SELECTED_POSITION, videoPost);
        Log.d("POSISI", "SIMPAN " + videoPost);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState != null) {
                videoPost = savedInstanceState.getLong(SELECTED_POSITION, 0);
            }
        }
    }

//    private void initializePlayer() {
//        try {
//            if (player == null) {
//                Log.d("HASIL", "" + uri + " Exoplayer");
//                String userAgent = Util.getUserAgent(RecipeVideoActivity.this, "ExoPlayerBakingApp");
//                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
//                DefaultTrackSelector trackSelector = new DefaultTrackSelector(handler, videoTrackSelectionFactory);
//                LoadControl loadControl = new DefaultLoadControl();
//
//                player = ExoPlayerFactory.newSimpleInstance(RecipeVideoActivity.this, trackSelector, loadControl);
//                simpleExoPlayerView.setPlayer(player);
//
//                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(uri), new DefaultDataSourceFactory(RecipeVideoActivity.this, userAgent), new DefaultExtractorsFactory(), null, null);
//                player.prepare(mediaSource);
//                player.setPlayWhenReady(true);
//                if (getResources().getBoolean(R.bool.isTablet)) {
//                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
//                }
//            }
//        } catch (Exception e) {
//            imageView.setVisibility(View.VISIBLE);
//            simpleExoPlayerView.setVisibility(View.GONE);
//        }
//    }

    private void initializePlayer(Uri mediaUri) {
        if (player == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(handler, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(RecipeVideoActivity.this, trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(player);
            String userAgent = Util.getUserAgent(RecipeVideoActivity.this, "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(RecipeVideoActivity.this, userAgent), new DefaultExtractorsFactory(), null, null);
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
