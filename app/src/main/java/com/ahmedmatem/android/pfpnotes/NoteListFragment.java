package com.ahmedmatem.android.pfpnotes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmedmatem.android.pfpnotes.asynctasks.DetailAsyncTask;
import com.ahmedmatem.android.pfpnotes.data.DbContract;
import com.ahmedmatem.android.pfpnotes.data.Preferences;
import com.ahmedmatem.android.pfpnotes.data.adapters.NoteAdapter;
import com.ahmedmatem.android.pfpnotes.data.daos.ImageDAO;
import com.ahmedmatem.android.pfpnotes.data.daos.PlaceDAO;
import com.ahmedmatem.android.pfpnotes.models.Item;
import com.ahmedmatem.android.pfpnotes.models.NoteItem;
import com.ahmedmatem.android.pfpnotes.models.NoteModel;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.ahmedmatem.android.pfpnotes.DetailActivity.POSITION_IN_DETAIL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoteListListener} interface
 * to handle interaction events.
 * Use the {@link NoteListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        NoteAdapter.NoteClickListener {
    public static final int REQUEST_CODE_DETAIL_VIEW = 1;
    public static final String POSITION = "position";
    private static final int NOTE_LOADER_ID = 5;
    public static final String NOTE_ID = "note_id";

    public static final String ARG_NOTE = "note";

    private NoteModel mNote;

    private NoteListListener mListener;

    private static LoaderManager mLoaderManager;

    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;

    private int mPositionInDetail;

    public NoteListFragment() {
        // Required empty public constructor
    }

    public static NoteListFragment newInstance(LoaderManager loaderManager, int position) {
        mLoaderManager = loaderManager;
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mPositionInDetail = args.getInt(POSITION);

        mLoaderManager.initLoader(NOTE_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        mRecyclerView = view.findViewById(R.id.rv_notes);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NoteListListener) {
            mListener = (NoteListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnKeyboardInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                DbContract.NoteEntry.CONTENT_URI,
                null,
                DbContract.NoteEntry.FULL_EMAIL + "=?",
                new String[]{new Preferences(getContext()).readUserEmail()},
                DbContract.NoteEntry.COLUMN_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new NoteAdapter(this, data, getContext(), getLayoutInflater());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        int positionInMaster = getPositionInMaster(mAdapter.getData(), mPositionInDetail--);
        mRecyclerView.scrollToPosition(positionInMaster);

        if (getResources().getBoolean(R.bool.twoPane)) {
            if (mListener != null) {
                mListener.onNotesLoadFinished();
            }
        }
    }

    private int getPositionInMaster(ArrayList<Item> data, int positionInDetail) {
        if(data == null) {
            return 0;
        }
        NoteItem currentItem;
        for (Item item : data){
            if(item instanceof NoteItem){
                currentItem = (NoteItem) item;
                if(currentItem.getPositionInDetail() == positionInDetail){
                    return currentItem.getPositionInMaster();
                }
            }
        }
        return 0;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onActionClick(NoteItem item, int actionId) {
        switch (actionId) {
            case R.id.btn_edit:
                startNoteEditActivity(item);
                break;
            case R.id.btn_detail:
                if (getResources().getBoolean(R.bool.twoPane)) {
                    // two pane mode
                    new DetailAsyncTask((DetailAsyncTask.DetailListener) getContext(), item.getId())
                            .execute(getContext());
                } else {
                    startDetailActivity(item);
                }
                break;
        }
    }

    private void startNoteEditActivity(NoteItem item) {
        String fullPlaceName = item.getPlace();
        String shortPlaceName = new PlaceDAO(getContext().getContentResolver())
                .getShortPlaceNameBy(fullPlaceName);
        ArrayList<String> paths = new ImageDAO(getContext()
                .getContentResolver())
                .getPaths(item.getId());

        mNote = new NoteModel(
                item.getId(),
                shortPlaceName,
                fullPlaceName,
                item.getDimension(),
                paths,
                item.getNoteStatus());

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_NOTE, mNote);

        Intent intent = new Intent(getContext(), NoteEditActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void startDetailActivity(NoteItem item) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(NOTE_ID, item.getId());
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_DETAIL_VIEW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_DETAIL_VIEW) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if(bundle != null && bundle.containsKey(POSITION_IN_DETAIL)) {
                    int positionInDetail = bundle.getInt(POSITION_IN_DETAIL);
                    int positionInMaster = getPositionInMaster(mAdapter.getData(), positionInDetail);
                    mRecyclerView.scrollToPosition(positionInMaster);
                }
            }
        }
    }

    public interface NoteListListener {
        void onNotesLoadFinished();
    }

    public NoteAdapter getAdapter() {
        return mAdapter;
    }
}

