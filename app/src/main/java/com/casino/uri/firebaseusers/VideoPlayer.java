package com.casino.uri.firebaseusers;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;
import android.media.MediaPlayer;
import android.widget.MediaController;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.Firebase;
public class VideoPlayer extends AppCompatActivity {

    VideoView video;
    String selectedVideoPath = null;
    int selectedItemPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        video = (VideoView) this.findViewById(R.id.VVvideo);
        selectedItemPosition = getIntent().getIntExtra("position", 0);
        FirebaseConfig config  = (FirebaseConfig) this.getApplication();
        Firebase loggedUserNotesReference = config.getLoggedUserReference().child("NotesList");
        loggedUserNotesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int contador = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                {
                    Note note = userSnapshot.getValue(Note.class);
                    if (selectedItemPosition == contador)
                    {
                        if (note.getVideoPath()== null) {warning();}
                        else
                        {
                            selectedVideoPath = note.getVideoPath();
                            playVideo();
                        }
                    }
                    contador++;
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public void warning()
    {
        new AlertDialog.Builder(this)
                .setTitle("   NO VIDEO AVAILABLE")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        goBack();
                    }
                })
                .setIcon(R.drawable.ic_alert)
                .show();
    }
    public void goBack()
    {
        Intent goBack = new Intent(this, MainActivity.class);
        startActivity(goBack);
    }
    public void playVideo()
    {
        video.setVideoPath(selectedVideoPath);
        video.setMediaController(new MediaController(this));
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                video.requestFocus();
                video.start();
            }
        });

    }
}
