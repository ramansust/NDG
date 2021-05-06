package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.model.VideoInfo;

import java.util.List;

/**
 * Created by raman on 1/19/17.
 */
public class VideoAdapter extends BaseAdapter {

    private final Context context;
    private LayoutInflater inflater;
    private final List<VideoInfo> list;

    public VideoAdapter(Context context, List<VideoInfo> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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

        // for loading video thumb using Ion image loader library
        Ion.with(viewHolder.imgView)
                .placeholder(R.drawable.arrow)
                .error(R.drawable.arrow)
                .load(list.get(position).getThumb());

        viewHolder.txtViewTitle.setText(list.get(position).getTitle());
//        viewHolder.txtViewDescription.setText(list.get(position).getDescription());
//        viewHolder.txtViewCount.setText(list.get(position).getViews());

        return convertView;
    }

    /**
     * Declare ViewHolder class for initialized item view
     */
    static class ViewHolder {
        final ImageView imgView;
        final TextView txtViewTitle;
        TextView txtViewDescription;
        TextView txtViewCount;

        public ViewHolder(View view) {
            imgView = view.findViewById(R.id.img_view);
            txtViewTitle = view.findViewById(R.id.txt_video_title);
//            txtViewDescription = (TextView) view.findViewById(R.id.txt_video_description);
//            txtViewCount = (TextView) view.findViewById(R.id.txt_video_count);
        }
    }

}
