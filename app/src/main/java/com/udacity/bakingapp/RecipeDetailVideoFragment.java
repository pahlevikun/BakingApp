package com.udacity.bakingapp;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.net.URLConnection;
import java.util.ArrayList;

public class RecipeDetailVideoFragment extends Fragment {

    final static String KEY_POSITION = "POSITION";
    final static String SELECTED_POSITION = "SELECTED_POSITION";
    final static String ARRAY_STEP = "STEPLIST";
    final static String PLAY_STATE = "PLAYSTEP";
    int posisiSekarang = -1;

    private ArrayList<Step> stepList = new ArrayList<Step>();
    private ImageView imageView;
    private TextView textView;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private Handler handler;
    private boolean isPlayWhenReady = false;
    private long positionExo = 0;
    private String uri;


    public RecipeDetailVideoFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("HASILFRAG", "8 Activity Create");
        if (savedInstanceState != null) {
            positionExo = savedInstanceState.getLong(SELECTED_POSITION, 0);
            posisiSekarang = savedInstanceState.getInt(KEY_POSITION);
            stepList = savedInstanceState.getParcelableArrayList(ARRAY_STEP);
            isPlayWhenReady = savedInstanceState.getBoolean(PLAY_STATE);

            Log.d("POSISI", "AMBIL " + positionExo + " " + isPlayWhenReady);
            //releasePlayer();
            setDescription(posisiSekarang);
        } else {
            stepList = ((RecipeDetailFragActivity) getActivity()).stepList;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_description, container, false);
        Log.d("HASILFRAG", "10 Create View");

        simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.playerView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textView = (TextView) view.findViewById(R.id.textViewStep);
        handler = new Handler();
        bandwidthMeter = new DefaultBandwidthMeter();

        if (getArguments() != null && getArguments().containsKey(KEY_POSITION)) {
            Log.d("HASILFRAG", "11 Argument not null get argument");
            stepList = getArguments().getParcelableArrayList(ARRAY_STEP);
            releasePlayer();
            setDescription(getArguments().getInt(KEY_POSITION));
        }

        return view;
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
                        Picasso.with(getActivity()).load(stepList.get(descriptionIndex).getThumbnailURL()).into(imageView);
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
                String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerBakingApp");
                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
                DefaultTrackSelector trackSelector = new DefaultTrackSelector(handler, videoTrackSelectionFactory);
                LoadControl loadControl = new DefaultLoadControl();

                player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
                simpleExoPlayerView.setPlayer(player);

                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(uri), new DefaultDataSourceFactory(getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);

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

}
