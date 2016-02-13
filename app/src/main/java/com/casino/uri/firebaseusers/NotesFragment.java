package com.casino.uri.firebaseusers;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {
    FirebaseConfig config;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ListView notesList = (ListView) view.findViewById(R.id.LVnotes);
        config  = (FirebaseConfig) getActivity().getApplication();
        Firebase mainReference = config.getMainReference();
        Firebase notesReference = mainReference.child("NotesList");
        FirebaseListAdapter<Note> adapter = new FirebaseListAdapter<Note>(getActivity(), Note.class, R.layout.listview_layout, notesReference) {
            @Override
            protected void populateView(View view, Note note, int position) {
                TextView title = (TextView) view.findViewById(R.id.TVtitle);
                TextView description = (TextView) view.findViewById(R.id.TVdescription);
                TextView latlng = (TextView) view.findViewById(R.id.TVlatlng);
                ImageView image = (ImageView) view.findViewById(R.id.IVimage);
                title.setText(note.getTitle());
                description.setText(note.getDescription());
                if (config.getLanguage().equals("SPANISH"))
                {
                    latlng.setText("Latitud: " + note.getLatitude() + "\nLongitud: " + note.getLongitude());
                }
                else
                {
                    latlng.setText("Latitude: " + note.getLatitude() + "\nLongitude: " + note.getLongitude());
                }
                if (note.getImagePath()==null)
                {
                    Picasso.with(getContext()).load(R.drawable.empty).fit().into(image);
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
