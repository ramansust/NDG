package com.nissan.alldriverguide.fragments.assistance;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.AssistanceAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.multiLang.model.ChildNode;
import com.nissan.alldriverguide.multiLang.model.Datum;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class NissanAssistanceFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final int[] nissanNssistanceImage = {R.drawable.pickup, R.drawable.phone, R.drawable.calendar};

    private TextView txtViewCarName;
    private TextView txtViewDriverGuide;
    private SimpleDraweeView imageView;
    private ListView lstView;
    private TextView txtViewTitle;

    private ImageButton btnBack;
    private LinearLayout linearBack;

    public Resources resources;
    private PreferenceUtil preferenceUtil;
    private static final String TITLE = "title";
    private static final String IMG_URL = "img_url";
    private static final String ONLINE_BOOKING_URL = "";
    private String[] nissanAssistance;
    private String onlineBookingTItle;

    public static Fragment newInstance(String title, String imgUrl, String onLineUrl) {
        Fragment frag = new NissanAssistanceFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(IMG_URL, imgUrl);
        args.putString(ONLINE_BOOKING_URL, onLineUrl);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assistance, container, false);

        initViews(view);
        loadResource();
        setListener();
        nissanAssistance();
//        loadData();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    private void nissanAssistance() {
        List<Datum> list = NissanApp.getInstance().getAssistanceInfo().getData();
        if (list != null && list.size() > 0) {
            List<ChildNode> childNodes;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getIndex() == 6) {
                    childNodes = list.get(i).getChildNode();
                    if (nissanAssistance == null) {
                        if (!(Objects.requireNonNull(requireArguments().getString(ONLINE_BOOKING_URL)).isEmpty())) {

                            nissanAssistance = new String[childNodes.size() + 1];
                            for (int j = 0; j < childNodes.size(); j++) {
                                nissanAssistance[j] = childNodes.get(j).getTitle();
                            }

                            if (list.get(6).getTitle() != null) {
                                onlineBookingTItle = list.get(6).getTitle();
                                nissanAssistance[childNodes.size()] = onlineBookingTItle;
                            }
                        } else {
                            nissanAssistance = new String[childNodes.size()];
                            for (int j = 0; j < childNodes.size(); j++) {
                                nissanAssistance[j] = childNodes.get(j).getTitle();
                            }
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
//        txtViewDriverGuide.setText(resources.getString(R.string.driver_guide));
        txtViewDriverGuide.setText(NissanApp.getInstance().getAssistanceInfo().getAssistanceTitle());
        txtViewTitle.setText(Objects.requireNonNull(requireArguments().get(TITLE)).toString());
//        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), resources.getStringArray(R.array.nissan_assistance_array), nissanNssistanceImage);
        AssistanceAdapter adapter = new AssistanceAdapter(requireActivity().getApplicationContext(), nissanAssistance, nissanNssistanceImage);
        lstView.setAdapter(adapter);

        txtViewCarName.setText(NissanApp.getInstance().assistanceInfo.getSelectedCar());

        txtViewCarName.setBackgroundResource(R.color.black);
        // set image background according to car type
        setCarBackground();
    }

    private void loadResource() {
        resources = new Resources(requireActivity().getAssets(), new DisplayMetrics(), NissanApp.getInstance().changeLocalLanguage(requireActivity(), preferenceUtil.getSelectedLang()));
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    // set image background for assistance
    private void setCarBackground() {
        if (Objects.requireNonNull(requireArguments().get(IMG_URL)).toString().isEmpty()) {
            imageView.setBackgroundResource(R.drawable.car_download_place_holder);
        } else
            imageView.setImageURI(Objects.requireNonNull(requireArguments().get(IMG_URL)).toString());

//        imageView.setBackgroundResource(R.drawable.car_download_place_holder);
//        NissanApp.getInstance().setCarImageAssistance(index, imageView);
    }

    private void initViews(View view) {
        txtViewCarName = view.findViewById(R.id.txt_view_car_name);
        txtViewDriverGuide = view.findViewById(R.id.txt_view_driver_guide);
        imageView = view.findViewById(R.id.img_car_bg);
        lstView = view.findViewById(R.id.lst_view);
        txtViewTitle = view.findViewById(R.id.txt_title);

        linearBack = view.findViewById(R.id.linear_back);
        btnBack = view.findViewById(R.id.btn_back);
        preferenceUtil = new PreferenceUtil(requireActivity().getApplicationContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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

            case 2:
                if (!DetectConnection.checkInternetConnection(requireActivity())) {
                    showNoInternetDialogue();
                    return;
                }
                if (!Objects.requireNonNull(requireArguments().getString(ONLINE_BOOKING_URL)).isEmpty()) {
                    frag = OnlineBookingFragment.newInstance(onlineBookingTItle, requireArguments().getString(ONLINE_BOOKING_URL));
                }
                break;

            default:
                break;
        }

        // here redirect and transaction for desire fragment
        if (frag != null) {
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            ft.replace(R.id.container, frag);
            ft.addToBackStack(Values.tabAssistance);
            ft.commit();
        }
    }

    private void showNoInternetDialogue() {

        final Dialog dialog = new DialogController(getActivity()).internetDialog();
        dialog.setCancelable(false);
        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText("No Internet Connection. Please check your WIFI or cellular data network and try again.");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            requireActivity().finish();
        });

        dialog.show();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.linear_back:
                requireActivity().onBackPressed();
                break;

            default:
                break;
        }
    }
}
