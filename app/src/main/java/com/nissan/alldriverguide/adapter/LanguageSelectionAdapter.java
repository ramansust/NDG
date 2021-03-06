package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.model.LanguageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raman on 1/19/17.
 */
public class LanguageSelectionAdapter extends BaseAdapter {

    private final Context context;
    private LayoutInflater inflater;
    private List<LanguageInfo> list;
    private final boolean isColor;

    /**
     * Declare constructor
     *
     * @param context needed
     * @param list    data list
     * @param isColor for check visible/ invisible
     */
    public LanguageSelectionAdapter(Context context, List<LanguageInfo> list, boolean isColor) {
        this.context = context;
        this.list = list;
        this.isColor = isColor;
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
            convertView = inflater.inflate(R.layout.language_selection_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageURI(list.get(position).getImage());
        viewHolder.txtViewTitle.setText(list.get(position).getName());

        if (isColor) {
            viewHolder.txtViewTitle.setTextColor(Color.BLACK);
            // check the selection. if selected checkBox visible else invisible
            if (list.get(position).isSelected()) {
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

    public void setList(ArrayList<LanguageInfo> newList) {
        this.list = newList;
    }

    static class ViewHolder {
        final SimpleDraweeView imageView;
        final TextView txtViewTitle;
        final CheckBox checkBox;

        public ViewHolder(View view) {
            imageView = view.findViewById(R.id.img_view); // use fresco to load image
            txtViewTitle = view.findViewById(R.id.txt_title);
            checkBox = view.findViewById(R.id.check_box);
        }
    }

}
