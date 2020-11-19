package com.nissan.alldriverguide.fragments.search;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.WebDataAdapter;
import com.nissan.alldriverguide.model.WebContent;

import java.util.List;

public class WebListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private WebDataAdapter adapter;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_web);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WebDataAdapter(this, data);
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
