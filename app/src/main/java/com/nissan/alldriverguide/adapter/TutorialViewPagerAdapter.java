package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nissan.alldriverguide.R;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class TutorialViewPagerAdapter extends PagerAdapter {

    private final Context context;
    private final int[] imageId;
    private final String[] tutorialTitles;
    private final String[] tutorialDetails;

    /**
     * Declare constructor
     *
     * @param context
     * @param imageId         for view pager background image
     * @param tutorialTitles  display in viewpager as a title text
     * @param tutorialDetails display in viewpager as a details text
     */
    public TutorialViewPagerAdapter(Context context, int[] imageId, String[] tutorialTitles, String[] tutorialDetails) {
        this.context = context;
        this.imageId = imageId;
        this.tutorialTitles = tutorialTitles;
        this.tutorialDetails = tutorialDetails;
    }

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        ImageView imageThumbnail;
        TextView title;
        TextView detail;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        imageThumbnail = itemView.findViewById(R.id.imageThumbnail);
        imageThumbnail.setImageResource(imageId[position]);

        title = itemView.findViewById(R.id.txt_title);
        title.setText(tutorialTitles[position]);

        detail = itemView.findViewById(R.id.txt_details);
        detail.setText(tutorialDetails[position].replace("\\n", "\n"));

        container.addView(itemView);
        return itemView;
    }
}
