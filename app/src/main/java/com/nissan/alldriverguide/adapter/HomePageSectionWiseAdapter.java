package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;
import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.model.HomePageEpubInfo;
import com.nissan.alldriverguide.model.HomePageSectionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sourav
 * @Since 09/03/17.
 * This class not in used anymore
 */
public class HomePageSectionWiseAdapter extends SectionedRecyclerViewAdapter<HomePageSectionWiseAdapter.MainVH> {
    private Context context;
    private List<HomePageSectionInfo> allData;

    public interface OnItemClickListener {
        void onClick(View view, int index);
    }

    private OnItemClickListener listener;

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public HomePageSectionWiseAdapter(Context context, List<HomePageEpubInfo> data) {
        this.context = context;
        this.allData = loadData(data);
    }

    @Override
    public int getSectionCount() {
        return allData.size(); // number of sections, you would probably base this on a data set such as a map
    }

    @Override
    public int getItemCount(int sectionIndex) {
        return allData.get(sectionIndex).getEpublist().size();
    }

    @Override
    public void onBindHeaderViewHolder(MainVH holder, int section, boolean expanded) {
        // Setup header view
        String sectionName = allData.get(section).getSectionTitle();
        HomePageSectionWiseAdapter.MainVH sectionViewHolder = (HomePageSectionWiseAdapter.MainVH) holder;
        sectionViewHolder.sectionTitle.setText(sectionName);

        sectionViewHolder.sectionTitle.setBackgroundColor(Color.parseColor("" + allData.get(section).getColorCode().trim()));
    }

    @Override
    public void onBindViewHolder(MainVH holder, int section, int relativePosition, int absolutePosition) {
        // Setup non-header view.
        // 'section' is section index.
        // 'relativePosition' is index in this section.
        // 'absolutePosition' is index out of all items, including headers and footers.
        // See sample project for a visual of how these indices work.

        final EpubInfo epubInfo = allData.get(section).getEpublist().get(relativePosition);

        HomePageSectionWiseAdapter.MainVH viewHolder = (HomePageSectionWiseAdapter.MainVH) holder;

        if(allData.get(section).getEpublist().size() - 1 == relativePosition) {
            viewHolder.imageViewDivider.setVisibility(View.GONE);
        } else {
            viewHolder.imageViewDivider.setVisibility(View.VISIBLE);
        }

        viewHolder.txtViewTitle.setText((relativePosition + 1) + ". " + epubInfo.getTitle());
        viewHolder.bindDataWithViewHolder(epubInfo, epubInfo.getIndex());
    }

    @Override
    public void onBindFooterViewHolder(MainVH holder, int section) {
        // Setup footer view, if footers are enabled (see the next section)
    }

    @Override
    public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
        // Change inflated layout based on type
        int layoutRes;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                layoutRes = R.layout.list_section_row;
                break;
            default:
                layoutRes = R.layout.homepage_row;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);

        return new MainVH(v, listener);
    }

    public class MainVH extends SectionedViewHolder {
        final TextView sectionTitle;
        TextView txtViewTitle;

        ImageView imageViewDivider;

        EpubInfo epubInfo;
        int index;

        public MainVH(final View itemView, final OnItemClickListener listener) {
            super(itemView);
            // Setup view holder. You'd want some views to be optional, e.g. the
            // header/footer will have views that normal item views do or do not have.
            sectionTitle = (TextView) itemView.findViewById(R.id.txt_title_section);
            txtViewTitle = (TextView) itemView.findViewById(R.id.txt_title);

            imageViewDivider = (ImageView) itemView.findViewById(R.id.img_view_divider);

            if(itemView.getId() == R.id.relative_main) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null && itemView != null) {
                            listener.onClick(itemView, index);
                        }
                    }
                });
            }
        }

        public void bindDataWithViewHolder(EpubInfo epubInfo, int currentPosition) {
            this.epubInfo = epubInfo;
            this.index = currentPosition;
        }
    }

    private ArrayList<HomePageSectionInfo> loadData(List<HomePageEpubInfo> gameList) {
        ArrayList<HomePageSectionInfo> list = new ArrayList<>();
        if (gameList != null && gameList.size() > 0) {
            String title = "";
            HomePageSectionInfo sectionInfo = new HomePageSectionInfo();
            ArrayList<HomePageEpubInfo> itemList = new ArrayList<>();

            for (int i = 0; i < gameList.size(); i++) {
                HomePageEpubInfo info = gameList.get(i);

                if (title.equalsIgnoreCase("")) {
                    title = info.getSectionTitle();
                    sectionInfo.setSectionTitle(info.getSectionTitle());
                    sectionInfo.setColorCode(info.getColorCode());
                }

                if (title.equalsIgnoreCase(info.getSectionTitle())) {
                    itemList.add(info);
                    if(i == gameList.size() - 1) {
                        sectionInfo.setEpublist(itemList);
                        list.add(sectionInfo);
                    }
                } else {
                    sectionInfo.setEpublist(itemList);
                    list.add(sectionInfo);

                    title = info.getSectionTitle();

                    sectionInfo = new HomePageSectionInfo();
                    itemList = new ArrayList<>();
                    sectionInfo.setSectionTitle(info.getSectionTitle());
                    sectionInfo.setColorCode(info.getColorCode());
                    itemList.add(info);
                    sectionInfo.setEpublist(itemList);

                    if(i == gameList.size() - 1) {
                        sectionInfo.setEpublist(itemList);
                        list.add(sectionInfo);
                    }
                }
            }
        }

        return list;
    }
}
