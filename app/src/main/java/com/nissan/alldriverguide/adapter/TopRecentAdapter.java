package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.fragments.search.TabFragment;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by nirob on 10/9/17.
 */

public class TopRecentAdapter extends RecyclerView.Adapter<TopRecentAdapter.SimpleViewHolder> {

    private final Context mContext;
    private final List<String> list2;
    private final List<String> list3;

    /**
     * Declare the constructor
     *
     * @param mContext needed
     * @param list2    need to a collection list
     */
//    public TopRecentAdapter(Context mContext, List<SearchModel> list1, List<SearchModel> list2) {
    public TopRecentAdapter(Context mContext, List<String> list2) {
        this.mContext = mContext;
        this.list2 = list2;
        list3 = new ArrayList<>();
        list3.addAll(list2);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.top_recent_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.keyword.setTag("" + position);
        holder.keyword.setText(list3.get(position));
    }

    @Override
    public int getItemCount() {
        return list3 == null ? 0 : list3.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView keyword;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            keyword = itemView.findViewById(R.id.top_recent_search_keyword);
            keyword.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction fragmentTransaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);

            int index = getAdapterPosition();
            String keyword;
            try {
                index = Integer.parseInt(v.getTag().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            /*if (index < list1.size()) {
                keyword = list1.get(index).getSearchtag();
            } else {
                keyword = list2.get(index - list1.size()).getSearchtag();
            }*/

            keyword = list2.get(index);

            Values.keyWord = keyword;
            new NissanApp().replaceFragment(fragmentTransaction, R.id.container, TabFragment.newInstance(keyword), Values.tabSearch);
        }
    }
}
