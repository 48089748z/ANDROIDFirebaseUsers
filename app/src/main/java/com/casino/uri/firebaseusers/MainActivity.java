package com.casino.uri.firebaseusers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import com.firebase.client.Firebase;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private FirebaseConfig config = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        config  = (FirebaseConfig) this.getApplication();
        CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager(CustomViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NotesFragment(), "NOTES");
        adapter.addFragment(new MapFragment(), "MAP");
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);
    }
    public void showAlert()
    {
        new AlertDialog.Builder(this)
                .setTitle("   DELETE ALL NOTES?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Firebase mainReference = config.getMainReference();
                        Firebase notes = mainReference.child("NotesList");
                        notes.removeValue();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){}
                })
                .setIcon(R.drawable.ic_alert)
                .show();
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentsList = new ArrayList<>();
        private final List<String> fragmentsTitlesList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }
        @Override
        public int getCount() {
            return fragmentsList.size();
        }
        public void addFragment(Fragment fragment, String title) {
            fragmentsList.add(fragment);
            fragmentsTitlesList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitlesList.get(position);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            showAlert();
            return true;
        }
        if (id == R.id.action_newnote) {
            Intent newNoteActivity = new Intent(this, NewNoteActivity.class);
            startActivity(newNoteActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
