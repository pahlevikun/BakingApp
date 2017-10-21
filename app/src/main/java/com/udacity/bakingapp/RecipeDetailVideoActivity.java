package com.udacity.bakingapp;

import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;
import com.udacity.bakingapp.pojo.Step;

import java.util.ArrayList;

public class RecipeDetailVideoActivity extends AppCompatActivity {

    final static String KEY_POSITION = "POSITION";
    final static String SELECTED_POSITION = "SELECTED_POSITION";
    final static String ARRAY_STEP = "STEPLIST";
    final static String PLAY_STATE = "PLAYSTEP";
    private int posisiSekarang = -1;
    private boolean isPlayWhenReady = false;
    private long positionExo = 0;

    private ArrayList<Step> stepList = new ArrayList<Step>();
    private ImageView imageView;
    private TextView textView;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private Handler handler;
    private String uri;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            positionExo = savedInstanceState.getLong(SELECTED_POSITION, 0);
            posisiSekarang = savedInstanceState.getInt(KEY_POSITION);
            stepList = savedInstanceState.getParcelableArrayList(ARRAY_STEP);
            isPlayWhenReady = savedInstanceState.getBoolean(PLAY_STATE);

            Log.d("POSISI", "AMBIL " + positionExo + " " + isPlayWhenReady);
            //releasePlayer();
            setDescription(posisiSekarang);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (player != null) {
            positionExo = player.getCurrentPosition();
            isPlayWhenReady = player.getPlayWhenReady();

            Log.d("POSISI", "SIMPAN NOT NULL " + positionExo + " " + isPlayWhenReady);
        } else {
            positionExo = 0;
            isPlayWhenReady = false;
        }
        outState.putBoolean(PLAY_STATE, isPlayWhenReady);
        outState.putLong(SELECTED_POSITION, positionExo);
        outState.putInt(KEY_POSITION, posisiSekarang);
        outState.putParcelableArrayList(ARRAY_STEP, stepList);
        //releasePlayer();
        Log.d("POSISI", "SIMPAN " + positionExo + " " + isPlayWhenReady);
    }

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
        releasePlayer();
        setDescription(posisiSekarang);

    }


    public void setDescription(int descriptionIndex) {
        try {
            Log.d("HASILFRAG", "12 Set Description");
            posisiSekarang = descriptionIndex;
            Log.d("POSISI", "SET DESC 1 POSISI " + positionExo);
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
                    } else if (!stepList.get(descriptionIndex).getThumbnailURL().isEmpty()) {
                        Picasso.with(this).load(stepList.get(descriptionIndex).getThumbnailURL()).into(imageView);
                    } else {
                        imageView.setVisibility(View.VISIBLE);
                        simpleExoPlayerView.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {

        }
        Log.d("HASIL", "URI " + uri);
        if (player != null) {
            releasePlayer();
            initializePlayer();
            player.seekTo(positionExo);
            player.setPlayWhenReady(isPlayWhenReady);
            Log.d("POSISI", "SET DESC 2 POSISI " + positionExo);
        } else {
            releasePlayer();
            initializePlayer();
            player.seekTo(positionExo);
            player.setPlayWhenReady(isPlayWhenReady);
            Log.d("POSISI", "SET DESC 3 POSISI " + positionExo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
            Log.d("POSISI", "onResume");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (player != null) {
                positionExo = player.getCurrentPosition();
                isPlayWhenReady = player.getPlayWhenReady();
            } else {
                positionExo = 0;
                isPlayWhenReady = false;
            }
            Log.d("POSISI", "onPause " + positionExo + " " + isPlayWhenReady);
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            Log.d("POSISI", "onStop " + positionExo + " " + isPlayWhenReady);
            if (player != null) {
                positionExo = player.getCurrentPosition();
                isPlayWhenReady = player.getPlayWhenReady();
            } else {
                positionExo = 0;
                isPlayWhenReady = false;
            }
            releasePlayer();
        }
    }

    private void initializePlayer() {
        try {
            if (player == null) {
                Log.d("HASIL", "" + uri + " Exoplayer");
                String userAgent = Util.getUserAgent(RecipeDetailVideoActivity.this, "ExoPlayerBakingApp");
                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
                DefaultTrackSelector trackSelector = new DefaultTrackSelector(handler, videoTrackSelectionFactory);
                LoadControl loadControl = new DefaultLoadControl();

                player = ExoPlayerFactory.newSimpleInstance(RecipeDetailVideoActivity.this, trackSelector, loadControl);
                simpleExoPlayerView.setPlayer(player);

                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(uri), new DefaultDataSourceFactory(RecipeDetailVideoActivity.this, userAgent), new DefaultExtractorsFactory(), null, null);

                player.seekTo(positionExo);
                player.setPlayWhenReady(isPlayWhenReady);
                player.prepare(mediaSource);
                player.setPlayWhenReady(true);
                if (getResources().getBoolean(R.bool.isTablet)) {
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                }
            }
        } catch (Exception e) {
            imageView.setVisibility(View.VISIBLE);
            simpleExoPlayerView.setVisibility(View.GONE);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
