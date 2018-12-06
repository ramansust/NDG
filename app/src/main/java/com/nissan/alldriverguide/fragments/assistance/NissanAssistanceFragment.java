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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.AssistanceAdapter;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.multiLang.model.ChildNode;
import com.nissan.alldriverguide.multiLang.model.Datum;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.util.List;

public class NissanAssistanceFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "NissanAssistanceFragmen";
    private int[] nissanNssistanceImage = {R.drawable.pickup, R.drawable.phone};

    private View view;
    private TextView txtViewCarName;
    private TextView txtViewDriverGuide;
    private SimpleDraweeView imageView;
    private ListView lstView;
    private TextView txtViewTitle;

    private ImageButton btnBack;
    private LinearLayout linearBack;

    private AssistanceAdapter adapter;

    private DisplayMetrics metrics;
    public Resources resources;
    private PreferenceUtil preferenceUtil;
    private static final String TITLE = "title";
    private static final String IMG_URL = "img_url";
    private String[] nissanAssistance;

    public static Fragment newInstance(String title, String imgUrl) {
        Fragment frag = new NissanAssistanceFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(IMG_URL, imgUrl);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assistance, container, false);

        initViews(view);
        loadResource();
        setListener();
        nissanAssistance();
//        loadData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    private void nissanAssistance() {
        List<Datum> list = NissanApp.getInstance().getAssistanceInfo().getData();
        if (list != null && list.size() > 0) {
            List<ChildNode> childNodes;
            for (int i = 0; i <list.size(); i++) {
                if (list.get(i).getIndex() == 6) {
                    childNodes = list.get(i).getChildNode();
                    if (nissanAssistance == null) {
                        nissanAssistance = new String[childNodes.size()];
                        for (int j = 0; j < childNodes.size(); j++) {
                            nissanAssistance[j] = childNodes.get(j).getTitle();
                        }
                    }
                }
            }
        }
        loadData();
    }

    /**
     * loading initialized data
     */
    private void loadData() {
        linearBack.setVisibility(View.VISIBLE);

        txtViewDriverGuide.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "font/Nissan Brand Regular.otf"));
//        txtViewDriverGuide.setText(resources.getString(R.string.driver_guide));
        txtViewDriverGuide.setText(NissanApp.getInstance().getAssistanceInfo().getAssistanceTitle());
        txtViewTitle.setText(getArguments().get(TITLE).toString());
//        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), resources.getStringArray(R.array.nissan_assistance_array), nissanNssistanceImage);
        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), nissanAssistance, nissanNssistanceImage);
        lstView.setAdapter(adapter);

        txtViewCarName.setText(NissanApp.getInstance().assistanceInfo.getSelectedCar());

        txtViewCarName.setBackgroundResource(R.color.black);
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
        if (getArguments().get(IMG_URL).toString().isEmpty())
            imageView.setBackgroundResource(R.drawable.car_download_place_holder);
        else
            imageView.setImageURI(getArguments().get(IMG_URL).toString());

//        imageView.setBackgroundResource(R.drawable.car_download_place_holder);
//        NissanApp.getInstance().setCarImageAssistance(index, imageView);
    }

    private void initViews(View view) {
        txtViewCarName = (TextView) view.findViewById(R.id.txt_view_car_name);
        txtViewDriverGuide = (TextView) view.findViewById(R.id.txt_view_driver_guide);
        imageView = (SimpleDraweeView) view.findViewById(R.id.img_car_bg);
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
//                frag = DetailsFragment.newInstance(0, resources.getStringArray(R.array.nissan_assistance_array)[position]);
                frag = DetailsFragment.newInstance(0, nissanAssistance[position]);
                break;

            case 1:
//                frag = CallNissanAssistanceFragment.newInstance(resources.getStringArray(R.array.nissan_assistance_array)[position]);
                frag = CallNissanAssistanceFragment.newInstance(nissanAssistance[position]);
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
