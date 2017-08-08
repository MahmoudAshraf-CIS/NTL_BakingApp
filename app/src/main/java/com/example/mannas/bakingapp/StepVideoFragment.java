package com.example.mannas.bakingapp;


import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.mannas.bakingapp.dummy.Step;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


import java.util.ArrayList;

public class StepVideoFragment extends Fragment implements ExoPlayer.EventListener{

    SimpleExoPlayer player;
    TextView state,index,total,previous,next,title,long_description;
    View loading_indicator,ErrorLoading;
    SimpleExoPlayerView simpleExoPlayerView;
    Uri mp4VideoUri;
    DataSource.Factory dataSourceFactory;
    ExtractorsFactory extractorsFactory;


    public static final String ARG_INGREDIENTS = "param1";
    public static final String ARG_INDEX = "Index";
    ArrayList<Step> mSteps;
    Integer Step_index;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSteps = getArguments().getParcelableArrayList(ARG_INGREDIENTS);
            Step_index = getArguments().getInt(ARG_INDEX);
            Step_index++;
        }
//        mp4VideoUri = Uri.parse(mSteps.get(Step_index -1).videoURL);
        mp4VideoUri = Uri.parse(mSteps.get(Step_index -1).videoURL);

        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(),
               getContext().getPackageName()));
        // Produces Extractor instances for parsing the media data.
        extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        player.addListener(this);
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        player.release();
    }

    void setNextBtnState(){
        Integer index = Step_index-1;
        if(index==mSteps.size()-1){
            next.setEnabled(false);
            next.setBackgroundResource(R.color.inactive);
        }else{
            next.setEnabled(true);
            next.setBackgroundResource(R.color.active);
        }
    }
    void setPreviousBtnState(){
        Integer index = Step_index-1;
        if(index==0){
            previous.setEnabled(false);
            previous.setBackgroundResource(R.color.inactive);
        }else{
            previous.setEnabled(true);
            previous.setBackgroundResource(R.color.active);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.fragment_step_video,container,false);
        simpleExoPlayerView = (SimpleExoPlayerView) r.findViewById(R.id.simpleExoPlayerView);
        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        assert mSteps!=null;
        ErrorLoading = r.findViewById(R.id.error);
        state = (TextView)r.findViewById(R.id.player_state_view);
        long_description = (TextView)r.findViewById(R.id.long_description);
            long_description.setText(mSteps.get(Step_index-1).description);
        index = (TextView)r.findViewById(R.id.index);
            index.setText(Step_index.toString());
        total = (TextView)r.findViewById(R.id.total);
            total.setText(mSteps.size()!=0? Integer.toString(mSteps.size()):"0");
        previous = (TextView)r.findViewById(R.id.previous);
        setPreviousBtnState();
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( Step_index-2 >= 0 && Step_index-2 <= mSteps.size()-1){
                        Step_index--;
                        mp4VideoUri =  Uri.parse(mSteps.get(Step_index -1).videoURL);
                        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
                        player.prepare(videoSource);
                        setPreviousBtnState();
                        setNextBtnState();
                        setViewInfo();
                    }
                }
            });
        next = (TextView)r.findViewById(R.id.next);
        setNextBtnState();
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Step_index >=0 && Step_index <= mSteps.size()-1){
                        Step_index++;
                        mp4VideoUri =  Uri.parse(mSteps.get(Step_index -1).videoURL);
                        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
                        player.prepare(videoSource);
                        setPreviousBtnState();
                        setNextBtnState();
                        setViewInfo();
                    }
                }
            });
        title = (TextView)r.findViewById(R.id.title);
           title.setText(mSteps.get(Step_index-1).shortDescription);
        loading_indicator = r.findViewById(R.id.loading_indicator);
        return r;
    }

    void setViewInfo(){
        title.setText(mSteps.get(Step_index-1).shortDescription);
        index.setText(Step_index.toString());
        long_description.setText(mSteps.get(Step_index-1).description);
    }
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        if(isLoading) {
            loading_indicator.setVisibility(View.VISIBLE);
            simpleExoPlayerView.setVisibility(View.GONE);
            ErrorLoading.setVisibility(View.GONE);
        }
        else{
            loading_indicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        String playerState ="";
        switch (player.getPlaybackState()) {
            case ExoPlayer.STATE_BUFFERING:
                playerState = "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                playerState = "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                playerState = "idle";
                break;
            case ExoPlayer.STATE_READY:
                playerState = "ready";
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                ErrorLoading.setVisibility(View.GONE);
                break;
        }
        if(state!=null)
        state.setText(playerState);
    }
    @Override
    public void onPlayerError(ExoPlaybackException error) {
        ErrorLoading.setVisibility(View.VISIBLE);
        simpleExoPlayerView.setVisibility(View.GONE);
        loading_indicator.setVisibility(View.GONE);
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}


