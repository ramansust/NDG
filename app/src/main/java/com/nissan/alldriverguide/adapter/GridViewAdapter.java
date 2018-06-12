package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.multiLang.model.ExploreTabVideoModel;

import java.util.ArrayList;

/**
 * Created by raman on 1/19/17.
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private Typeface tf;
    private Typeface tfRegular;
    private ArrayList<ExploreTabVideoModel> video_list;
    private String device_density;
    private String img_name;
    private Uri uri;

    public GridViewAdapter(Context context, ArrayList<ExploreTabVideoModel> video_list, String device_density) {
        this.context = context;
        this.video_list = video_list;
        this.device_density = device_density;
        this.tf = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Bold.otf");
        this.tfRegular = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Regular.otf");
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return video_list != null ? video_list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return video_list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.video_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //viewHolder.imageView.setBackgroundResource(thumbnil[position]);
        if (device_density.equalsIgnoreCase("xxxhdpi")) {
            img_name = video_list.get(position).getThumbXxxhdpi();
        } else if (device_density.equalsIgnoreCase("xxhdpi")) {
            img_name = video_list.get(position).getThumbXxhdpi();
        } else if (device_density.equalsIgnoreCase("xhdpi")) {
            img_name = video_list.get(position).getThumbXhdpi();
        } else if (device_density.equalsIgnoreCase("hdpi")) {
            img_name = video_list.get(position).getThumbHdpi();
        } else if (device_density.equalsIgnoreCase("ldpi")) {
            img_name = video_list.get(position).getThumbLdpi();
        } else {
            img_name = video_list.get(position).getThumbXhdpi();
        }

        uri = Uri.parse(img_name);

        /*Glide.with(context)
                .load(img_name)
                .into(viewHolder.imageView);*/

        viewHolder.imageView.setImageURI(uri);

        return convertView;
    }

    public void setList(ArrayList<ExploreTabVideoModel> videoList) {

        this.video_list = videoList;

    }

    static class ViewHolder {
        SimpleDraweeView imageView;
        TextView txtViewTitle;

        public ViewHolder(View view) {
            imageView = (SimpleDraweeView) view.findViewById(R.id.img_view);
            txtViewTitle = (TextView) view.findViewById(R.id.txt_video_title);
        }
    }

}
