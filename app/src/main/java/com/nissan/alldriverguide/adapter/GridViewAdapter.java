package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.fragments.explore.ExploreFragment;
import com.nissan.alldriverguide.multiLang.model.ExploreTabVideoModel;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * Created by raman on 1/19/17.
 */
public class GridViewAdapter extends BaseAdapter {

    private final Context context;
    private LayoutInflater inflater;
    private ArrayList<ExploreTabVideoModel> video_list;
    private final String device_density;

    public GridViewAdapter(Context context, ArrayList<ExploreTabVideoModel> video_list, String device_density) {
        this.context = context;
        this.video_list = video_list;
        this.device_density = device_density;
        inflater = LayoutInflater.from(this.context);
/*
        if (Values.carType == 11) {
            for (Iterator<ExploreTabVideoModel> iterator = this.video_list.listIterator(); iterator.hasNext(); ) {
                ExploreTabVideoModel model = iterator.next();
                if (model.getTag() == 997) {
                    iterator.remove();
                }
            }
        }
*/
    }

    @Override
    public int getCount() {
        if (Values.carType == 11 || Values.carType == 12 || Values.carType == 16) {
            if (video_list != null && video_list.size() > 0) {
                if (video_list.get(video_list.size() - 1).getTag() == 997)
                    return video_list.size() - 1;
            }
        }
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
        String img_name;
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

        Uri uri = Uri.parse(img_name);

        if (img_name.equalsIgnoreCase("")) {
            if (ExploreFragment.progress_bar != null) {
                ExploreFragment.progress_bar.setVisibility(View.INVISIBLE);
            }
        } else {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .build();
            viewHolder.imageView.setController(controller);
        }
        //viewHolder.imageView.setImageURI(uri);

        return convertView;
    }

    final ControllerListener<ImageInfo> listener = new BaseControllerListener<ImageInfo>() {

        @Override
        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
            //Action on final image load
            if (ExploreFragment.progress_bar != null) {
                ExploreFragment.progress_bar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            //Action on failure
        }

    };

    public void setList(ArrayList<ExploreTabVideoModel> videoList) {

        this.video_list = videoList;

    }

    static class ViewHolder {
        final SimpleDraweeView imageView;
        final TextView txtViewTitle;

        public ViewHolder(View view) {
            imageView = view.findViewById(R.id.img_view);
            txtViewTitle = view.findViewById(R.id.txt_video_title);
        }
    }

}
