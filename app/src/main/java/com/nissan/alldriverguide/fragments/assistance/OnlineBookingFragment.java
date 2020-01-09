package com.nissan.alldriverguide.fragments.assistance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;

public class OnlineBookingFragment extends Fragment {

    private WebView webView;
    private LinearLayout linearBack;
    private static final String TITLE = "title";
    private static final String IMG_URL = "img_url";
    private View view;

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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl(IMG_URL);
        webView.requestFocus();
    }

    private void setListener() {
        linearBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });
    }

    private void initView() {
        webView = (WebView) view.findViewById(R.id.webView_online_booking);
        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
