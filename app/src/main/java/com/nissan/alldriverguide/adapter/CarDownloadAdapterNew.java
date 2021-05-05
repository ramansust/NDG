/*
 * Copyright (c) 20/10/2019
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarDownloadAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String DOWNLOADED_CAR = "Downloaded Car";
    private final String AVAILABE_CAR = "Available Car";
    private final String PREVIOUS_CAR = "Previous Car";
    private OnItemClickListener listener;
    private final Context context;
    private final ArrayList<Object> orderedCarList = new ArrayList<>();
    private final List<CarList> carLists;
    private final Set<String> downloadedCarList = new HashSet<>();
    private final Set<Integer> previousCarList = new HashSet<>(Arrays.asList(1, 2, 4, 5, 7, 9));

    public CarDownloadAdapterNew(Context context, List<CarList> carLists) {
        this.context = context;
        this.carLists = carLists;
        getCarDownloadedlist();
        orderingCarList();
    }

    private void orderingCarList() {
        if (downloadedCarList != null && downloadedCarList.size() > 0) {

            orderedCarList.add(DOWNLOADED_CAR);
            for (String car : downloadedCarList) {
                for (CarList carList : carLists) {
                    if (carList.getCarUniqueName().equalsIgnoreCase(car)) {
                        orderedCarList.add(carList);
                    }
                }
            }
        }

        orderedCarList.add(AVAILABE_CAR);
        for (CarList carList : carLists) {
            if (Integer.valueOf(carList.getId()) == 15 || Integer.valueOf(carList.getId()) == 16)
                continue;
            if (!previousCarList.contains(Integer.valueOf(carList.getId())) && !downloadedCarList.contains(carList.getCarUniqueName()))
                orderedCarList.add(carList);
        }

        orderedCarList.add(PREVIOUS_CAR);
        for (CarList carList : carLists) {
            if (previousCarList.contains(Integer.valueOf(carList.getId()))) {
                if (Integer.valueOf(carList.getId()) == 2 || Integer.valueOf(carList.getId()) == 5)
                    continue;
                orderedCarList.add(carList);
            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ViewType.DEFAULT.ordinal()) {
            view = LayoutInflater.from(context).inflate(R.layout.car_download_row, parent, false);
            return new DefaultViewHolder(view);

        } else if (viewType == ViewType.DOWNLOADED.ordinal()) {
            view = LayoutInflater.from(context).inflate(R.layout.car_download_row, parent, false);
            return new DownloadedViewHolder(view);
        } else if (viewType == ViewType.HEADER.ordinal()) {
            view = LayoutInflater.from(context).inflate(R.layout.car_download_row_title, parent, false);
            return new HeaderViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == ViewType.HEADER.ordinal()) {
            ((HeaderViewHolder) holder).setDetails(orderedCarList.get(position));
        }
        if (getItemViewType(position) == ViewType.DOWNLOADED.ordinal()) {
            ((DownloadedViewHolder) holder).setDetails(orderedCarList.get(position), position);
        }
        if (getItemViewType(position) == ViewType.DEFAULT.ordinal()) {
            ((DefaultViewHolder) holder).setDetails(orderedCarList.get(position), position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (orderedCarList.get(position).getClass() == String.class)
            return ViewType.HEADER.ordinal();

        if (orderedCarList.get(position).getClass() == CarList.class) {
            CarList carList = (CarList) orderedCarList.get(position);
            if (downloadedCarList.contains(carList.getCarUniqueName().toLowerCase())) {
                return ViewType.DOWNLOADED.ordinal();
            }
        }
        return ViewType.DEFAULT.ordinal();
    }

    @Override
    public int getItemCount() {
        return orderedCarList.size();
    }

    private void getCarDownloadedlist() {
        File f = new File(Values.PATH + "/");
        File[] files = f.listFiles();
        if (files == null)
            return;
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                Log.e("LIST CAR DOWNLOADED: ", inFile.getName());
                downloadedCarList.add(inFile.getName().toLowerCase());
            }
        }
    }

    enum ViewType {
        HEADER, DOWNLOADED, DEFAULT
    }

    public interface OnItemClickListener {
        void onItemClick(int carId, int pos);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        LinearLayout relativeLayout;
        TextView txtViewSection;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative_section);
            txtViewSection = itemView.findViewById(R.id.txt_title);
        }

        public void setDetails(Object o) {
            if (o.getClass() == String.class) {
                String type = (String) o;
                if (type.equalsIgnoreCase(DOWNLOADED_CAR)) {
                    relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.orange));
                    txtViewSection.setBackgroundResource(R.drawable.downloaded_car);
                }
                if (type.equalsIgnoreCase(AVAILABE_CAR)) {
                    relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                    txtViewSection.setBackgroundResource(R.drawable.available_for_downlaod);
                }
                if (type.equalsIgnoreCase(PREVIOUS_CAR)) {
                    relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                    txtViewSection.setBackgroundResource(R.drawable.previous_model);
                }
            }
        }
    }

    public class DownloadedViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        SimpleDraweeView imageView;
        ImageView imageViewBorder;
        TextView txtViewTitle;
        TextView txtView_loading;
        ImageButton imgDeleteOrDownload;


        public DownloadedViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative_car_download);
            imageView = itemView.findViewById(R.id.ivMainCarImage);
            imageViewBorder = itemView.findViewById(R.id.img_view_border);
            txtViewTitle = itemView.findViewById(R.id.txt_title);
            txtView_loading = itemView.findViewById(R.id.txtView_loading);
            imgDeleteOrDownload = itemView.findViewById(R.id.img_btn_delete_or_download);

            imgDeleteOrDownload.setBackgroundResource(R.drawable.delete_selector);
            imageViewBorder.setVisibility(View.INVISIBLE);
        }

        public void setDetails(Object o, final int position) {

            final CarList carList;
            if (o.getClass() == CarList.class) {
                carList = (CarList) o;

                imageView.setImageURI(carList.getCarImg());
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.orange));
                txtViewTitle.setText(carList.getCarName());

                imgDeleteOrDownload.setOnClickListener(view -> listener.onItemClick(Integer.valueOf(carList.getId()), position));
            }

        }
    }

    public class DefaultViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        SimpleDraweeView imageView;
        ImageView imageViewBorder;
        TextView txtViewTitle;
        TextView txtView_loading;
        ImageButton imgDeleteOrDownload;


        public DefaultViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative_car_download);
            imageView = itemView.findViewById(R.id.ivMainCarImage);
            imageViewBorder = itemView.findViewById(R.id.img_view_border);
            txtViewTitle = itemView.findViewById(R.id.txt_title);
            txtView_loading = itemView.findViewById(R.id.txtView_loading);
            imgDeleteOrDownload = itemView.findViewById(R.id.img_btn_delete_or_download);

            imgDeleteOrDownload.setBackgroundResource(R.drawable.delete_selector);
            imgDeleteOrDownload.setVisibility(View.GONE);
        }

        public void setDetails(Object o, final int position) {

            final CarList carList;
            if (o.getClass() == CarList.class) {
                carList = (CarList) o;


                imageView.setImageURI(carList.getCarImg());
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                txtViewTitle.setText(carList.getCarName());

                int carID = Integer.valueOf(carList.getId());
                if (carID == 13 || carID == 15) {
                    txtViewTitle.setText("NISSAN X-TRAIL");
                }
                if (carID == 12 || carID == 16) {
                    txtViewTitle.setText("NISSAN QASHQAI");
                }
                if (carID == 1 || carID == 2) {
                    txtViewTitle.setText("QASHQAI");
                }
                if (carID == 4 || carID == 5) {
                    txtViewTitle.setText("X-TRAIL");
                }

            }

        }
    }
}
