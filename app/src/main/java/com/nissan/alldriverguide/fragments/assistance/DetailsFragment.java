package com.nissan.alldriverguide.fragments.assistance;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteAssistanceTabContent;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;

public class DetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "DetailsFragment";

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
    private DisplayMetrics metrics;
    private Resources resources;

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
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), new PreferenceUtil(getActivity()).getSelectedLang()));

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

                    Logger.error("Epub type ", " Home  " + index);
                    if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
                        list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);

                        for (EpubInfo info : list
                        ) {
                            Logger.error("EpubInfo Title", " -- " + info.getTitle());
                            Logger.error("EpubInfo Index", " -- " + info.getIndex());
                        }

                        if (list != null) {
                            htmlContent = "file://"
                                    + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()
                                    + Values.HOME_PAGE
                                    .trim() + "/OEBPS/"
                                    + Uri.decode(list.get(index).getHtmlLink());


                            Logger.error("Epub type ", " content  " + htmlContent);
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
                // action for app back button
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    ((MainActivity) getActivity()).onBackPressed();
                }
                break;
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

            // this method call form below api level 24
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Logger.error("Details::url", url);
                if (url != null && url.startsWith("http")) {
                    return false;

                } else if (url != null && url.startsWith("file")) {

                    if (new PreferenceUtil(getActivity()).isCallNissan()) {
                        emptyWebVieLinkwAlert();
                    } else {
                        nissanCallFragment();
                    }
                    return true;
                }
                return false;
            }

            // this method call form api level 24
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (url != null && url.startsWith("http")) {
                    return false;

                } else if (url != null && url.startsWith("file")) {

                    if (new PreferenceUtil(getActivity()).isCallNissan()) {
                        emptyWebVieLinkwAlert();
                    } else {
                        nissanCallFragment();
                    }
                    return true;
                }
                return false;

                // Return false means, web view will handle the link
                // Return true means, leave the current web view and handle the url itself
            }
        });


        // this method working for device back button
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });

    }

    public void emptyWebVieLinkwAlert() {
        final Dialog dialog = new DialogController(getActivity()).langDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        String haveYouAlreadyText = NissanApp.getInstance().getAlertMessage(getActivity(), new PreferenceUtil(getActivity()).getSelectedLang(), Values.HAVE_YOU_ALREADY_CONSULTED);
        txtViewTitle.setText(haveYouAlreadyText == null || haveYouAlreadyText.isEmpty() ? resources.getString(R.string.web_view_call_nissan_link_popup) : haveYouAlreadyText);

        String yesText = NissanApp.getInstance().getGlobalMessage(getActivity()).getYes();
        String noText = NissanApp.getInstance().getGlobalMessage(getActivity()).getNo();

        Button btnYes = (Button) dialog.findViewById(R.id.btn_cancel);
        btnYes.setText(yesText == null || yesText.isEmpty() ? resources.getString(R.string.button_YES) : yesText);

        Button btnNo = (Button) dialog.findViewById(R.id.btn_ok);
        btnNo.setText(noText == null || noText.isEmpty() ? resources.getString(R.string.button_NO) : noText);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new PreferenceUtil(getActivity()).setCallNissan(false);
                nissanCallFragment();
            }
        });

        dialog.show();
    }

    private void nissanCallFragment() {
        String sharedpref_key = Values.carType + "_" + Values.ASSISTANCE_OBJ_STORE_KEY;
        AssistanceInfo assistanceInfo = new PreferenceUtil(getActivity()).retrieveAssistanceData(sharedpref_key);

        if (assistanceInfo != null && assistanceInfo.getData() != null && assistanceInfo.getData().size() > 0) {
            NissanApp.getInstance().setAssistanceInfo(assistanceInfo);

            Fragment frag = CallNissanAssistanceFragment.newInstance(resources.getStringArray(R.array.nissan_assistance_array)[1]);
            if (frag != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, frag);
                ft.addToBackStack(Values.tabExplore);
                ft.commit();
            }
        } else {
            postAssistanceData();
        }
    }

    public void postAssistanceData() {
        int language_ID = NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang());
        new ApiCall().postAssistanceTabContent(NissanApp.getInstance().getDeviceID(getActivity()), "" + language_ID, "" + Values.carType, Values.EPUBID, "2", new CompleteAssistanceTabContent() {
            @Override
            public void onDownloaded(AssistanceInfo responseInfo) {

                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    new PreferenceUtil(getActivity()).storeAssistanceData(responseInfo, Values.carType + "_" + Values.ASSISTANCE_OBJ_STORE_KEY);
                    NissanApp.getInstance().setAssistanceInfo(responseInfo);

                    Fragment frag = CallNissanAssistanceFragment.newInstance(resources.getStringArray(R.array.nissan_assistance_array)[1]);
                    if (frag != null) {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                        ft.replace(R.id.container, frag);
                        ft.addToBackStack(Values.tabExplore);
                        ft.commit();
                    }
                }
            }

            @Override
            public void onFailed(String failedReason) {

            }
        });
    }


    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
