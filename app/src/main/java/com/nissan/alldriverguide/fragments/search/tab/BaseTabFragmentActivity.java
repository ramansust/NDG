package com.nissan.alldriverguide.fragments.search.tab;

import android.os.Bundle;

import com.nissan.alldriverguide.fragments.search.TabFragment;

public class BaseTabFragmentActivity extends SearchActivity implements DataPassing {

    protected TabFragment tabFragment;
    private int contentView;
    public static String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView);
    }

    // this method is used to TabFragment
    public void setMedia(TabFragment media) {
        this.tabFragment = media;
    }

    public void setAllFragment(AllFragment allFragment) {
    }

    public void setWarningLightFragment(WarningLightFragment warningLightFragment) {
    }

    // this view set in MainActivity inside onCreate
    public void setView(int view) {
        this.contentView = view;
    }

    @Override
    public void onDataPass(String data) {
        keyword = data; // getting search keyword by interface
    }
}
