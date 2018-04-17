package com.ahmedmatem.android.pfpnotes.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.ahmedmatem.android.pfpnotes.data.DbContract;
import com.ahmedmatem.android.pfpnotes.models.Image;
import com.ahmedmatem.android.pfpnotes.models.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ahmed on 24/03/2018.
 */

public class UploadInfoAsyncTask extends AsyncTask<Context, Void, Map<Note,List<Image>>> {
    private UploadDataListener mListener;

    public interface UploadDataListener {
        void onUploadDataReceived(Map<Note,List<Image>> data);
    }

    public UploadInfoAsyncTask(UploadDataListener listener) {
        mListener = listener;
    }

    @Override
    protected Map<Note,List<Image>> doInBackground(Context... contexts) {
        Context context = contexts[0];

        // get all notes for upload
        Cursor cursor = context.getContentResolver()
                .query(DbContract.NoteEntry.CONTENT_URI,
                        null,
                        DbContract.NoteEntry.COLUMN_STATUS + "!=?",
                        new String[]{String.valueOf(DbContract.NoteEntry.Status.DONE)},
                        DbContract.NoteEntry.COLUMN_DATE + " DESC");

        ArrayList<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()){
            notes.add(new Note(cursor));
        }
        if(notes.size() == 0){
            return null;
        }

        cursor = context.getContentResolver()
                .query(DbContract.ImageEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        DbContract.ImageEntry.COLUMN_NOTE_ID + " DESC");
        Map<Integer, List<Image>> noteImages = new HashMap<>();
        List<Image> images = null;
        int noteId = -1;
        Image currentImage = null;
        while (cursor.moveToNext()){
            int currentNoteId = cursor.getInt(
                    cursor.getColumnIndex(DbContract.ImageEntry.COLUMN_NOTE_ID));
            currentImage = new Image(cursor);
            if (currentNoteId == noteId) {
                images.add(currentImage);
            } else {
                noteImages.put(noteId, images);
                noteId = currentNoteId;
                images = new ArrayList<>();
                images.add(currentImage);
            }
        }
        // put last images for last noteId
        noteImages.put(noteId, images);

        Map<Note, List<Image>> uploadData = null;
        List<Image> imageList;
        for(Note note : notes){
            imageList = noteImages.get(note.getId());
            if(imageList != null) {
                if(uploadData == null){
                    uploadData = new HashMap<>();
                }
                uploadData.put(note, imageList);
            }
        }

        return uploadData;
    }

    @Override
    protected void onPostExecute(Map<Note,List<Image>> data) {
        if(mListener != null){
            mListener.onUploadDataReceived(data);
        }
    }
}
