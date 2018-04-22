package com.ahmedmatem.android.snagging;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ahmedmatem.android.snagging.helpers.ImageHelper;
import com.ahmedmatem.android.snagging.net.SavePhoto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;

public class DrawingsListActivity extends AppCompatActivity implements DrawingsListActivityFragment.OnDrawingsListChanged{
    static final int REQUEST_TAKE_PHOTO = 1;

    private static Uri mPhotoUri;
    private DatabaseReference mDatabaseReference;

    private DrawingsListActivityFragment mDrawingsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawings_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawingsListFragment = (DrawingsListActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.drawings_list_fragment);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.addValueEventListener(mDrawingsListFragment);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = new ImageHelper(this).createImageFile();
            } catch (IOException ex) {
                // TODO: Error occurred while creating the File
            }

            if (photoFile != null) {
                mPhotoUri = FileProvider.getUriForFile(this,
                        Config.FILE_PROVIDER_AUTHORITY, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDatabaseReference != null) {
            mDatabaseReference.removeEventListener(mDrawingsListFragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (mPhotoUri != null) {
                new SavePhoto(mDrawingsListFragment).execute(mPhotoUri);
            }
        }
    }

    @Override
    public void onDrawingsListChanged() {

    }
}
