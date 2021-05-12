package com.nissan.alldriverguide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.WARNING_LIGHTS;

/**
 * Created by rohan on 2/23/17.
 */

public class CombimeterActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private PreferenceUtil preferenceUtil;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private ScrollView scrollView;
    private TextView txt_title;
    private int width = 0;
    private LinearLayout mainLinearLayout;
    private final String drawable_folder = Values.car_path + "/combimeter_button";

    //    Combimeter color list
    private ArrayList<String> list_red;
    private ArrayList<String> list_orange;
    private ArrayList<String> list_yellow;
    private ArrayList<String> list_green;
    private ArrayList<String> list_blue;
    private ArrayList<String> list_gray;
    private ArrayList<String> list_cyan;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_combimeter);

        initViews();
        setListener();
        loadData();
    }

    private void loadData() {
        // declare color category wise ArrayList
        list_red = new ArrayList<>();
        list_orange = new ArrayList<>();
        list_yellow = new ArrayList<>();
        list_green = new ArrayList<>();
        list_blue = new ArrayList<>();
        list_gray = new ArrayList<>();
        list_cyan = new ArrayList<>();

        width = NissanApp.getInstance().getWidth(CombimeterActivity.this);

        mainLinearLayout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLinearLayout.setLayoutParams(llp);

        listFiles();

        if (!list_red.isEmpty()) {
            loadCombiButtonsinGeneral(list_red, Values.RED_TYPE);
        }

        if (!list_orange.isEmpty()) {
            loadCombiButtonsinGeneral(list_orange, Values.ORANGE_TYPE);
        }

        if (!list_yellow.isEmpty()) {
            loadCombiButtonsinGeneral(list_yellow, Values.YELLOW_TYPE);
        }

        if (!list_green.isEmpty()) {
            loadCombiButtonsinGeneral(list_green, Values.GREEN_TYPE);
        }

        if (!list_cyan.isEmpty()) {
            loadCombiButtonsinGeneral(list_cyan, Values.CYAN_TYPE);
        }

        if (!list_blue.isEmpty()) {
            loadCombiButtonsinGeneral(list_blue, Values.BLUE_TYPE);
        }

        if (!list_gray.isEmpty()) {
            loadCombiButtonsinGeneral(list_gray, Values.GRAY_TYPE);
        }
        scrollView.addView(mainLinearLayout);
        String header_txt = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), WARNING_LIGHTS);
        txt_title.setText(header_txt.isEmpty() ? getResources().getString(R.string.warning_lights) : header_txt);
    }

    /**
     * Here set the click listener
     */
    private void setListener() {
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    /**
     * Here initialized all view
     */
    private void initViews() {
        context = CombimeterActivity.this;
        preferenceUtil = new PreferenceUtil(context);
        btnBack = findViewById(R.id.btn_back);
        linearBack = findViewById(R.id.linear_back);
        scrollView = findViewById(R.id.scroll_view);
        txt_title = findViewById(R.id.txt_title);
    }

    /**
     * Here load the name form sdCard into ArrayList
     */
    public void listFiles() {

        File dir = new File(drawable_folder + "/");

        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.toString().contains("combimeter_")) {

                    if (file.getName().contains("_r")) {
                        list_red.add(file.getName());
                    } else if (file.getName().contains("_y")) {
                        list_yellow.add(file.getName());
                    } else if (file.getName().contains("_b")) {
                        list_blue.add(file.getName());
                    } else if (file.getName().contains("_grn")) {
                        list_green.add(file.getName());
                    } else if (file.getName().contains("_c")) {
                        list_cyan.add(file.getName());
                    } else if (file.getName().contains("_g")) {
                        list_gray.add(file.getName());
                    } else if (file.getName().contains("_org")) {
                        list_orange.add(file.getName());
                    }
                }
            }
        }

        // sort the ArrayList using Comparator interface
        Collections.sort(list_red, new MassComparator());
        Collections.sort(list_orange, new MassComparator());
        Collections.sort(list_yellow, new MassComparator());
        Collections.sort(list_green, new MassComparator());
        Collections.sort(list_blue, new MassComparator());
        Collections.sort(list_gray, new MassComparator());
        Collections.sort(list_cyan, new MassComparator());
    }

    private void loadCombiButtonsinGeneral(ArrayList<String> list_combi, int type) {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.setLayoutParams(llp);
        LinearLayout layoutHorizontal = null;
        int counter = 0;
        int column = 0;
        int max_column = 3;
        int space;

        LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        llp2.width = (int) (width * .28); // set icon width

        llp2.height = (int) (llp2.width * (98.00 / 150.00)); // set icon height

        space = (width - llp2.width * max_column) / (max_column * 2); // set space between icon
        llp2.setMargins(space, space, space, space);

        for (int j = 0; j < list_combi.size(); j++) {

            column++;

            if (column == 1) {
                layoutHorizontal = new LinearLayout(getApplicationContext());

                layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                layoutHorizontal.setLayoutParams(llp);
            }
            ImageButton button = new ImageButton(getApplicationContext());

            Drawable d = Drawable.createFromPath(drawable_folder + "/" + list_combi.get(j));
            final String[] output = list_combi.get(j).split("_");

            try {
                counter = Integer.parseInt(output[1]);

                button.setBackground(d);
                button.setLayoutParams(llp2);

                button.setTag(Integer.parseInt(output[1]));
                button.setOnClickListener(this);

                Objects.requireNonNull(layoutHorizontal).addView(button);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] last_value = list_combi.get(list_combi.size() - 1).split("_");
            //counter++;

            if (column == 3) {
                column = 0;
                linearLayout.addView(layoutHorizontal);
                if (counter != Integer.parseInt(last_value[1])) {
                    if (type == Values.RED_TYPE) {
                        linearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.red_dash_line, space));//dashed line added by Rohan
                    } else if (type == Values.ORANGE_TYPE) {
                        linearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.orange_dash_line, space));
                    } else if (type == Values.YELLOW_TYPE) {
                        linearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.yellow_dash_line, space));
                    } else if (type == Values.GREEN_TYPE) {
                        linearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.green_dash_line, space));
                    } else if (type == Values.CYAN_TYPE) {
                        linearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.cyan_dash_line, space));
                    } else if (type == Values.BLUE_TYPE) {
                        linearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.blue_dash_line, space));
                    } else if (type == Values.GRAY_TYPE) {
                        linearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.gray_dash_line, space));
                    }
                }

            } else {
                try {
                    if (counter == Integer.parseInt(last_value[1])) {
                        column = 0;
                        linearLayout.addView(layoutHorizontal);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        }

        mainLinearLayout.addView(linearLayout);
        // here actually define which color deshline added after which one
        if (type == Values.RED_TYPE) {
            if (!list_orange.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.orange_dash_line, space));
            } else {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.yellow_dash_line, space));
            }
        } else if (type == Values.ORANGE_TYPE) {
            if (!list_yellow.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.yellow_dash_line, space));
            } else {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.green_dash_line, space));
            }
        } else if (type == Values.YELLOW_TYPE) {
            if (!list_green.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.green_dash_line, space));
            } else {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.cyan_dash_line, space));
            }
        } else if (type == Values.GREEN_TYPE) {
            if (!list_cyan.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.cyan_dash_line, space));
            } else {
                if (!list_blue.isEmpty()) {
                    mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.blue_dash_line, space));
                } else {
                    mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.gray_dash_line, space));
                }
            }
        } else if (type == Values.CYAN_TYPE) {
            if (!list_blue.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.blue_dash_line, space));
            } else {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.gray_dash_line, space));
            }
        } else if (type == Values.BLUE_TYPE) {
            if (!list_gray.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getApplicationContext(), R.drawable.gray_dash_line, space));
            }
        }

    }

    // for sorting ArrayList
    public static class MassComparator implements Comparator<String> {

        @Override
        public int compare(String weight1, String weight2) {

            String[] output1 = weight1.split("_");
            String[] output2 = weight2.split("_");

            int value = Integer.parseInt(output1[1]);
            int value2 = Integer.parseInt(output2[1]);

            int result = 0;
            if (value < value2) {
                result = -1;
            } else if (value > value2) {
                result = 1;
            }
            return result;
        }
    }

    //add dashed Line
    @SuppressLint("UseCompatLoadingForDrawables")
    private View add_DashedLine(Context c, int drawables, int space) {

        LinearLayout colorLinearLayout = new LinearLayout(c);

        colorLinearLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        colorLinearLayout.setBackground(getResources().getDrawable(drawables));
        LinearLayout.LayoutParams llp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        llp3.setMargins(space, 0, space, 0);

        colorLinearLayout.setLayoutParams(llp3);

        return colorLinearLayout;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        String EPUB_INDEX = "epub_index";
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.linear_back:
                finish();
                break;

            default:
/*
                if(Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
//                if(Values.carType == 14) {
                    ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;
                } else { // Three languages for all car epub index will be double coz search tag is added in new epub
                    if(preferenceUtil.getSelectedLang().equalsIgnoreCase("pl") || preferenceUtil.getSelectedLang().equalsIgnoreCase("fi") || preferenceUtil.getSelectedLang().equalsIgnoreCase("pt")) {
                        ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;
                    } else {
                        ePubIndex = Integer.parseInt(v.getTag().toString());
                    }
                }
*/
                if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                int ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;

//                ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;

                startActivity(new Intent(CombimeterActivity.this, DetailsActivity.class).putExtra(EPUB_INDEX, ePubIndex - 1).putExtra("epub_title", ((TextView) findViewById(R.id.txt_title)).getText().toString().trim()));
                break;
        }
    }
}
