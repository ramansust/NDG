package com.nissan.alldriverguide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.nissan.alldriverguide.utils.Values;

import java.io.File;


public class PDFOpenActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_open);

        File file = new File(Values.car_path + File.separator + Values.MAP_PDF_FOLDER + File.separator + Values.MAP_PDF_NAME);

        if (!file.exists()) {
            Toast.makeText(this, "error opening pdf file", Toast.LENGTH_SHORT).show();
            return;
        }

        webView = (WebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + file.getAbsolutePath());

    }
}
