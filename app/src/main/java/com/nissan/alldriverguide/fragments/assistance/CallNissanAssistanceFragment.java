package com.nissan.alldriverguide.fragments.assistance;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.CallNumberAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.model.CallInfo;
import com.nissan.alldriverguide.multiLang.model.ChildNode;
import com.nissan.alldriverguide.multiLang.model.CountryList;
import com.nissan.alldriverguide.multiLang.model.Datum;
import com.nissan.alldriverguide.utils.NissanApp;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CallNissanAssistanceFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private String[] countryNameFR = {"l' Allemagne", "l' Autriche", "la Belgique", "le Danemark", "l' Espagne", "l' Estonie", "la Finlande", "la France", "la Grèce", "la Hongrie", "l' Italie", "la Lettonie", "la Lituanie", "la Norvège", "les Pays-Bas", "la Pologne", "le Portugal", "la République Tchèque", "le Royaume-Uni", "la Slovaquie", "la Suède", "la Suisse"};
    private String[] countryName;
    private String[] countrFlag;
    private int[] flag = {R.drawable.austria, R.drawable.belgium, R.drawable.czech_republic/*, R.drawable.denmark, R.drawable.estonia, R.drawable.finland, R.drawable.france, R.drawable.germany, R.drawable.greece, R.drawable.hungary, R.drawable.italy, R.drawable.latvia, R.drawable.lithuania, R.drawable.netherlands, R.drawable.norway, R.drawable.poland, R.drawable.portugal, R.drawable.slovakia, R.drawable.spain, R.drawable.sweden, R.drawable.switzerland, R.drawable.united_kingdom*/};
    private String[] nationalNumber = {"0800215380", "0080050001001", "800232323"/*, "", "8006666", "", "0805112233", "08005894987", "2103428600", "0680333888", "800105800", "80003211", "880030725", "08000231513", "", "0801647726", "800200000", "0800112020", "900118119", "", "0800860900", "03301231231"*/};
    private String[] internationalNumber = {"+43190577777", "+3238703401", "+3613715491"/*, "+4570140147", "+3726506043", "+358107705222", "+33172676914", "+492232572079", "+302103428600", "+3613715493", "+390690808777", "+3726064071", "+3705270940", "+31205162026", "+4781521310 ", "+3613715496", "+34932907526", "+3613715495", "+34932907515", "+46850103000", "+41447365550", "+441913352879"*/};

    private View view;
    private ListView lstView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private CallNumberAdapter adapter;
    private ArrayList<CallInfo> list;

    private PreferenceUtil preferenceUtil;
    private Configuration conf;
    private Resources resources;
    private DisplayMetrics metrics;
    private TextView txtViewTitle;
    private Typeface tf;
    private static final String TITLE = "title";

    public static Fragment newInstance(String title) {
        Fragment frag = new CallNissanAssistanceFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_call_nissan_assistance, container, false);

//        countryName = getResources().getStringArray(R.array.country_for_call_array);

        initViews(view);
        loadResource();
        setListener();
        callNissanAssistance();
//        loadData();

        return view;
    }

    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
        txtViewTitle.setText(getArguments().getString(TITLE));
//        txtViewTitle.setTypeface(tf);
    }

    public void callNissanAssistance() {
        List<Datum> list = NissanApp.getInstance().getAssistanceInfo().getData();
        if (list != null && list.size() > 0) {
            List<ChildNode> childNodes;
            List<CountryList> countryLists;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getIndex() == 6) {
                    childNodes = list.get(i).getChildNode();
                    for (int j = 0; j < childNodes.size(); j++) {
                        if (childNodes.get(j).getIndex() == 2) {
                            countryLists = childNodes.get(j).getCountryList();
                            if (countryName == null & countrFlag == null) {
                                countryName = new String[countryLists.size()];
                                countrFlag = new String[countryLists.size()];
                                for (int k = 0; k < countryLists.size(); k++) {
                                    countryName[k] = countryLists.get(k).getCountryName();
                                    countrFlag[k] = countryLists.get(k).getThumbXhdpi();
                                    Log.e("callNissanAssistance: ", countryLists.get(k).getThumbXhdpi());
                                }
                            }
                        }
                    }
                }
            }
        }
        loadData();
    }

    private void loadData() {
        list = new ArrayList<>();

        for (int i = 0; i < countryName.length; i++) {
            CallInfo info = new CallInfo(countryName[i], flag[i], nationalNumber[i], internationalNumber[i]);
            list.add(info);
        }

        Collections.sort(list, new Comparator<CallInfo>() {
            @Override
            public int compare(CallInfo info1, CallInfo info2) {
//                return info1.getCountryName().compareToIgnoreCase(info2.getCountryName());
                Collator collator = Collator.getInstance(new Locale(preferenceUtil.getSelectedLang())); //Your locale here
                collator.setStrength(Collator.PRIMARY);
                return collator.compare(info1.getCountryName(), info2.getCountryName());
            }
        });

        if ("sv".equalsIgnoreCase(preferenceUtil.getSelectedLang())) {
            CallInfo info = list.get(list.size() - 1);
            list.remove(list.size() - 1);
            list.add(11, info);
        }

        adapter = new CallNumberAdapter(getActivity().getApplicationContext(), list);
        lstView.setAdapter(adapter);
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    private void initViews(View view) {
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        lstView = (ListView) view.findViewById(R.id.lst_view);
        txtViewTitle = (TextView) view.findViewById(R.id.txt_title);
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/Nissan Brand Regular.otf");
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
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final Dialog dialog = new DialogController(getActivity()).callNumberDialog();

        if ("fr".equalsIgnoreCase(preferenceUtil.getSelectedLang())) {
            TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
            txtViewTitle.setText(getResources().getString(R.string.telephone_number_for) + " " + countryNameFR[position]);
        } else {
            TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
            txtViewTitle.setText(getResources().getString(R.string.telephone_number_for) + " " + list.get(position).getCountryName());
        }

        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView txtViewNational = (TextView) dialog.findViewById(R.id.txt_view_national_number);
        txtViewNational.setText(" " + list.get(position).getNationalNumber());
        txtViewNational.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Uri number = Uri.parse("tel:" + list.get(position).getNationalNumber());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        TextView txtViewInternational = (TextView) dialog.findViewById(R.id.txt_view_international_number);
        txtViewInternational.setText(" " + list.get(position).getInternationalNumber());
        txtViewInternational.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Uri number = Uri.parse("tel:" + list.get(position).getInternationalNumber());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        if (list.get(position).getNationalNumber().isEmpty()) {
            ((LinearLayout) dialog.findViewById(R.id.linear_national)).setVisibility(View.GONE);
        }

        if (list.get(position).getInternationalNumber().isEmpty()) {
            ((LinearLayout) dialog.findViewById(R.id.linear_international)).setVisibility(View.GONE);
        }

        dialog.show();
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
