package com.ahmedmatem.android.snagging.net;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.ahmedmatem.android.snagging.Config;
import com.ahmedmatem.android.snagging.models.Drawing;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

public class SavePhoto extends AsyncTask<Uri, Void, Void> {
    private OnPhotoSaved mListener;

    public interface OnPhotoSaved{
        void onPhotoSaved(Drawing drawing);
    }

    public SavePhoto(OnPhotoSaved listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Uri... uris) {
        Uri uri = uris[0];
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageReference.child(Config.DRAWINGS_REF + "/" +
                uri.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(uri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // TODO: Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Drawing drawing = new Drawing(String.valueOf(Calendar.getInstance().getTimeInMillis()),
                        downloadUrl.toString());
                if (mListener != null) {
                    mListener.onPhotoSaved(drawing);
                }
                savePhotoProperties(drawing);
            }
        });

        removePhotoFromInternalStorage(uri);

        return null;
    }

    private void removePhotoFromInternalStorage(Uri uri) {
        // TODO: remove file from internal storage
    }

    private void savePhotoProperties(Drawing drawing) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Config.DRAWINGS_REF)
                .child(drawing.getId())
                .setValue(drawing);
    }
}
