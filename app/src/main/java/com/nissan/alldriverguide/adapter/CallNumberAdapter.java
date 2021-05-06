package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.model.CallInfo;

import java.util.List;

/**
 * Created by raman on 1/19/17.
 */
public class CallNumberAdapter extends BaseAdapter {

    private final Context context;
    private LayoutInflater inflater;
    private final List<CallInfo> list;

    public CallNumberAdapter(Context context, List<CallInfo> list) {
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
            convertView = inflater.inflate(R.layout.call_number_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageURI(list.get(position).getCountryFlag());
        viewHolder.txtViewTitle.setText(list.get(position).getCountryName());

        return convertView;
    }

    static class ViewHolder {
        final SimpleDraweeView imageView;
        final TextView txtViewTitle;
        final CheckBox checkBox;

        public ViewHolder(View view) {
            imageView = view.findViewById(R.id.img_view);
            txtViewTitle = view.findViewById(R.id.txt_title);
            checkBox = view.findViewById(R.id.check_box);
        }
    }

}
