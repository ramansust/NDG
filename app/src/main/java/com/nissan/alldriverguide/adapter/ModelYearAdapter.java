package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.interfaces.ModelYearItemClcikListener;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ModelYearAdapter extends RecyclerView.Adapter<ModelYearAdapter.ModelYearViewHolder> {
    private final Context context;
    public ArrayList<Object> list;
    ModelYearItemClcikListener itemClcikListener;

    public ModelYearAdapter(Context context, ArrayList<Object> list, ModelYearItemClcikListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClcikListener = itemClickListener;
    }

    public ModelYearAdapter(Context context, ArrayList<Object> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ModelYearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_download_model_year_row, parent, false);
        return new ModelYearViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ModelYearViewHolder holder, final int position) {
        if (list.get(position).getClass() == CarInfo.class) {
            final CarInfo carInfo = (CarInfo) list.get(position);
            holder.txt_title.setText(carInfo.getName());
            if (Values.ALREADY_DOWNLOADED.equals(carInfo.getStatus()))
                holder.txt_title.setAlpha(0.5f);
            holder.itemView.setOnClickListener(v -> {
                if (Values.ALREADY_DOWNLOADED.equals(carInfo.getStatus()))
                    return;
                itemClcikListener.onItemClicked(holder, carInfo, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Object> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class ModelYearViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title;
        //SimpleDraweeView imageView;

        public ModelYearViewHolder(View view) {
            super(view);
            // initialized list view item id
            //RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_car_download);
            //imageView = (SimpleDraweeView) view.findViewById(R.id.ivMainCarImage);
            //ImageView imageViewBorder = (ImageView) view.findViewById(R.id.img_view_border);
            //TextView txtViewTitle = (TextView) view.findViewById(R.id.txt_title);
            txt_title = view.findViewById(R.id.txtView_car_downlaod_model_year_row_title);
            //ImageButton imgDeleteOrDownload = (ImageButton) view.findViewById(R.id.img_btn_delete_or_download);
            //imgDeleteOrDownload.setBackgroundResource(R.drawable.delete_selector);
        }
    }
}
