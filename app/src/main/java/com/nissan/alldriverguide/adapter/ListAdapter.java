package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.utils.Values;

import java.util.List;

/**
 * Created by raman on 1/19/17.
 */
public class ListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<EpubInfo> list;

    /**
     * Declare Constructor
     *
     * @param context needed
     * @param list    EpubInfo type data list
     */
    public ListAdapter(Context context, List<EpubInfo> list) {
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
            convertView = inflater.inflate(R.layout.list_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Here set the DEMO text color for 2 & 6 number epub for Tyre Information
        if (Values.ePubType == Values.TYRE_TYPE) {
            if (position == 2 || position == 6) {
                String str = list.get(position).getTitle().toString().toUpperCase(); // output:eg 2.1. CHANGING FLAT TYRE (DEMO)
                viewHolder.txtViewTitle.setTextColor(Color.parseColor("#C3002F")); // set the text color
                String c = "(";
                int p = str.indexOf(c); // you can find the c in str in 24 index
                if (p <= 0) {
                    p = 0;
                }
                String s = " ";
                if (p > 0) {
                    s = str.substring(0, p - 1); // here subtract (DEMO) from str, output: 2.1. CHANGING FLAT TYRE
                }
                SpannableString spanString1 = new SpannableString(s + " ");
                spanString1.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString1.length(), 0); // set bold for this text: 2.1. CHANGING FLAT TYRE
                SpannableString spanString = new SpannableString(str.substring(p, str.length()));
                spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0); // set underline for (DEMO)
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0); // set BOLD for (DEMO)
                viewHolder.txtViewTitle.setText(TextUtils.concat(spanString1, spanString)); // finally set the SpannableString in textView
            } else {
                viewHolder.txtViewTitle.setText(list.get(position).getTitle().toUpperCase());
            }

        } else {
            viewHolder.txtViewTitle.setText((position + 1) + ". " + list.get(position).getTitle().toUpperCase());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtViewTitle;

        public ViewHolder(View view) {
            txtViewTitle = (TextView) view.findViewById(R.id.txt_title);
        }
    }

}
