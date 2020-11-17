package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.model.HomePageEpubInfo;
import com.nissan.alldriverguide.model.HomePageSectionInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mobioapp on 1/24/18.
 */

public class HomePageExpandableAdapter extends BaseExpandableListAdapter {
    // declare the interface that used for getting item position
    public interface OnItemClickListener {
        void onClick(int index);
    }

    private HomePageExpandableAdapter.OnItemClickListener listener;

    public void setOnClickListener(HomePageExpandableAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    private Context context;
    private List<HomePageSectionInfo> listHeader;
    private HashMap<String, List<EpubInfo>> childList;

    /**
     * Declare Constructor
     *
     * @param context needed
     * @param list    for data process
     */
    public HomePageExpandableAdapter(Context context, List<HomePageEpubInfo> list) {
        this.context = context;
        loadData(list);
    }

    /**
     * This method actually process the given data
     *
     * @param ePubList data that provided through constructor
     */
    private void loadData(List<HomePageEpubInfo> ePubList) {

        listHeader = new ArrayList<>();
        childList = new HashMap<>();

        if (ePubList != null && ePubList.size() > 0) {
            String title = "";
            HomePageSectionInfo sectionInfo = new HomePageSectionInfo();
            ArrayList<EpubInfo> itemList = new ArrayList<>();

            for (int i = 0; i < ePubList.size(); i++) { // looping for given arrayList
                HomePageEpubInfo info = ePubList.get(i);

                if ("".equals(title)) {
                    title = info.getSectionTitle();
                    sectionInfo.setSectionTitle(info.getSectionTitle());
                    sectionInfo.setColorCode(info.getColorCode());
                    sectionInfo.setColorCodeItem(info.getColorCodeItem());
                }

                if (title != null && info.getSectionTitle() != null && title.equalsIgnoreCase(info.getSectionTitle())) {
                    itemList.add(info);
                    if (i == ePubList.size() - 1) {
                        childList.put(title, itemList);
                        listHeader.add(sectionInfo);
                    }
                } else {
                    childList.put(title, itemList);
                    listHeader.add(sectionInfo);

                    title = info.getSectionTitle();

                    sectionInfo = new HomePageSectionInfo();
                    itemList = new ArrayList<>();
                    sectionInfo.setSectionTitle(info.getSectionTitle());
                    sectionInfo.setColorCode(info.getColorCode());
                    sectionInfo.setColorCodeItem(info.getColorCodeItem());
                    itemList.add(info);
                    childList.put(title, itemList);

                    if (i == ePubList.size() - 1) {
                        childList.put(title, itemList);
                        listHeader.add(sectionInfo);
                    }
                }
            }
        }
    }

    @Override
    public int getGroupCount() {
        return this.listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(listHeader.get(groupPosition).getSectionTitle().toString()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listHeader.get(groupPosition).toString();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(listHeader.get(groupPosition).getSectionTitle().toString()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_section_row, null);
        }

        ImageView imgView = (ImageView) convertView.findViewById(R.id.img_view);

        if (isExpanded) {
            imgView.setImageResource(R.drawable.ic_up_arrow);
        } else {
            imgView.setImageResource(R.drawable.ic_down_arrow);
        }

        TextView txtViewHeaderTitle = (TextView) convertView.findViewById(R.id.txt_title_section);
        if (groupPosition < listHeader.size()) {
            //index exists
            txtViewHeaderTitle.setText(listHeader.get(groupPosition).getSectionTitle() == null ? "" : listHeader.get(groupPosition).getSectionTitle());
        }


        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative_header);
        if (listHeader.get(groupPosition).getColorCode() != null && !"".equalsIgnoreCase(listHeader.get(groupPosition).getColorCode())) {
            String colorCode = "#000000";
            if (listHeader.get(groupPosition).getColorCode().startsWith("#")) {
                colorCode = listHeader.get(groupPosition).getColorCode().trim();
            }

            relativeLayout.setBackgroundColor(Color.parseColor(colorCode));
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.homepage_row, null);
        }

        final TextView txtViewTitle = (TextView) convertView.findViewById(R.id.txt_title);
        txtViewTitle.setText("" + childList.get(listHeader.get(groupPosition).getSectionTitle().toString()).get(childPosition).getTitle());

        ImageView imageViewDivider = (ImageView) convertView.findViewById(R.id.img_view_divider);

        if (childList.get(listHeader.get(groupPosition).getSectionTitle()).size() - 1 == childPosition) {
            imageViewDivider.setVisibility(View.INVISIBLE);
        } else {
            imageViewDivider.setVisibility(View.VISIBLE);
        }

        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative_main);
        if (listHeader.get(groupPosition).getColorCodeItem() != null && !"".equalsIgnoreCase(listHeader.get(groupPosition).getColorCodeItem())) {
            relativeLayout.setBackgroundColor(Color.parseColor("" + listHeader.get(groupPosition).getColorCodeItem().trim()));
        }


        txtViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && txtViewTitle != null) {
                    listener.onClick(childList.get(listHeader.get(groupPosition).getSectionTitle().toString()).get(childPosition).getIndex());
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
