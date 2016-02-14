package com.casino.uri.firebaseusers;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class NewNoteActivity extends AppCompatActivity  implements LocationListener
{
    FirebaseConfig config;
    boolean tookPhoto = false;
    Location loc;
    ProgressDialog dialog;
    ImageView takenPhoto;
    TextView title;
    TextView description;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        config  = (FirebaseConfig) this.getApplication();
        takenPhoto = (ImageView) this.findViewById(R.id.IVtakenPhoto);
        title = (TextView) this.findViewById(R.id.ETtitle);
        description = (TextView) this.findViewById(R.id.ETdescription);
        if(config.getLanguage().equals("SPANISH"))
        {
            dialog = ProgressDialog.show(this,"    ENCIENDE EL GPS","Intentamos localizarte.\nEsto puede tardar unos segundos.");
            title.setHint("Titulo de la Nota");
            description.setHint("Descripción");
        }
        else
        {
            dialog = ProgressDialog.show(this, "  TURN ON YOUR GPS PLEASE","We are getting your location.\nThis may take a few seconds.");
            title.setHint("Note Title");
            description.setHint("Note Description");
        }
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        FloatingActionButton add = (FloatingActionButton) this.findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Note newNote = new Note();
                newNote.setTitle(title.getText().toString());
                newNote.setDescription(description.getText().toString());
                newNote.setLatitude(String.valueOf(loc.getLatitude()));
                newNote.setLongitude(String.valueOf(loc.getLongitude()));
                if (tookPhoto)
                {
                    newNote.setImagePath(getLastPhotoPath());
                    //newNote.setCodedImage(codeImage(getLastPhotoPath())); FULLY SAVE IMAGES IN FIREBASE MAKES YOU RUN OUT OF MEMORY
                }
                addNoteToFireBase(newNote);
            }
        });
    }
    public void openCamera()
    {
        tookPhoto = true;
        Intent openCamera = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivity(openCamera);
    }
    public String getLastPhotoPath()
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToLast();
        return cursor.getString(column_index_data);
    }
    public void addNoteToFireBase(Note note)
    {
        Firebase loggedUserNotesReference = config.getLoggedUserReference().child("NotesList");
        Firebase fnote = loggedUserNotesReference.push();
        fnote.setValue(note);
        this.finish();
    }
    public void onStart()
    {
        super.onStart();
        if (tookPhoto)
        {
            File imagePath = new File(getLastPhotoPath());
            Picasso.with(this).load(imagePath).centerCrop().resize(230, 290).into(takenPhoto);
        }
    }
    @Override
    public void onLocationChanged(Location location)
    {
        if (location!=null) {
            loc = location;
            dialog.dismiss();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newnote, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_takephoto) {
            openCamera();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String codeImage(String path) //FULLY SAVE IMAGES IN FIREBASE MAKES YOU RUN OUT OF MEMORY
    {
        File imagePath = new  File(path);
        if(imagePath.exists())
        {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imageBitmap.recycle();
            byte[] byteArray = baos.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return null;
    }
}
