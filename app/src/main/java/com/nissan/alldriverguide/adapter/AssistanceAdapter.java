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
public class AssistanceAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] carName;
    private int[] carImage;
    private Typeface tf;

    public AssistanceAdapter(Context context, String[] carName, int[] carImage) {
        this.context = context;
        this.carName = carName;
        this.carImage = carImage;
        this.tf = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Bold.otf");
        inflater = LayoutInflater.from (this.context);
    }

    @Override
    public int getCount () {
        return carName != null ? carName.length : 0;
    }

    @Override
    public Object getItem (int position) {
        return carName[position];
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
            convertView = inflater.inflate (R.layout.assistance_row, parent, false);
            viewHolder = new ViewHolder (convertView);
            convertView.setTag (viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag ();
        }

        viewHolder.imageView.setBackgroundResource(carImage[position]);
        viewHolder.txtViewTitle.setTypeface(tf);
        viewHolder.txtViewTitle.setText(carName[position]);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView txtViewTitle;
        TextView txtViewSubTitle;

        public ViewHolder (View view) {
            imageView = (ImageView) view.findViewById(R.id.img_view);
            txtViewTitle = (TextView) view.findViewById (R.id.txt_title);
            txtViewSubTitle = (TextView) view.findViewById (R.id.txt_subtitle);
        }
    }

}
