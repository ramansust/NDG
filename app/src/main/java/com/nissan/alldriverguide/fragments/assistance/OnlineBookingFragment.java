package com.nissan.alldriverguide.fragments.assistance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nissan.alldriverguide.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OnlineBookingFragment extends Fragment {

    private WebView webView;
    private LinearLayout linearBack;
    private ImageButton btnBack;
    private TextView txtViewTitle;
    private static final String TITLE = "title";
    private static final String IMG_URL = "img_url";
    private View view;
    private ProgressBar progressBar;

    public static Fragment newInstance(String title, String imgUrl) {
        Fragment frag = new OnlineBookingFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(IMG_URL, imgUrl);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_online_booking, container, false);

        initView();
        setListener();
        webViewInstantiate();

        return view;
    }

    private void webViewInstantiate() {

        txtViewTitle.setText(getArguments().getString(TITLE));
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.clearCache(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl(getArguments().getString(IMG_URL));
        webView.requestFocus();
    }

    private void setListener() {
        linearBack.setOnClickListener(view -> getActivity().onBackPressed());
        btnBack.setOnClickListener(view -> getActivity().onBackPressed());
    }

    private void initView() {
        webView = view.findViewById(R.id.webView_online_booking);
        linearBack = view.findViewById(R.id.linear_back);
        btnBack = view.findViewById(R.id.btn_back);
        txtViewTitle = view.findViewById(R.id.txt_title);
        progressBar = view.findViewById(R.id.prog_online);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            /*view.loadUrl(url);
            return true;*/

            if (url.contains("tel:")) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
