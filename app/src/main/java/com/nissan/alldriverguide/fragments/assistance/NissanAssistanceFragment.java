package com.nissan.alldriverguide.fragments.assistance;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.AssistanceAdapter;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

public class NissanAssistanceFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private int[] nissanNssistanceImage = {R.drawable.pickup, R.drawable.phone};

    private View view;
    private TextView txtViewCarName;
    private TextView txtViewDriverGuide;
    private ImageView imageView;
    private ListView lstView;
    private TextView txtViewTitle;

    private ImageButton btnBack;
    private LinearLayout linearBack;

    private AssistanceAdapter adapter;

    private DisplayMetrics metrics;
    public Resources resources;
    private PreferenceUtil preferenceUtil;
    private static final String TITLE = "title";

    public static Fragment newInstance(String title) {
        Fragment frag = new NissanAssistanceFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assistance, container, false);

        initViews(view);
        loadResource();
        setListener();
        loadData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    /**
     * loading initialized data
     */
    private void loadData() {
        linearBack.setVisibility(View.VISIBLE);

        txtViewDriverGuide.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "font/Nissan Brand Regular.otf"));
        txtViewDriverGuide.setText(resources.getString(R.string.driver_guide));
        txtViewTitle.setText(getArguments().get(TITLE).toString());
        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), resources.getStringArray(R.array.nissan_assistance_array), nissanNssistanceImage);
        lstView.setAdapter(adapter);

        // condition for new four cars (eg. 11 = All New Nissan Micra, 12 = New Nissan QASHQAI etc...)
        if(Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
            txtViewCarName.setText(resources.getStringArray(R.array.car_names)[Values.carType - 1]);
        } else {
            txtViewCarName.setText("NISSAN " + resources.getStringArray(R.array.car_names)[Values.carType - 1]);
        }
        // set image background according to car type
        setCarBackground(Values.carType);
    }

    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    // set image background for assistance
    private void setCarBackground(int index) {
        NissanApp.getInstance().setCarImageAssistance(index, imageView);
    }

    private void initViews(View view) {
        txtViewCarName = (TextView) view.findViewById(R.id.txt_view_car_name);
        txtViewDriverGuide = (TextView) view.findViewById(R.id.txt_view_driver_guide);
        imageView = (ImageView) view.findViewById(R.id.img_car_bg);
        lstView = (ListView) view.findViewById(R.id.lst_view);
        txtViewTitle = (TextView) view.findViewById(R.id.txt_title);

        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment frag = null;
        // here set the epbub type for NissanAssistanceFragment
        Values.ePubType = Values.ASSISTANCE_TYPE;

        switch (position) {
            case 0:
                frag = DetailsFragment.newInstance(0, resources.getStringArray(R.array.nissan_assistance_array)[position]);
                break;

            case 1:
                frag = CallNissanAssistanceFragment.newInstance(resources.getStringArray(R.array.nissan_assistance_array)[position]);
                break;

            default:
                break;
        }

        // here redirect and transaction for desire fragment
        if (frag != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            ft.replace(R.id.container, frag);
            ft.addToBackStack(Values.tabAssistance);
            ft.commit();
        }
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
}
