package com.nissan.alldriverguide.adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.fragments.assistance.DetailsFragment;
import com.nissan.alldriverguide.fragments.search.TabFragment;
import com.nissan.alldriverguide.utils.StringHelper;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;

/**
 * This class not in used anymore
 */

public class DataGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<EpubInfo> list;
    private String drawable_folder = Values.car_path + "/combimeter_button";
    private int _ePubType = 0;
    private Typeface tf;

    public DataGridAdapter(Context mContext, ArrayList<EpubInfo> list, int ePubType) {
        this.mContext = mContext;
        this.list = list;
        this._ePubType = ePubType;
        tf = mContext.getResources().getFont(R.font.nissan_brand_bold);
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

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.all_search_item, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String finalText = "";
        int numbering = position + 1;


        EpubInfo epubInfo = list.get(position);

        viewHolder.rv.setVisibility(View.GONE);
        viewHolder.qRefGuide.setTypeface(tf);

        finalText = StringHelper.convertFromUTF8(epubInfo.getTitle());
//        finalText = epubInfo.getTitle();

        if (_ePubType == Values.TYRE_TYPE) {
            if (!finalText.isEmpty()) {
                finalText = finalText.substring(finalText.indexOf(" ") + 1, finalText.length());
            }
        }

        viewHolder.qRefGuide.setText(numbering + ". " + finalText);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EpubInfo epubInfo = list.get(position);

                Values.ePubType = _ePubType; //getEpubValues.TYRE_TYPE;  //epubModel.getEpubtype();
//                epubInfo.setTitle(TabFragment.getTitleName(_ePubType));
                Fragment frag = DetailsFragment.newInstance((epubInfo.getIndex() * 2 + 1), TabFragment.getTitleName(_ePubType));
                FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, frag);
                ft.addToBackStack(Values.tabSearch);
                ft.commit();
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView itemSearch;
        TextView qRefGuide;
        RelativeLayout rv;

        public ViewHolder(View view) {
            itemSearch = (ImageView) view.findViewById(R.id.search_item);
            qRefGuide = (TextView) view.findViewById(R.id.quick_ref_guide);
            rv = (RelativeLayout) view.findViewById(R.id.combimeter_item);
        }

    }

    // adding data in map for warning light
    private String warningLight(EpubInfo epubInfo) {
        File dir = new File(drawable_folder + "/");
        String[] wl = epubInfo.getHtmlLink().split("\\-");

        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                String[] name = file.getName().split("\\_");

                if (name[1].equalsIgnoreCase(wl[wl.length - 1])) {
//                    list.add(new CombimeterInfo(file.getName(), true));
                    return file.getName();
                }
            }
        }
        return "";
    }

}