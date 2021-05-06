package com.nissan.alldriverguide.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datasoft.downloadManager.epubUtils.EpubInfo;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.fragments.assistance.DetailsFragment;
import com.nissan.alldriverguide.fragments.search.TabFragment;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 * This class not in used anymore
 */

public class DataGridAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<EpubInfo> list;
    private final int _ePubType;

    public DataGridAdapter(Context mContext, ArrayList<EpubInfo> list, int ePubType) {
        this.mContext = mContext;
        this.list = list;
        this._ePubType = ePubType;
    }

    public void setData(ArrayList<EpubInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.all_search_item, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String finalText;
        int numbering = position + 1;


        EpubInfo epubInfo = list.get(position);

        viewHolder.rv.setVisibility(View.GONE);

//        finalText = StringHelper.convertFromUTF8(epubInfo.getTitle());
        finalText = epubInfo.getTitle();

        if (_ePubType == Values.TYRE_TYPE) {
            if (!finalText.isEmpty()) {
                finalText = finalText.substring(finalText.indexOf(" ") + 1);
            }
        }

        viewHolder.qRefGuide.setText(numbering + ". " + finalText);

        convertView.setOnClickListener(view -> {

            EpubInfo epubInfo1 = list.get(position);

            Values.ePubType = _ePubType; //getEpubValues.TYRE_TYPE;  //epubModel.getEpubtype();
//                epubInfo.setTitle(TabFragment.getTitleName(_ePubType));
            Fragment frag = DetailsFragment.newInstance((epubInfo1.getIndex() * 2 + 1), TabFragment.getTitleName(_ePubType));
            FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            ft.replace(R.id.container, frag);
            ft.addToBackStack(Values.tabSearch);
            ft.commit();
        });

        return convertView;
    }

    static class ViewHolder {
        final ImageView itemSearch;
        final TextView qRefGuide;
        final RelativeLayout rv;

        public ViewHolder(View view) {
            itemSearch = view.findViewById(R.id.search_item);
            qRefGuide = view.findViewById(R.id.quick_ref_guide);
            rv = view.findViewById(R.id.combimeter_item);
        }
    }
}