package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nissan.alldriverguide.R;

/**
 * Created by raman on 1/19/17.
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private int[] thumbnil;
    private Typeface tf;
    private Typeface tfRegular;

    // declare the constructor the need to context and int array
    public GridViewAdapter(Context context, int[] thumbnil) {
        this.context = context;
        this.thumbnil = thumbnil;
        this.tf = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Bold.otf");
        this.tfRegular = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Regular.otf");
        inflater = LayoutInflater.from (this.context);
    }

    @Override
    public int getCount () {
        return thumbnil != null ? thumbnil.length : 0;
    }

    @Override
    public Object getItem (int position) {
        return thumbnil[position];
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate (R.layout.video_row, parent, false);
            viewHolder = new ViewHolder (convertView);
            convertView.setTag (viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag ();
        }

        // set the video thumb in imageView
        viewHolder.imageView.setBackgroundResource(thumbnil[position]);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView txtViewTitle;

        public ViewHolder (View view) {
            imageView = (ImageView) view.findViewById(R.id.img_view);
            txtViewTitle = (TextView) view.findViewById (R.id.txt_video_title);
        }
    }

}
