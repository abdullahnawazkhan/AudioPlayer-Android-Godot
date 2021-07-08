package com.example.audioplayer;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AudioPlayer extends GodotPlugin {
    MediaPlayer mediaPlayer;

    // holds true if audio is playing
    // holds false if audio is not playing
    // duhhh
    boolean is_playing = false;

    // the prepared state of MediaPlayer is a state in which URL has been assigned to it
    // when calling the "stop" function removes URL from MediaPlayer
    // so after every stop, to play again we need to "prepare" the MediaPlayer before
    // please android docs to get a clearer picture of the different states:
        // https://developer.android.com/reference/android/media/MediaPlayer
    boolean is_prepared = false;

    final String TAG = "godot-AudioPlayer";

    public AudioPlayer(Godot godot) {
        super(godot);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                is_prepared = true;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // will run when the audio file has finished
                is_playing = false;
            }
        });
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "AudioPlayer";
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList("start", "pause", "init", "get_is_playing", "stop", "prepare");
    }

    public void init(String url) {
        // used for setting up the url inside the MediaPlayer Class
        // in the case of only 1 verse to be recited, this will only be called once
        // in the case, user needs to recite the entire surah, this method will be called everytime
            // the user successfully recites the current verse and requires the audio for the next verse
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        if (is_playing == false) {
            mediaPlayer.start();
            is_playing = true;
        }
        else {
            Log.d("gd-android-audioplayer", "Audio Is Already Playing");
        }
    }

    public void pause() {
        if (is_playing == true) {
            is_playing = false;
            mediaPlayer.pause();
        }
        else {
            Log.d("gd-android-audioplayer", "Audio Is Not already Playing");
        }
    }

    public void stop() {
        // calling this function removes the URL from the MediaPlayer
        // after this has been called,
        // before calling the "play" function , need to call the "prepare" function
        if (is_prepared == true) {
            is_playing = false;
            is_prepared = false;
            mediaPlayer.stop();
        }
        else {
            Log.d("gd-android-audioplayer", "Can't stop, Audio not Initialized with URL");
        }
    }

    public void prepare() {
        // this function is normally called right after the stop function
        // this takes the already provided URL and prepares the MediaPlayer accordingly
        if (is_prepared == true) {
            Log.d(TAG, "Already Prepared");
        }
        else {
            try {
                mediaPlayer.prepare();
                is_prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean get_is_playing() {
        return is_playing;
    }

    @Override
    public void onMainPause() {
        // this function is called when the user goes outside the app
        // if audio is playing we will pause it
        // or else it will continue to play even tho we have gone out of the application
        super.onMainPause();
        if (is_playing == true) {
            mediaPlayer.pause();
            is_playing = false;
        }
    }
}
