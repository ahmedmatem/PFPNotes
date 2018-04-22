package com.ahmedmatem.android.snagging.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ahmedmatem.android.snagging.R;
import com.ahmedmatem.android.snagging.models.Drawing;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DrawingsGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Drawing> mDrawings;

    public DrawingsGridViewAdapter(Context context, List<Drawing> drawings) {
        mContext = context;
        mDrawings = drawings;
    }

    @Override
    public int getCount() {
        return mDrawings != null ? mDrawings.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawingViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater
                    .inflate(R.layout.drawings_grid_view_item, parent, false);
            viewHolder = new DrawingViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DrawingViewHolder) convertView.getTag();
        }

        Drawing drawing = mDrawings.get(position);
        Picasso.get()
                .load(drawing.getPath())
                .into(viewHolder.mDrawingThumbnail);

        return convertView;
    }

    public class DrawingViewHolder {
        private ImageView mDrawingThumbnail;

        public DrawingViewHolder(View convertView) {
            mDrawingThumbnail = (ImageView) convertView.findViewById(R.id.iv_drawing_thumbnail);
        }
    }

    public void notifyDataSetChanged(List<Drawing> drawings) {
        if (drawings != null) {
            mDrawings = drawings;
            notifyDataSetChanged();
        }
    }
}
