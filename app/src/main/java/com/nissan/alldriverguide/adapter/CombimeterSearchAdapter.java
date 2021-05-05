package com.nissan.alldriverguide.adapter;

/*
  Created by shubha on 11/16/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.fragments.assistance.DetailsFragment;
import com.nissan.alldriverguide.fragments.search.TabFragment;
import com.nissan.alldriverguide.fragments.search.model.SearchCombimeterModel;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


public class CombimeterSearchAdapter extends RecyclerView.Adapter<CombimeterSearchAdapter.SimpleViewHolder> {

    private final Context mContext;
    private final ArrayList<SearchCombimeterModel> warning_list;

    /**
     * Declare constructor
     *
     * @param mContext need context
     * @param list     need data list
     */
    public CombimeterSearchAdapter(Context mContext, ArrayList<SearchCombimeterModel> list) {
        this.mContext = mContext;
        this.warning_list = list;
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.all_search_item, parent, false);
        SimpleViewHolder s = new SimpleViewHolder(view);
        s.setIsRecyclable(false);
        return s;
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {

        if (holder instanceof SimpleViewHolder) {
            holder.rv.setBackgroundColor(Color.BLACK);
            String path = warning_list.get(position).getImagePath(); //warningLight(epubInfo);

            Drawable d = Drawable.createFromPath(Values.car_path + "/combimeter_button" + "/" + path);
            holder.itemSearch.setImageDrawable(d);

            String[] tag = path.split("_");

            holder.itemSearch.setTag(Integer.parseInt(tag[1]));

            holder.itemSearch.setVisibility(View.VISIBLE);
            holder.qRefGuide.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return warning_list == null ? 0 : warning_list.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemSearch;
        TextView qRefGuide;
        RelativeLayout rv;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            itemSearch = itemView.findViewById(R.id.search_item);
            qRefGuide = itemView.findViewById(R.id.quick_ref_guide);
            rv = itemView.findViewById(R.id.combimeter_item);
            itemSearch.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.search_item:
                    // here set the epub type
                    Values.ePubType = Values.COMBIMETER_TYPE;
                    int ePubIndex = 0;
                    /*if(Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
                        ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;
                    } else {
                        ePubIndex = Integer.parseInt(v.getTag().toString());
                    }*/

                    ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;

                    // here redirect the fragment to display details data
                    FragmentTransaction fragmentTransaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                    fragmentTransaction.replace(R.id.container, DetailsFragment.newInstance(ePubIndex - 1, TabFragment.getTitleName(Values.COMBIMETER_TYPE)));
                    fragmentTransaction.addToBackStack(Values.tabSearch);
                    fragmentTransaction.commit();
                    break;

                default:
                    break;

            }
        }
    }

}