package com.ahmedmatem.android.snagging;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.ahmedmatem.android.snagging.adapters.DrawingsGridViewAdapter;
import com.ahmedmatem.android.snagging.models.Drawing;
import com.ahmedmatem.android.snagging.net.SavePhoto;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DrawingsListActivityFragment extends Fragment implements ValueEventListener,
        SavePhoto.OnPhotoSaved{
    private static final String TAG = "DrawingsListActivityFra";

    private ArrayList<Drawing> mDrawingsList;
    private OnDrawingsListChanged mListener;

    public interface OnDrawingsListChanged {
        void onDrawingsListChanged();
    }

    private GridView mDrawingsGridView;

    public DrawingsListActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawingsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_drawings_list, container, false);

        mDrawingsGridView = (GridView) rootView.findViewById(R.id.drawings_grid_view);
        DrawingsGridViewAdapter adapter = new DrawingsGridViewAdapter(getContext(), mDrawingsList);
        mDrawingsGridView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDrawingsListChanged) {
            mListener = (OnDrawingsListChanged) context;
        } else {
            throw new ClassCastException(getContext().getClass().getSimpleName() +
                    " must implement interface DrawingsListActivityFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) {
            mListener = null;
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> dataSnapshots = dataSnapshot
                .child(Config.DRAWINGS_REF)
                .getChildren();
        Drawing drawing;
        for (DataSnapshot ds : dataSnapshots) {
            drawing = ds.getValue(Drawing.class);
            mDrawingsList.add(drawing);
        }
        updateUI();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onPhotoSaved(Drawing drawing) {
        if (mDrawingsList == null) {
            mDrawingsList = new ArrayList<>();
        }
        mDrawingsList.add(drawing);
        updateUI();
    }

    private void updateUI() {
        DrawingsGridViewAdapter adapter = (DrawingsGridViewAdapter) mDrawingsGridView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged(mDrawingsList);
        }
    }
}
