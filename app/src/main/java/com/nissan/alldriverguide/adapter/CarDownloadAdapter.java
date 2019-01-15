package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;

/**
 * Created by raman on 1/19/17.
 */
public class CarDownloadAdapter extends BaseAdapter {

    public interface OnItemClickListener {
        void onItemClick(int carId, int pos);
    }

    private final OnItemClickListener listener;

    private Context context;
    private LayoutInflater inflater;
    public ArrayList<Object> list;
    private CarInfo info = null;
    private Typeface typeFaceBold;
    TextView txtView_loading;
    SimpleDraweeView imageView;

    /**
     * Constructor
     *
     * @param context
     * @param list
     */
    public CarDownloadAdapter(Context context, ArrayList<Object> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(this.context);
        this.listener = onItemClickListener;
        typeFaceBold = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Bold.otf");
    }

    public void setList(ArrayList<Object> _list) {
        this.list = _list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // if ArrayList is null then getCount size will be 0; else return ArrayList size;
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (list.get(position).getClass() == CarInfo.class) {
            info = (CarInfo) list.get(position);
            // inflate list view item layout
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.car_download_row, parent, false);

            // initialized list view item id
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_car_download);
            imageView = (SimpleDraweeView) view.findViewById(R.id.ivMainCarImage);
            ImageView imageViewBorder = (ImageView) view.findViewById(R.id.img_view_border);
            TextView txtViewTitle = (TextView) view.findViewById(R.id.txt_title);
            txtView_loading = (TextView) view.findViewById(R.id.txtView_loading);
            ImageButton imgDeleteOrDownload = (ImageButton) view.findViewById(R.id.img_btn_delete_or_download);
            imgDeleteOrDownload.setBackgroundResource(R.drawable.delete_selector);

            txtViewTitle.setTypeface(typeFaceBold);

            imgDeleteOrDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(info.getId(), position);
                }
            });

            if (!((CarInfo) list.get(position)).getStatus().equals("1")){
                imgDeleteOrDownload.setVisibility(View.INVISIBLE);
            }

            // here compare the status for previous section
            if (Values.PREVIOUS_CAR.equalsIgnoreCase(info.getStatus())) {
                // set the car images for previous section
                imageView.setImageURI(info.getCarImg());
                // set the previous section background colour
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                // here actually display first car name for 4 cars that belong in previous section.
                if (info.getId() == 1 || info.getId() == 2 || info.getId() == 4 || info.getId() == 5) {
                    String name[] = info.getName().split(" ");
                    txtViewTitle.setText(name[0]);
                } else { // this for display full name for all car without 4 cars on top
                    txtViewTitle.setText(info.getName());
                }
                // here compare the status for already download section
            } else if (Values.ALREADY_DOWNLOADED.equalsIgnoreCase(info.getStatus())) {
                // set the car images for already downloaded section
//                NissanApp.getInstance().setCarImage(info.getId(), imageView);
                imageView.setImageURI(info.getCarImg());
                // set the already downloaded section background colour
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.orange));
                // set the car name for already downloaded car
                txtViewTitle.setText(info.getName());

            } else { // this section for available download
                // set the car images for available downloaded section
                imageView.setImageURI(info.getCarImg());
                // set the available downloaded section background colour
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                // set the car name for available downloaded car
                txtViewTitle.setText(info.getName());
                if(info.getId() == 13 || info.getId() == 15){
                    //Set fixed name for both this car == NEW X-TRAIL  (EUR/RUS)
                    String name[] = info.getName().split(" ");
//                    txtViewTitle.setText(info.getName() == null || info.getName().isEmpty() ? "NEW NISSAN X-TRAIL" : name[1] + " " + name[2]);
                    txtViewTitle.setText("NISSAN X-TRAIL");
                }
                if (info.getId() == 12 || info.getId() == 16) {
                    txtViewTitle.setText("NEW NISSAN QASHQAI");
                }
            }

            // this item layout is actually used in CarDownloadSettingsAdapter.java
            // so here these two id need to invisible
            imageViewBorder.setVisibility(View.INVISIBLE);

        } else {
            // here inflate the item header like (available for download, previous model)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.car_download_row_title, parent, false);

            view.setOnClickListener(null); // for section header
            view.setClickable(false); // for section header

            // this layout determine for set section wise header color
            LinearLayout relativeLayout = (LinearLayout) view.findViewById(R.id.relative_section);

            TextView txtViewSection = (TextView) view.findViewById(R.id.txt_title);
            // here compare the string from list that load in CarDownloadActivity.java class
            // in LoadDataBase Async class in onPostExecute method
            if (context.getResources().getString(R.string.downloaded_car).equalsIgnoreCase(list.get(position).toString())) {
                // here set the background color when car is downloaded
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.orange));
                // here set the downloaded car section header image
                txtViewSection.setBackgroundResource(R.drawable.downloaded_car);
            } else if (context.getResources().getString(R.string.previous_car).equalsIgnoreCase(list.get(position).toString())) {
                // here set the background color when car in previous section
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                // here set the previous model section header image
                txtViewSection.setBackgroundResource(R.drawable.previous_model);
            } else {
                // here set the background color when car in available for download section
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                // here set the available for download section header image
                txtViewSection.setBackgroundResource(R.drawable.available_for_downlaod);
            }
        }

        return view;
    }

}
