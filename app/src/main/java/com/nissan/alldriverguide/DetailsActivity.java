package com.nissan.alldriverguide;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.datasoft.downloadManager.epubUtils.EpubInfo;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.nissan.alldriverguide.utils.NissanApp.getInstance;


public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private int index = 0;
    private String htmlContent = "";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details);

        String EPUB_INDEX = "epub_index";
        if (savedInstanceState == null) {
            Bundle args = getIntent().getExtras();
            index = Objects.requireNonNull(args).getInt(EPUB_INDEX);
        } else {
            index = savedInstanceState.getInt(EPUB_INDEX);
        }

        initViews();
        setListener();
        setupWebView();

        try {
            loadData();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * here set the web view for display epub
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.clearCache(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadData() throws IndexOutOfBoundsException {
        try {
            ((TextView) findViewById(R.id.txt_title)).setText(Objects.requireNonNull(getIntent().getExtras()).getString("epub_title"));
            ArrayList<EpubInfo> list;
            switch (Values.ePubType) {
                case Values.COMBIMETER_TYPE:

                    if (new File(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.COMBIMETER + Values.TOC_DIRECTORY).exists()) {
                        list = getInstance().parseePub(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.COMBIMETER);
                        //list = new MAePubParser(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.combimeter + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.epub).parseePub();

                        if (list != null) {
                            htmlContent = "file://"
                                    + getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang()
                                    + Values.COMBIMETER
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());
                        }

                        sendMsgToGoogleAnalytics(getAnalytics(Analytics.AUGMENTED_REALITY + Analytics.WARNING_LIGHT + Analytics.DOT + Objects.requireNonNull(list).get(index).getTitle()));
                    }
                    break;

                case Values.HOMEPAGE_TYPE:
                    if (new File(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
                        //list = new MAePubParser(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.homepage + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.epub).parseePub();
                        list = getInstance().parseePub(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);
                        if (list != null) {
                            htmlContent = "file://"
                                    + getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang()
                                    + Values.HOME_PAGE
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());
                            sendMsgToGoogleAnalytics(getAnalytics(Analytics.DOT + "video" + Analytics.HOMEPAGE + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.TYRE_TYPE:
                    if (new File(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.TYRE + Values.TOC_DIRECTORY).exists()) {
                        list = getInstance().parseePub(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.TYRE);
                        //list = new MAePubParser(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.tyre + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.epub).parseePub();

                        if (list != null) {
                            htmlContent = "file://"
                                    + getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang()
                                    + Values.TYRE
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());
                            sendMsgToGoogleAnalytics(getAnalytics(Analytics.TYRE + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.ENGINE_TYPE:
                    if (new File(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.ENGINE + Values.TOC_DIRECTORY).exists()) {
                        list = getInstance().parseePub(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.ENGINE);
                        //list = new MAePubParser(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.engine + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.epub).parseePub();

                        if (list != null) {
                            htmlContent = "file://"
                                    + getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang()
                                    + Values.ENGINE
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());
                            sendMsgToGoogleAnalytics(getAnalytics(Analytics.ENGINE + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.WARRANTY_TYPE:
                    if (new File(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.WARRANTY + Values.TOC_DIRECTORY).exists()) {
                        list = getInstance().parseePub(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.WARRANTY);
                        //list = new MAePubParser(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.warranty + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.epub).parseePub();

                        if (list != null) {
                            htmlContent = "file://"
                                    + getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang()
                                    + Values.WARRANTY
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());
                            sendMsgToGoogleAnalytics(getAnalytics(Analytics.WARRANTY + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.BUTTON_TYPE:

                    if (new File(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.BUTTON + Values.TOC_DIRECTORY).exists()) {
                        list = getInstance().parseePub(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.BUTTON);
                        //list = new MAePubParser(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.button + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.epub).parseePub();

                        if (list != null) {
                            htmlContent = "file://"
                                    + getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang()
                                    + Values.BUTTON
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());
                            sendMsgToGoogleAnalytics(getAnalytics(Analytics.AUGMENTED_REALITY + Analytics.DOT + getIntent().getStringExtra("ar_name") + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.INFO_TYPE:
                    if (new File(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.INFO + Values.TOC_DIRECTORY).exists()) {
                        list = getInstance().parseePub(getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.INFO);
                        //list = new MAePubParser(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.info + new PreferenceUtil(getApplicationContext()).getSelectedLang() + Values.epub).parseePub();

                        if (list != null) {
                            htmlContent = "file://"
                                    + getInstance().getCarPath(Values.carType) + getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getApplicationContext()).getSelectedLang()
                                    + Values.INFO
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());
                            sendMsgToGoogleAnalytics(getAnalytics(Analytics.INFO));
                        }
                    }
                    break;

                default:
                    break;
            }

            webView.loadUrl(htmlContent);
//            webView.loadUrl(list.get(index).getHtmlLink());
            webView.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * here set the click listener
     */
    private void setListener() {
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }


    /**
     * here initialized all global variable
     */
    private void initViews() {
        progressBar = findViewById(R.id.progressBarDetailsFragment);
        webView = findViewById(R.id.webViewDetailsFragment);
        btnBack = findViewById(R.id.btn_back);
        linearBack = findViewById(R.id.linear_back);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.linear_back:
                onBackPressed();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        // Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void sendMsgToGoogleAnalytics(String msgName) {
        // Get a Tracker (should auto-report)
        ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Get tracker.
        Tracker tracker = ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Set screen name.
        tracker.setScreenName(msgName.toLowerCase());
        tracker.enableAdvertisingIdCollection(true);
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    public String getAnalytics(String details) {
        return getInstance().getCarName(Values.carType) + Analytics.DOT + Values.tabExplore + details + Analytics.DOT + getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM + "";
    }
}
