package com.nissan.alldriverguide.fragments.combimeter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nissan.alldriverguide.ImageTargetActivity;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.assistance.DetailsFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CombimeterFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CombimeterFragment";
    private View view;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private ScrollView scrollView;
    private int width = 0;
    private LinearLayout mainLinearLayout;
    private TextView txt_title, txt_back_title;
    private String drawable_folder = Values.car_path + "/combimeter_button"; // combimiter path from sdCard
    private static final String TITLE = "title";

    // Combimeter color list
    private ArrayList<String> list_red;
    private ArrayList<String> list_orange;
    private ArrayList<String> list_yellow;
    private ArrayList<String> list_green;
    private ArrayList<String> list_blue;
    private ArrayList<String> list_gray;
    private ArrayList<String> list_cyan;

    public static Fragment newInstance(String title) {
        Fragment frag = new CombimeterFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_combimeter, container, false);

        initViews(view);
        setListener();
        loadData();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp(); // this for rate our app click count
    }


    private void loadData() {
        txt_title.setText(getArguments().get(TITLE).toString());

        list_red = new ArrayList<String>();
        list_orange = new ArrayList<String>();
        list_yellow = new ArrayList<String>();
        list_green = new ArrayList<String>();
        list_blue = new ArrayList<String>();
        list_gray = new ArrayList<String>();
        list_cyan = new ArrayList<String>();

        width = NissanApp.getInstance().getWidth(getActivity());

        mainLinearLayout = new LinearLayout(getActivity());
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
    }

    private void setListener() {
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    private void initViews(View view) {
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        txt_back_title = (TextView) view.findViewById(R.id.txt_back_title);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (ImageTargetActivity.isDetected) {
                    getActivity().finish();
                } else {
                    ((MainActivity) getActivity()).onBackPressed();
                }
                break;
            case R.id.linear_back:
                if (ImageTargetActivity.isDetected) {
                    getActivity().finish();
                } else {
                    ((MainActivity) getActivity()).onBackPressed();
                }
                break;
            default:
                PreferenceUtil preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
                int ePubIndex = 0;
                /*if(Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
//                if(Values.carType == 14) {
                    ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;
                } else {
                    if(preferenceUtil.getSelectedLang().equalsIgnoreCase("pl") || preferenceUtil.getSelectedLang().equalsIgnoreCase("fi") || preferenceUtil.getSelectedLang().equalsIgnoreCase("pt")) {
                        ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;
                    } else {
                        ePubIndex = Integer.parseInt(v.getTag().toString());
                    }
                }*/

                Logger.error("__MB__Tag__", v.getTag().toString());
                ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;

//                ePubIndex = Integer.parseInt(v.getTag().toString()) * 2;

                // here redirect the DetailsFragment class for opening epub
                // according to combimeter icon
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                fragmentTransaction.replace(R.id.container, DetailsFragment.newInstance(ePubIndex - 1, getArguments().get(TITLE).toString()));
//                fragmentTransaction.replace(R.id.container, DetailsFragment.newInstance(ePubIndex - 1, getResources().getString(R.string.warning_lights)));
                fragmentTransaction.addToBackStack(Values.tabAssistance);
                fragmentTransaction.commit();
                break;
        }
    }


    /**
     * here added specific combimeter in specific ArrayList
     * and sorting the ArrayList
     */
    public void listFiles() {

        // here specify the combimeter folder path that belong in sdCard
        File dir = new File(drawable_folder + "/");

        if (dir.isDirectory()) {

            for (File file : dir.listFiles()) { // get all of the file from file path
                if (file.toString().contains("combimeter_")) {

                    Logger.error("___MB__", file.getName());
                    // here add the colors in ArrayList form combimeter folder
                    // comparing the naming convention like: _r = red, _y = blue, _grn = green etc.
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

        // here sorted the array list by the file number that contain in file name
        Collections.sort(list_red, new MassComparator());
        Collections.sort(list_orange, new MassComparator());
        Collections.sort(list_yellow, new MassComparator());
        Collections.sort(list_green, new MassComparator());
        Collections.sort(list_blue, new MassComparator());
        Collections.sort(list_gray, new MassComparator());
        Collections.sort(list_cyan, new MassComparator());
    }

    //Combimeter Optimization Testing
    private void loadCombiButtonsinGeneral(ArrayList<String> list_combi, int type) {
        LinearLayout linearLayout = new LinearLayout(getActivity());

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.setLayoutParams(llp);
        LinearLayout layoutHorizontal = null;
        int counter = 0;
        int column = 0;
        int max_column = 3;
        int space = 0;

        LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        llp2.width = (int) (width * .28); // set the layout width

        llp2.height = (int) (llp2.width * (98.00 / 150.00)); // set the layout height

        space = (width - llp2.width * max_column) / (max_column * 2);
        llp2.setMargins(space, space, space, space); // set the space in layout

        for (int j = 0; j < list_combi.size(); j++) {

            column++;

            if (column == 1) {
                layoutHorizontal = new LinearLayout(getActivity());

                layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                layoutHorizontal.setLayoutParams(llp);
            }
            ImageButton button = new ImageButton(getActivity());
            Drawable d = Drawable.createFromPath(drawable_folder + "/" + list_combi.get(j));
            final String[] output = list_combi.get(j).split("\\_");

            try {
                counter = Integer.parseInt(output[1]);

                button.setBackground(d);

                button.setLayoutParams(llp2);

                button.setTag(Integer.parseInt(output[1]));
                button.setOnClickListener(this);

                layoutHorizontal.addView(button);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] last_value = list_combi.get(list_combi.size() - 1).split("\\_");
            //counter++;

            if (column == 3) {
                column = 0;
                linearLayout.addView(layoutHorizontal);
                if (counter == Integer.parseInt(last_value[1])) {
                } else {
                    if (type == Values.RED_TYPE) {
                        linearLayout.addView(add_DashedLine(getActivity(), R.drawable.red_dash_line, space));//dashed line added by Rohan
                    } else if (type == Values.ORANGE_TYPE) {
                        linearLayout.addView(add_DashedLine(getActivity(), R.drawable.orange_dash_line, space));
                    } else if (type == Values.YELLOW_TYPE) {
                        linearLayout.addView(add_DashedLine(getActivity(), R.drawable.yellow_dash_line, space));
                    } else if (type == Values.GREEN_TYPE) {
                        linearLayout.addView(add_DashedLine(getActivity(), R.drawable.green_dash_line, space));
                    } else if (type == Values.CYAN_TYPE) {
                        linearLayout.addView(add_DashedLine(getActivity(), R.drawable.cyan_dash_line, space));
                    } else if (type == Values.BLUE_TYPE) {
                        linearLayout.addView(add_DashedLine(getActivity(), R.drawable.blue_dash_line, space));
                    } else if (type == Values.GRAY_TYPE) {
                        linearLayout.addView(add_DashedLine(getActivity(), R.drawable.gray_dash_line, space));
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
        /**
         * here add the dotted dash line view in "mainLinearLayout" layout
         * comparing with type argument
         */
        if (type == Values.RED_TYPE) {
            if (!list_orange.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.orange_dash_line, space));
            } else {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.yellow_dash_line, space));
            }
        } else if (type == Values.ORANGE_TYPE) {
            if (!list_yellow.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.yellow_dash_line, space));
            } else {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.green_dash_line, space));
            }
        } else if (type == Values.YELLOW_TYPE) {
            if (!list_green.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.green_dash_line, space));
            } else {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.cyan_dash_line, space));
            }
        } else if (type == Values.GREEN_TYPE) {
            if (!list_cyan.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.cyan_dash_line, space));
            } else {
                if (!list_blue.isEmpty()) {
                    mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.blue_dash_line, space));
                } else {
                    mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.gray_dash_line, space));
                }
            }
        } else if (type == Values.CYAN_TYPE) {
            if (!list_blue.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.blue_dash_line, space));
            } else {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.gray_dash_line, space));
            }
        } else if (type == Values.BLUE_TYPE) {
            if (!list_gray.isEmpty()) {
                mainLinearLayout.addView(add_DashedLine(getActivity(), R.drawable.gray_dash_line, space));
            } else if (type == Values.GRAY_TYPE) {
            }
        }

    }

    // this class using for sorting the ArrayList
    public static class MassComparator implements Comparator<String> {

        @Override
        public int compare(String weight1, String weight2) {

            String[] output1 = weight1.split("\\_");
            String[] output2 = weight2.split("\\_");

            // get only number form file name
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

    /**
     * @param c         context
     * @param drawables for dotted vertical line
     * @param space     used for layout margin
     * @return view
     */
    private View add_DashedLine(Context c, int drawables, int space) {

        LinearLayout colorLinearLayout = new LinearLayout(c);

        colorLinearLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        colorLinearLayout.setBackground(getResources().getDrawable(drawables));
        LinearLayout.LayoutParams llp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        llp3.setMargins(space, 0, space, 0);

        colorLinearLayout.setLayoutParams(llp3);

        return colorLinearLayout;
    }
}
