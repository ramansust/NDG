package com.nissan.alldriverguide.fragments.search;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nissan.alldriverguide.R;

public class WebviewLoadActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private WebView webView;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_url);
        title = getIntent().getStringExtra("title");

        initToolbar();

        webView = (WebView) findViewById(R.id.web_view_url);
        webView.loadUrl(getIntent().getStringExtra("url"));
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar == null) {
            return;
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setTitle(title);
        }
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
