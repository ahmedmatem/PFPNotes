package com.ahmedmatem.android.pfpnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ahmedmatem.android.pfpnotes.asynctasks.DeleteImageAsyncTask;
import com.ahmedmatem.android.pfpnotes.common.ImageHelper;
import com.ahmedmatem.android.pfpnotes.interfaces.OnDatabaseListener;
import com.ahmedmatem.android.pfpnotes.models.NoteModel;
import com.ahmedmatem.android.pfpnotes.asynctasks.NoteUpdateAsyncTask;
import com.ahmedmatem.android.pfpnotes.ui.DeleteDialogFragment;

import java.io.File;
import java.io.IOException;

import static com.ahmedmatem.android.pfpnotes.NoteListFragment.ARG_NOTE;


public class NoteEditActivity extends AppCompatActivity implements
        NoteAddEditFragment.OnFragmentInteractionListener,
        OnDatabaseListener, DeleteDialogFragment.DeleteDialogListener {

    public static final int DIMENSION_REQUEST = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;

    private NoteAddEditFragment mFragment;
    private NoteModel mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        Bundle bundle = getIntent().getExtras();
        mNote = (NoteModel) bundle.getParcelable(ARG_NOTE);

        mFragment = NoteAddEditFragment.newInstance(getSupportLoaderManager(), mNote);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.edit_fragment_container, mFragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edit_note) {
            new NoteUpdateAsyncTask(this, mNote).execute(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFragment != null) {
            mFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDimensionClick(View view) {
        Intent intent = new Intent(this, DimensionActivity.class);
        startActivityForResult(intent, DIMENSION_REQUEST);
    }

    @Override
    public void onCameraClick(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new ImageHelper(this).createImageFile();
            } catch (IOException e) {

            }

            // continue if file was successfully created
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.ahmedmatem.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onUserInputChanged() {
    }

    @Override
    public void onDataSaved() {
        Intent intent = new Intent(this, NoteListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDataDeleted() {
        if(mNote.getPaths().remove(mNote.getImageToDelete())){
            // this will cause notify data set changed
            mFragment.getImageAdapter().setPaths(mNote.getPaths());
        }
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        new DeleteImageAsyncTask(this).execute(mNote);
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}

