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

public class RecipeDetailFragment extends Fragment {

    final static String KEY_POSITION = "POSITION";
    final static String SELECTED_POSITION = "SELECTED_POSITION";
    final static String ARRAY_STEP = "STEPLIST";
    int posisiSekarang = -1;

    private ArrayList<Step> stepList = new ArrayList<Step>();
    private ImageView imageView;
    private TextView textView;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private Handler handler;
    private long position;
    private String uri;


    public RecipeDetailFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("HASILFRAG", "8 Activity Create");
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_POSITION)) {
            posisiSekarang = savedInstanceState.getInt(KEY_POSITION);
            stepList = savedInstanceState.getParcelableArrayList(ARRAY_STEP);
            Log.d("HASILFRAG", "9 stepList " + stepList.size() + " posisi " + posisiSekarang);
            releasePlayer();
            initializePlayer();
            setDescription(posisiSekarang);
        } else {
            stepList = ((RecipeDetailActivity) getActivity()).stepList;
        }
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
            setDescription(getArguments().getInt(KEY_POSITION));
        }

        return view;
    }

    public void setDescription(int descriptionIndex) {
        Log.d("HASILFRAG", "12 Set Description");
        posisiSekarang = descriptionIndex;
        descriptionIndex = descriptionIndex - 1;
        if (descriptionIndex > -1) {
            textView.setText(stepList.get(descriptionIndex).getDescription());

            if (!stepList.get(descriptionIndex).getVideoURL().isEmpty()) {
                imageView.setVisibility(View.GONE);
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                uri = stepList.get(descriptionIndex).getVideoURL();
                //initializePlayer();
            } else {
                if (isVideoFile(stepList.get(descriptionIndex).getThumbnailURL()) && !stepList.get(descriptionIndex).getThumbnailURL().isEmpty()) {
                    uri = stepList.get(descriptionIndex).getThumbnailURL();
                    imageView.setVisibility(View.GONE);
                    simpleExoPlayerView.setVisibility(View.VISIBLE);
                    //initializePlayer();
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    simpleExoPlayerView.setVisibility(View.GONE);
                    if (!stepList.get(descriptionIndex).getThumbnailURL().isEmpty()) {
                        Picasso.with(getActivity()).load(stepList.get(descriptionIndex).getThumbnailURL()).into(imageView);
                    }
                }
            }
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("HASILFRAG", "13 Simpan ke bundle");
        outState.putInt(KEY_POSITION, posisiSekarang);
        outState.putLong(SELECTED_POSITION, position);
        outState.putParcelableArrayList(ARRAY_STEP, stepList);
        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
            Log.d("HASIL", "exoplayer onResume");
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
            player.release();
            player = null;
        }
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

}
