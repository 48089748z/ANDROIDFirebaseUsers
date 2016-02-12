package com.casino.uri.firebaseusers;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView notesList = (ListView) view.findViewById(R.id.LVnotes);
        FirebaseConfig config  = (FirebaseConfig) getActivity().getApplication();
        final Firebase mainReference = config.getMainReference();
        final Firebase notesReference = mainReference.child("NotesList");
        FirebaseListAdapter<Note> adapter = new FirebaseListAdapter<Note>(getActivity(), Note.class, R.layout.listview_layout, notesReference) {
            @Override
            protected void populateView(View view, Note note, int position) {
                TextView title = (TextView) view.findViewById(R.id.TVtitle);
                TextView description = (TextView) view.findViewById(R.id.TVdescription);
                TextView latlng = (TextView) view.findViewById(R.id.TVlatlng);
                ImageView image = (ImageView) view.findViewById(R.id.IVimage);
                title.setText(note.getTitle());
                description.setText(note.getDescription());
                latlng.setText("Latitude: " + note.getLatitude() + "\nLongitude: " + note.getLongitude());
                if (note.getImagePath()==null)
                {
                    Picasso.with(getContext()).load(R.drawable.noimage).fit().into(image);
                }
                else
                {
                    File imagePath = new File(note.getImagePath());
                    Picasso.with(getContext()).load(imagePath).centerCrop().resize(185, 185).into(image);
                }
            }
        };
        notesList.setAdapter(adapter);
        return view;
    }
    public NotesFragment() {
    }
}
