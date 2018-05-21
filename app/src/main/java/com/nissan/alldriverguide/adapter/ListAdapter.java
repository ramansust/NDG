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
    private Typeface tf;

    public ListAdapter(Context context, List<EpubInfo> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from (this.context);
        this.tf = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Bold.otf"); //initialize typeface here.
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
            convertView = inflater.inflate (R.layout.list_row, parent, false);
            viewHolder = new ViewHolder (convertView);
            convertView.setTag (viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag ();
        }

        if (Values.ePubType == Values.TYRE_TYPE) {
            if (position == 2 || position == 6) {
                String str = list.get(position).getTitle().toString().toUpperCase();
                viewHolder.txtViewTitle.setTextColor (Color.parseColor("#C3002F"));
                String c = "(";
                int p = str.indexOf(c);
                if(p <= 0) {
                    p = 0;
                }
                String s = " ";
                if(p > 0) {
                    s = str.substring(0, p - 1);
                }
                SpannableString spanString1 = new SpannableString(s + " ");
                spanString1.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString1.length(), 0);
                SpannableString spanString = new SpannableString(str.substring(p, str.length()));
                spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                viewHolder.txtViewTitle.setTypeface(tf);
                viewHolder.txtViewTitle.setText(TextUtils.concat(spanString1, spanString));
            } else {
                viewHolder.txtViewTitle.setTypeface(tf);
                viewHolder.txtViewTitle.setText (list.get(position).getTitle().toUpperCase());
            }

        } else {
            viewHolder.txtViewTitle.setTypeface(tf);
            viewHolder.txtViewTitle.setText((position + 1) + ". " + list.get(position).getTitle().toUpperCase());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtViewTitle;

        public ViewHolder (View view) {
            txtViewTitle = (TextView) view.findViewById (R.id.txt_title);
        }
    }

}
