package com.nissan.alldriverguide.fragments.search;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.WebDataAdapter;
import com.nissan.alldriverguide.model.WebContent;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WebListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<WebContent> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        data = getIntent().getParcelableArrayListExtra("content");

        initializedAll();

        initToolbar();
    }

    private void initializedAll() {
        toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.rv_web);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        WebDataAdapter adapter = new WebDataAdapter(this, data);
        recyclerView.setAdapter(adapter);
    }

    private void initToolbar() {
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            setTitle("Web URL list");
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

}
