package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.fragments.search.WebviewLoadActivity;
import com.nissan.alldriverguide.model.WebContent;

import java.util.List;

/**
 * Created by nirob on 11/1/17.
 */

public class WebDataAdapter extends RecyclerView.Adapter<WebDataAdapter.SimpleViewHolder> {

    private Context mContext;
    private List<WebContent> list;

    public WebDataAdapter(Context mContext, List<WebContent> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.web_data_model, parent, false);
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.web_url);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (list.get(getAdapterPosition()).getTitle().toLowerCase().contains("demo")) {
                Toast.makeText(mContext, "What Msg showing not decided yet!", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(mContext, WebviewLoadActivity.class);
                i.putExtra("url", list.get(getAdapterPosition()).getFile());
                i.putExtra("title", list.get(getAdapterPosition()).getTitle());
                mContext.startActivity(i);
            }
        }
    }
}
