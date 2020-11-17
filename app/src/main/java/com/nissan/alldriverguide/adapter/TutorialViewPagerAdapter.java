package com.nissan.alldriverguide.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nissan.alldriverguide.R;

public class TutorialViewPagerAdapter extends PagerAdapter {

    private Context context;
    private int[] imageId;
    private String[] tutorialTitles;
    private String[] tutorialDetails;
    private LayoutInflater inflater;
    private Typeface typeFaceBold, typeFaceNormal;

    /**
     * Declare constructor
     * @param context
     * @param imageId for view pager background image
     * @param tutorialTitles display in viewpager as a title text
     * @param tutorialDetails display in viewpager as a details text
     */
    public TutorialViewPagerAdapter(Context context, int[] imageId, String[] tutorialTitles, String[] tutorialDetails) {
        this.context = context;
        this.imageId = imageId;
        this.tutorialTitles = tutorialTitles;
        this.tutorialDetails = tutorialDetails;
        typeFaceNormal = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Regular.otf");
        typeFaceBold = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Bold.otf");
    }

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageThumbnail;
        TextView title;
        TextView detail;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        imageThumbnail = (ImageView) itemView.findViewById(R.id.imageThumbnail);
        imageThumbnail.setImageResource(imageId[position]);

        title = (TextView) itemView.findViewById(R.id.txt_title);
        title.setText(tutorialTitles[position]);
        title.setTypeface(typeFaceBold);

        detail = (TextView) itemView.findViewById(R.id.txt_details);
        detail.setText(tutorialDetails[position].replace("\\n", "\n"));
        detail.setTypeface(typeFaceNormal);

        container.addView(itemView);
        return itemView;
    }
}
