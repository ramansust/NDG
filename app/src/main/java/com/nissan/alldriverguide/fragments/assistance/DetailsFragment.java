package com.nissan.alldriverguide.fragments.assistance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
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

import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;

public class DetailsFragment extends Fragment implements View.OnClickListener {
    private static final String EPUB_INDEX = "epub_index";

    private View view;
    private WebView webView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private TextView title, txt_back_title;
    private LinearLayout linearLayoutNoContent;
    private ProgressBar progressBar;
    private int index = 0;
    private String htmlContent = "";
    private ArrayList<EpubInfo> list;
    private static final String TITLE = "title";
    private Typeface tf;

    public static Fragment newInstance(int index, String title) {
        Fragment frag = new DetailsFragment();
        Bundle args = new Bundle();
        args.putInt(EPUB_INDEX, index);
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);

        initViews(view);
        setListener();
        setupWebView();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    private void initViews(View view) {
        webView = (WebView) view.findViewById(R.id.webViewDetailsFragment);
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        title = (TextView) view.findViewById(R.id.txt_title);
        txt_back_title = (TextView) view.findViewById(R.id.txt_back_title);
        linearLayoutNoContent = (LinearLayout) view.findViewById(R.id.linearLayoutNoContent);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarDetailsFragment);
        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/Nissan Brand Regular.otf"); //initialize typeface here.
        txt_back_title.setTypeface(tf);
    }

    private void setListener() {
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    private void loadData() throws IndexOutOfBoundsException {
        try {
            list = new ArrayList<>();
            title.setText(getArguments().getString(TITLE)); // here set the title on top bar
            switch (Values.ePubType) { // compare with epub type
                case Values.COMBIMETER_TYPE:
                    // check the toc file exist or not
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.COMBIMETER + Values.TOC_DIRECTORY).exists()) {
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.COMBIMETER);

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.COMBIMETER
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());
                            Logger.error("loadData: ", htmlContent);

                            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.WARNING_LIGHT + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.HOMEPAGE_TYPE:
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.HOME_PAGE
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());

                            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.HOMEPAGE + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.TYRE_TYPE:
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.TYRE + Values.TOC_DIRECTORY).exists()) {
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.TYRE);

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.TYRE
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());

                            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.TYRE + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.ENGINE_TYPE:
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.ENGINE + Values.TOC_DIRECTORY).exists()) {
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.ENGINE);

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.ENGINE
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());

                            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.ENGINE + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.WARRANTY_TYPE:
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.WARRANTY + Values.TOC_DIRECTORY).exists()) {
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.WARRANTY);

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.WARRANTY
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());

                            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.WARRANTY + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.BUTTON_TYPE:
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.BUTTON + Values.TOC_DIRECTORY).exists()) {
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.BUTTON);

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.BUTTON
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());

                            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.AUGMENTED_REALITY + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                case Values.INFO_TYPE:
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.INFO + Values.TOC_DIRECTORY).exists()) {
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.INFO);

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.INFO
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());

                            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.INFO));
                        }
                    }
                    break;

                case Values.ASSISTANCE_TYPE:
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.WARRANTY + Values.TOC_DIRECTORY).exists()) {
                        // parse toc.ncx file and added into list(added like: Text/DG_ZE1_WARRANTY_QR17xx-0ZE1E0EUR_November_2017-4.xhtml#_idParaDest-10)
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.WARRANTY);

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.WARRANTY
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(list.size() - 1).getHtmlLink());

                            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.WARRANTY + Analytics.DOT + list.get(index).getTitle()));
                        }
                    }
                    break;

                default:
                    break;
            }

            // here check the url
            // eg. url = file:///storage/emulated/0/.AllDriverGuide/micrak14/micrak14_en/.ar_warranty/OEBPS/Text/DG_K14_WARRANTY_DG17xx-0K14E0EUR_November_2017-4.xhtml#_idParaDest-10
            if (htmlContent.endsWith("/#")) {
                webView.setVisibility(View.GONE);
//                linearLayoutNoContent.setVisibility(View.VISIBLE);
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            } else {
                webView.loadUrl(htmlContent);
                webView.invalidate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            index = args.getInt(EPUB_INDEX);
        } else {
            index = savedInstanceState.getInt(EPUB_INDEX);
        }

        try {

            loadData();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.linear_back:
                ((MainActivity) getActivity()).onBackPressed();
                break;
            default:
                break;
        }
    }

    /**
     * setup web view
     */
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.clearCache(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setWebChromeClient(new WebChromeClient());
//        webView.getSettings().setDefaultFontSize((int) (dp2px(getActivity().getApplicationContext(), 20) / webView.getScale()));
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

    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
