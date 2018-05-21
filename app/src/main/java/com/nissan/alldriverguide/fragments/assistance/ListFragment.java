package com.nissan.alldriverguide.fragments.assistance;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.TyreTransitionActivity;
import com.nissan.alldriverguide.adapter.ListAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class ListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private View view;
    private ListView lstView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private TextView txt_back_title;
    private ListAdapter adapter;
    private List<EpubInfo> list;
    private TextView title;
    private static final String TITLE = "title";
    private Typeface tf;
    private String tyre;
    PreferenceUtil preferenceUtil;

    /**
     * this ListFragment is used for display QRG list
     */
    public static Fragment newInstance(String title) {
        Fragment frag = new ListFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        initViews(view);
        setListener();

        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    private void loadData() throws Exception {
        title.setText(getArguments().get(TITLE).toString());
        txt_back_title.setTypeface(tf);
        switch (Values.ePubType) {

            case Values.HOMEPAGE_TYPE:
                if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
                    list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);
                    ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.HOMEPAGE));
                }
                break;

            case Values.TYRE_TYPE:
                if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.TYRE + Values.TOC_DIRECTORY).exists()) {
                    list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.TYRE);
                    ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.TYRE));
                }
                break;

            case Values.ENGINE_TYPE:
                if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.ENGINE + Values.TOC_DIRECTORY).exists()) {
                    list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.ENGINE);
                    ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.ENGINE));
                }
                break;

            case Values.WARRANTY_TYPE:
                if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.WARRANTY + Values.TOC_DIRECTORY).exists()) {
                    list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.WARRANTY);
                    ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.WARRANTY));
                }
                break;

            default:
                break;
        }
        if (Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
            Log.e("loadData: ", "********** " + Values.carType);
            Log.e("loadData: ", "********** " + Values.ePubType);
            Log.e("loadData: ", "********** " + new PreferenceUtil(getActivity()).getSelectedLang());
//        if(Values.carType == 14) {
            if (list != null && list.size() > 0) {
                int i = 0;
                Iterator<EpubInfo> epubInfo = list.iterator();
                while (epubInfo.hasNext()) {
                    i++;
                    epubInfo.next();

                    if (i % 2 == 0) {
                        epubInfo.remove();
                    }
                }

                if (Values.ePubType == Values.HOMEPAGE_TYPE) {
                    if (Values.carType == 11) {
                        list = list.subList(0, list.size() - 2);
                    } else if (Values.carType == 12) {
                        list = list.subList(0, list.size() - 2);
                    } else if (Values.carType == 13) {
                        list = list.subList(0, 33);
                    }
                } else if (Values.ePubType == Values.WARRANTY_TYPE) {
                    list.remove(list.size() - 1);
                }

                adapter = new ListAdapter(getActivity().getApplicationContext(), list);
                lstView.setAdapter(adapter);
            } else {

            }
        } else {
            if (preferenceUtil.getSelectedLang().equalsIgnoreCase("pl") ||
                    preferenceUtil.getSelectedLang().equalsIgnoreCase("fi") ||
                    preferenceUtil.getSelectedLang().equalsIgnoreCase("pt")) {
                Log.e("loadData: ", ".......................");

                if (list != null && list.size() > 0) {
                    int i = 0;
                    Iterator<EpubInfo> epubInfo = list.iterator();
                    while (epubInfo.hasNext()) {
                        i++;
                        epubInfo.next();

                        if (i % 2 == 0) {
                            epubInfo.remove();
                        }
                    }

                    if (Values.ePubType == Values.HOMEPAGE_TYPE) {
                        if (Values.carType == 11) {
                            list = list.subList(0, list.size() - 2);
                        } else if (Values.carType == 12) {
                            list = list.subList(0, list.size() - 2);
                        } else if (Values.carType == 13) {
                            list = list.subList(0, 33);
                        }
                    } else if (Values.ePubType == Values.WARRANTY_TYPE) {
                        list.remove(list.size() - 1);
                    }

                    adapter = new ListAdapter(getActivity().getApplicationContext(), list);
                    lstView.setAdapter(adapter);
                } else {

                }

            } else {
                Log.e("loadData: ", "............***...........");

                if (list != null && list.size() > 0) {
                    if (Values.ePubType == Values.HOMEPAGE_TYPE) {
                        if (Values.carType == 11) {
                            list = list.subList(0, list.size() - 2);
                        } else if (Values.carType == 12) {
                            list = list.subList(0, list.size() - 2);
                        } else if (Values.carType == 13) {
                            list = list.subList(0, 33);
                        }
                    } else if (Values.ePubType == Values.WARRANTY_TYPE) {
                        list.remove(list.size() - 1);
                    }
                    adapter = new ListAdapter(getActivity().getApplicationContext(), list);
                    lstView.setAdapter(adapter);
                } else {

                }
            }
        }

        /*if (list != null && list.size() > 0) {
            int i = 0;
            Iterator<EpubInfo> epubInfo = list.iterator();
            while (epubInfo.hasNext()) {
                i++;
                epubInfo.next();

                if(i % 2 == 0) {
                    epubInfo.remove();
                }
            }

            if(Values.ePubType == Values.HOMEPAGE_TYPE) {
                if (Values.carType == 11) {
                    list = list.subList(0, list.size() - 2);
                } else if (Values.carType == 12) {
                    list = list.subList(0, list.size() - 2);
                } else if (Values.carType == 13) {
                    list = list.subList(0, 33);
                }
            } else if(Values.ePubType == Values.WARRANTY_TYPE) {
                list.remove(list.size() - 1);
            }

            adapter = new ListAdapter(getActivity().getApplicationContext(), list);
            lstView.setAdapter(adapter);
        } else {

        }*/

    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    private void initViews(View view) {
        preferenceUtil = new PreferenceUtil(getActivity());
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        lstView = (ListView) view.findViewById(R.id.lst_view);
        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        title = (TextView) view.findViewById(R.id.txt_title);
        txt_back_title = (TextView) view.findViewById(R.id.txt_back_title);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/Nissan Brand Regular.otf"); //initialize typeface here.
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

        if (Values.ePubType == Values.TYRE_TYPE) {
            if (position == 2 || position == 6) {
                Values.gif_index = position;
                showSliderDialog(position);
            } else {
                frag = DetailsFragment.newInstance(list.get(position).getIndex(), title.getText().toString().trim());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, frag);
                ft.addToBackStack(Values.tabAssistance);
                ft.commit();
            }
        } else {
            frag = DetailsFragment.newInstance(list.get(position).getIndex(), title.getText().toString().trim());
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

    private void showSliderDialog(final int index) {

        final Dialog dialog = new DialogController(getActivity()).tyreDialog();

        TextView tv = (TextView) dialog.findViewById(R.id.txt_title);
        if (index == 2) {
            tv.setText(R.string.alert_msg42);
            tyre = "tyre gifs";

        } else if (index == 6) {
            tv.setText(R.string.alert_msg39);
            tyre = "repair kit";
        } else {

        }

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_read);
        dialogButton.setText(R.string.alert_msg40);
        dialogButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dialogButton2 = (Button) dialog.findViewById(R.id.btn_already);
        dialogButton2.setText(R.string.alert_msg41);

        dialogButton2.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(getActivity(), TyreTransitionActivity.class);
                startActivity(i);
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.TYRE + Analytics.DOT + tyre));
            }
        });
        dialog.show();
    }
}
