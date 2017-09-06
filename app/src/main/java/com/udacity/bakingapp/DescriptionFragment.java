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
import android.widget.Toast;

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

/**
 * Created by derrickolivier on 3/01/15.
 */
public class DescriptionFragment extends Fragment {

    final static String KEY_POSITION = "position";
    int mCurrentPosition = -1;


    private ArrayList<Step> stepList = new ArrayList<Step>();
    private ImageView imageView, imagePrev, imageNext;
    private TextView textView;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private Handler handler;
    private long position;
    private String SELECTED_POSITION = "SELECTED_POSITION", uri;


    public DescriptionFragment(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stepList = ((RecipeDetailActivity)getActivity()).stepList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null){
            mCurrentPosition = savedInstanceState.getInt(KEY_POSITION);
        }
        View view = inflater.inflate(R.layout.fragment_description, container, false);

        simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.playerView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textView = (TextView) view.findViewById(R.id.textViewStep);
        imagePrev = (ImageView) view.findViewById(R.id.imageViewPrevious);
        imageNext = (ImageView) view.findViewById(R.id.imageViewNext);
        handler = new Handler();
        bandwidthMeter = new DefaultBandwidthMeter();

        return view;
    }

    public void setDescription(int descriptionIndex){
        descriptionIndex = descriptionIndex-1;
        if (descriptionIndex!=-1){
            Toast.makeText(getActivity(), "!-1", Toast.LENGTH_SHORT).show();
            textView.setText(stepList.get(descriptionIndex).getDescription());

            if (stepList.get(descriptionIndex).getVideoURL().isEmpty()) {
                if (stepList.get(descriptionIndex).getThumbnailURL().isEmpty()){
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
            mCurrentPosition = descriptionIndex;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION,mCurrentPosition);
        outState.putLong(SELECTED_POSITION, position);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
        Bundle args = getArguments();
        if (args != null){
            setDescription(args.getInt(KEY_POSITION));
        } else if(mCurrentPosition != -1){
            setDescription(mCurrentPosition);
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

    private void initializePlayer() {
        try{
            if (stepList.get(mCurrentPosition).getVideoURL().isEmpty()) {
                if (!stepList.get(mCurrentPosition).getThumbnailURL().isEmpty()){
                    uri=stepList.get(mCurrentPosition).getThumbnailURL();
                }
            } else {
                uri=stepList.get(mCurrentPosition).getVideoURL();
            }

            String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerBakingApp");
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(handler, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(player);

            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(uri), new DefaultDataSourceFactory(getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
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

}
