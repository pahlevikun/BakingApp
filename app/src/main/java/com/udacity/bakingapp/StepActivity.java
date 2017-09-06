package com.udacity.bakingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
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

public class StepActivity extends AppCompatActivity {

    private ArrayList<Step> stepList = new ArrayList<>();
    private ImageView imageView, imagePrev, imageNext;
    private TextView textView;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private Handler handler;
    private int id;
    private long position;
    private String SELECTED_POSITION = "SELECTED_POSITION", uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.playerView);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textViewStep);
        imagePrev = (ImageView) findViewById(R.id.imageViewPrevious);
        imageNext = (ImageView) findViewById(R.id.imageViewNext);
        handler = new Handler();
        bandwidthMeter = new DefaultBandwidthMeter();

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getIntExtra("id", 0);
            setTitle("Step-" + id);
            if (intent.hasExtra("parcel")) {
                stepList = intent.getParcelableArrayListExtra("parcel");
            } else {
                finish();
            }
        }

        textView.setText(stepList.get(id).getDescription());

        if (stepList.get(id).getVideoURL().isEmpty()) {
            if (stepList.get(id).getThumbnailURL().isEmpty()){
                imageView.setVisibility(View.VISIBLE);
                simpleExoPlayerView.setVisibility(View.GONE);
            }else {
                initializePlayer();
            }
        } else {
            imageView.setVisibility(View.GONE);
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            initializePlayer();
        }

        imageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id >= (stepList.size() - 1)) {
                    Toast.makeText(StepActivity.this, "Final Step!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(StepActivity.this, StepActivity.class);
                    intent.putExtra("id", (id + 1));
                    intent.putParcelableArrayListExtra("parcel", stepList);
                    startActivity(intent);
                    finish();
                }
            }
        });

        imagePrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((stepList.size() - 1) < 0) {
                    Toast.makeText(StepActivity.this, "First Step!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(StepActivity.this, StepActivity.class);
                    intent.putExtra("id", (id - 1));
                    intent.putParcelableArrayListExtra("parcel", stepList);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            position = player.getCurrentPosition();
            player.stop();
            player.release();
            player = null;
        }
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    /*@Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }*/

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putLong(SELECTED_POSITION, position);
    }


    private void initializePlayer() {
        try{
            if (stepList.get(id).getVideoURL().isEmpty()) {
                if (!stepList.get(id).getThumbnailURL().isEmpty()){
                    uri=stepList.get(id).getThumbnailURL();
                }
            } else {
                uri=stepList.get(id).getVideoURL();
            }

            String userAgent = Util.getUserAgent(StepActivity.this, "ExoPlayerBakingApp");
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(handler, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(StepActivity.this, trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(player);

            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(uri), new DefaultDataSourceFactory(StepActivity.this, userAgent), new DefaultExtractorsFactory(), null, null);
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
            if (getResources().getBoolean(R.bool.isTablet)){
                simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            }
        }catch (Exception e){
            imageView.setVisibility(View.VISIBLE);
            simpleExoPlayerView.setVisibility(View.GONE);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
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
