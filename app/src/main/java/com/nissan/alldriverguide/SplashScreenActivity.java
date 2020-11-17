package com.nissan.alldriverguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        createSplashScreen();

        /*Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            String carId = extras.getString("car_id");
            String languageId = extras.getString("language_id");
            String ePub_id = extras.getString("epub_id");

            Logger.error("Car_id : " + carId + " Languag_id : " + languageId, "ePub_id : " +ePub_id);
        }*/


        /*ViewGroup flowContainer = (ViewGroup) findViewById(R.id.flow_layout);
        for (Locale locale : Locale.getAvailableLocales()) {
            String countryName = locale.getDisplayCountry();
            if (!countryName.isEmpty()) {
                flowContainer.addView(createDummyTextView(countryName),
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }*/
    }

    private View createDummyTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setBackgroundResource(R.drawable.rectangle_shape);
        textView.setPadding(5, 5, 5, 5);
        return textView;
    }

    /**
     * Splash display for 3 sec then go to CarDownloadActivity class
     */
    private void createSplashScreen() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, CarDownloadActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
