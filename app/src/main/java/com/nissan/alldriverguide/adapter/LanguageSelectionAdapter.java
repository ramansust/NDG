package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.model.LanguageInfo;

import java.util.List;

/**
 * Created by raman on 1/19/17.
 */
public class LanguageSelectionAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<LanguageInfo> list;
    private boolean isColor;
    private Typeface tf;

    public LanguageSelectionAdapter (Context context, List<LanguageInfo> list, boolean isColor) {
        this.context = context;
        this.list = list;
        this.isColor = isColor;
        this.tf = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Regular.otf");
        inflater = LayoutInflater.from (this.context);
    }

    @Override
    public int getCount () {
        return list != null ? list.size () : 0;
    }

    @Override
    public Object getItem (int position) {
        return list.get (position);
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
            convertView = inflater.inflate (R.layout.language_selection_row, parent, false);
            viewHolder = new ViewHolder (convertView);
            convertView.setTag (viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag ();
        }

        viewHolder.imageView.setBackgroundResource(list.get(position).getImage());

        viewHolder.txtViewTitle.setTypeface(tf);
        viewHolder.txtViewTitle.setText(list.get(position).getName());

        if(isColor) {
            viewHolder.txtViewTitle.setTextColor(Color.BLACK);
            if(list.get(position).isSelected()) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
                viewHolder.checkBox.setButtonDrawable(R.drawable.tick_icon);
            } else {
                viewHolder.checkBox.setVisibility(View.GONE);
            }
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView txtViewTitle;
        CheckBox checkBox;

        public ViewHolder (View view) {
            imageView = (ImageView) view.findViewById(R.id.img_view);
            txtViewTitle = (TextView) view.findViewById (R.id.txt_title);
            checkBox = (CheckBox) view.findViewById(R.id.check_box);
        }
    }

}
