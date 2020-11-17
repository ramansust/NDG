package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nissan.alldriverguide.R;

/**
 * Created by raman on 1/19/17.
 */
public class AssistanceAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] carName;
    private int[] carImage;

    /**
     * Declare constructor that uses for
     * NissanAssistanceFragment.java
     * and AssistanceFragment.java
     *
     * @param context  application context
     * @param carName  string array
     * @param carImage int array
     */
    public AssistanceAdapter(Context context, String[] carName, int[] carImage) {
        this.context = context;
        this.carName = carName;
        this.carImage = carImage;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return carName != null ? carName.length > 6 ? 6 : carName.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return carName[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.assistance_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // imageView Logic for if left icon img in fixed size
        viewHolder.imageView.setBackgroundResource(position < carImage.length ? carImage[position] : R.drawable.tyre); // here set the car image
        viewHolder.txtViewTitle.setText(carName[position]);

        return convertView;
    }

    public void setList(String[] setting_names, int[] assistanceImage) {
        this.carName = setting_names;
        this.carImage = assistanceImage;
    }

    /**
     * this class for list item view initialization
     */
    static class ViewHolder {
        ImageView imageView;
        TextView txtViewTitle;
        TextView txtViewSubTitle;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.img_view);
            txtViewTitle = (TextView) view.findViewById(R.id.txt_title);
            txtViewSubTitle = (TextView) view.findViewById(R.id.txt_subtitle);
        }
    }
}
