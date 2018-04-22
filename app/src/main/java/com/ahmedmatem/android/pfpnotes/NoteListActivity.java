package com.ahmedmatem.android.pfpnotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ahmedmatem.android.pfpnotes.asynctasks.DetailAsyncTask;
import com.ahmedmatem.android.pfpnotes.data.Preferences;
import com.ahmedmatem.android.pfpnotes.data.adapters.DetailPagerAdapter;
import com.ahmedmatem.android.pfpnotes.models.Image;
import com.ahmedmatem.android.pfpnotes.models.Item;
import com.ahmedmatem.android.pfpnotes.models.Note;
import com.ahmedmatem.android.pfpnotes.models.NoteItem;
import com.ahmedmatem.android.snagging.Snagging;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoteListActivity extends AppCompatActivity
        implements NoteListFragment.NoteListListener,
        DetailAsyncTask.DetailListener,
        DetailFragment.OnDetailFragmentListener {
    private MenuItem mSignInMenuItem;
    private MenuItem mSignOutMenuItem;
    private NoteListFragment mNoteListFragment;

    // two pane properties
    private DetailPagerAdapter mPagerAdapter;
    private ViewPager mDetailViewPager;
    private int mCurrentIndex = -1;

    private int mPositionInDetail = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mNoteListFragment = NoteListFragment.newInstance(getSupportLoaderManager(),
                    mPositionInDetail);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.note_list_fragment_container, mNoteListFragment)
                    .commit();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteListActivity.this, NoteAddActivity.class);
                startActivity(intent);
            }
        });

        // two pane mode
        if (getResources().getBoolean(R.bool.twoPane)) {
            mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), null);
            mDetailViewPager = findViewById(R.id.note_detail_view_pager);
            mDetailViewPager.setAdapter(mPagerAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        mSignInMenuItem = menu.findItem(R.id.action_sign_in);
        mSignOutMenuItem = menu.findItem(R.id.action_sign_out);
        updateMenu();
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void updateMenu() {
        boolean isSignedIn = new Preferences(this).isSignedIn();
        mSignInMenuItem.setVisible(!isSignedIn);
        mSignOutMenuItem.setVisible(isSignedIn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Intent intent;
        switch (itemId) {
            case R.id.action_price_list:
                intent = new Intent(this, PriceListActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_places:
                intent = new Intent(this, PlaceListActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sign_in:
                intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_upload:
                intent = new Intent(this, UploadActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_drawings:
                Snagging.start(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        new Preferences(this).setSignedIn(false);
        updateMenu();
        Toast.makeText(this, R.string.toast_sign_out, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNotesLoadFinished() {
        // two pane mode
        if (getResources().getBoolean(R.bool.twoPane)) {
            if (mNoteListFragment.getAdapter() != null) {
                ArrayList<Item> items = mNoteListFragment.getAdapter().getData();
                if (items != null) {
                    for (Item item : items) {
                        if (item instanceof NoteItem) {
                            NoteItem noteItem = (NoteItem) item;
                            int noteId = ((NoteItem) item).getId();
                            // load detail data asynchronously
                            new DetailAsyncTask(this, noteId).execute(this);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDetailLoadFinished(Map<Note, List<Image>> data, int currentItem) {
        // two pane mode
        if (getResources().getBoolean(R.bool.twoPane)) {
            mPagerAdapter.setData(data);
            if (mCurrentIndex != -1) {
                mDetailViewPager.setCurrentItem(mCurrentIndex);
            } else {
                mDetailViewPager.setCurrentItem(currentItem);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // from detail fragment listener
    }
}
