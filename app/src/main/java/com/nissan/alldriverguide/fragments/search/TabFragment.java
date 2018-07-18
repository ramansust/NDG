package com.nissan.alldriverguide.fragments.search;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.search.tab.BaseTabFragmentActivity;
import com.nissan.alldriverguide.fragments.search.tab.DataPassing;
import com.nissan.alldriverguide.fragments.search.tab.EngineComponentFragment;
import com.nissan.alldriverguide.fragments.search.tab.QRGFragment;
import com.nissan.alldriverguide.fragments.search.tab.TyreInfoFragment;
import com.nissan.alldriverguide.fragments.search.tab.WarningLightFragment;
import com.nissan.alldriverguide.fragments.search.tab.WarrantyFragment;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.SearchModel;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

/**
 * Created by nirob on 9/12/17.
 * Updated by shuvo on 20/12/17.
 */

public class TabFragment extends Fragment {

    private CommonDao commondao;
    private TabLayout tabLayout;
    private View fragmentView, shadowView;

    public static String keyword;
    private DataPassing dataPassing;

    private EditText getSearchKeyword;
    private TextView cancel;
    private TextView txtViewUpdatedContent;
    private ImageView imageViewBack, imageViewClear;
    private Typeface tf;
    private static Resources resources;
    private DisplayMetrics metrics;
    private PreferenceUtil preferenceUtil;
    private boolean isAnimation = false;
    public int currentTab;
    public int previousTab;

    public static Fragment newInstance(String keyword) {
        TabFragment frag = new TabFragment();
        frag.setKey(keyword);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPassing = (DataPassing) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((BaseTabFragmentActivity) getActivity()).setMedia(this);

        if (fragmentView != null) {
            return fragmentView;
        }

        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_search_tab, container, false);

        initializeView(fragmentView);
        populateDataIntoView();
        dataPassing.onDataPass(keyword);
        addAllTabsInTabLayout();
        setListener();
        checkUpdatedContent();
        setKeywordIfAvailable();

        return fragmentView;
    }

    private void populateDataIntoView() {

        String cancelText = NissanApp.getInstance().getGlobalMessage(getActivity()).getCancel();
        cancel.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_CANCEL) : cancelText);
    }

    private void setKeywordIfAvailable() {
        if (!isEmpty(BaseTabFragmentActivity.keyword)) {
            getSearchKeyword.setText(BaseTabFragmentActivity.keyword);
        }
    }

    /**
     * Initialized all variable
     * @param v for fragment layout view
     */
    private void initializeView(View v) {
        commondao = CommonDao.getInstance();
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
        getSearchKeyword = (EditText) v.findViewById(R.id.input_search);
        getSearchKeyword.setHint(getResources().getString(R.string.search_box_hint).toUpperCase());// set hint text  allCaps

        cancel = (TextView) v.findViewById(R.id.cancel_search);
        tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        imageViewBack = (ImageView) v.findViewById(R.id.imageViewBack);
        imageViewClear = (ImageView) v.findViewById(R.id.imageViewClearButton);
        shadowView = v.findViewById(R.id.shadowView);
        txtViewUpdatedContent = (TextView) v.findViewById(R.id.txt_view_updated_content);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/Nissan Brand Bold.otf"); //initialize typeface here.
        loadResources();

        //sdk support for shadowView
        if (Build.VERSION.SDK_INT < 21) {
            shadowView.setVisibility(View.VISIBLE);
        } else {
            shadowView.setVisibility(View.GONE);
        }

        setCurrentTabFragment(0);
    }

    // add icon in tab layout
    private void addAllTabsInTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.warning_light));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.quick_reference));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.tyre));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.engine_compartment));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.warranty));

        setDividerBetweenTabs();
    }

    /**
     * Set the click listener
     */
    private void setListener() {
        getSearchKeyword.setOnEditorActionListener(editorActionListener);
        cancel.setOnClickListener(clickListener);
        getSearchKeyword.addTextChangedListener(textWatcher);
        imageViewBack.setOnClickListener(clickListener);
        imageViewClear.setOnClickListener(clickListener);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                QRGFragment.whichTab = currentTab + 1;
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                previousTab = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * Here check the content update
     */
    private void checkUpdatedContent() {
        final ArrayList<PushContentInfo> list = commondao.getNotificationList(getActivity().getApplicationContext(), Values.carType, NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()));

        if (list != null && list.size() > 0) {
            txtViewUpdatedContent.setVisibility(View.VISIBLE);
        } else {
            txtViewUpdatedContent.setVisibility(View.GONE);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (isAnimation) {
            if (currentTab > previousTab) {
                transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            } else {
                transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out, R.anim.right_in, R.anim.left_out);
            }
        }
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(Values.tabSearchChildFragment);
        transaction.commit();
        isAnimation = true;
    }

    private void loadResources() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
    }

    /**
     * Here indicate the search tab fragment
     * @param tabPosition identify the specific Fragment
     */
    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(WarningLightFragment.newInstance());
                break;
            case 1:
                replaceFragment(QRGFragment.newInstance());
                break;
            case 2:
                replaceFragment(TyreInfoFragment.newInstance());
                break;
            case 3:
                replaceFragment(EngineComponentFragment.newInstance());
                break;
            case 4:
                replaceFragment(WarrantyFragment.newInstance());
                break;

            default:
                break;
        }
    }

    /**
     * here set the search tab divider
     */
    private void setDividerBetweenTabs() {
        //this is to set divider between tabs
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.tab_color));
        drawable.setSize(2, 1);
        linearLayout.setDividerPadding(25);
        linearLayout.setDividerDrawable(drawable);
    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.h_b_media_tab, null);
//        tabOne.setText("ONE");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warning_light, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.h_b_media_tab, null);
//        tabTwo.setText("TWO");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quick_reference, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.h_b_media_tab, null);
//        tabThree.setText("THREE");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tyre, 0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.h_b_media_tab, null);
//        tabFour.setText("Four");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(R.drawable.engine_compartment, 0, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);

        TextView tabFive = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.h_b_media_tab, null);
//        tabFive.setText("Five");
        tabFive.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warranty, 0, 0, 0);
        tabLayout.getTabAt(4).setCustomView(tabFive);
    }

    private void setCustomFontForTabs() {
        String tabs[] = resources.getStringArray(R.array.assistance_array_temp);
        //this is to set custom font for tabs
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            TextView tv = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.h_b_media_tab, null);
            tv.setTypeface(tf);
            tv.setText(tabs[i]);
            if (tabLayout.getTabAt(i) != null) {
                tabLayout.getTabAt(i).setCustomView(tv);
            }
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel_search:
//                    cancelButtonAction();
                    ((MainActivity) getActivity()).onBackPressed();
                    break;

                case R.id.imageViewBack:
                    ((MainActivity) getActivity()).onBackPressed();
                    break;

                case R.id.imageViewClearButton:
                    String searchKeyword = getSearchKeyword.getText().toString().trim();
                    if (searchKeyword.length() > 0) {
                        getSearchKeyword.setText("");
                    }
                    break;
            }
        }
    };


    /**
     * This listener for keyboard search action
     */
    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) { // here implement the keyboard search

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    new NissanApp().hideKeyboard(getActivity(), view);
                }

                String getKeyword = getSearchKeyword.getText().toString().trim();

                if (getKeyword.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please Input Search Keyword", Toast.LENGTH_SHORT).show();
                } else {
                    if (new NissanApp().insertSearchDataIntoDatabase(getActivity().getApplicationContext(), getKeyword)) {
                        long currentTime = System.currentTimeMillis();

                        String lang_type = new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang();
                        boolean isTagExists = commondao.isTagExists(getActivity().getApplicationContext(), getKeyword, Values.carType, lang_type);

                        if (isTagExists) {
                            int check_count = commondao.getCountForSpecificTag(getActivity().getApplicationContext(), getKeyword, Values.carType, lang_type);
                            commondao.updateSearchCountInSearchTable(getActivity().getApplicationContext(), check_count + 1, currentTime + "", getKeyword, Values.carType, lang_type);
                        } else {
                            SearchModel searchModel = new SearchModel(getKeyword, System.currentTimeMillis() + "", 1, Values.carType, lang_type);
                            commondao.insertNewKeywordInSearchTable(getActivity().getApplicationContext(), searchModel, Values.carType, lang_type);
                        }
                    } else {
                        Logger.error("not found", "______search_result!");
                    }


                    BaseTabFragmentActivity.keyword = getKeyword;

                    setListener();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_container, getFragment(currentTab));
                    transaction.addToBackStack(Values.tabSearchChildFragment);
                    transaction.commit();
                    isAnimation = true;

/*
                    viewPagerAdapter = new ViewPagerMediaAdapter(getActivity().getApplicationContext(), getChildFragmentManager());

                    if (viewPager != null) {
                        viewPager.setAdapter(viewPagerAdapter);
                    }
                    if (tabLayout != null) {
                        tabLayout.setupWithViewPager(viewPager);
                        setupTabIcons();
                    }

                    setCustomFontForTabs();

                    if (pageSelected > -1) {
                        viewPager.setCurrentItem(pageSelected);
                    }
*/

                }
                return true;
            }
            return false;
        }
    };

    private Fragment getFragment(int pos) {
        switch (pos) {
            case 0:
                return WarningLightFragment.newInstance();
            case 1:
                return QRGFragment.newInstance();
            case 2:
                return TyreInfoFragment.newInstance();
            case 3:
                return EngineComponentFragment.newInstance();
            case 4:
                return WarrantyFragment.newInstance();
            default:
                break;
        }

        return new Fragment();
    }

    ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
//            pageSelected = position;
            QRGFragment.whichTab = position + 1;
        }
    };


    private void setKey(String keyword) {
        this.keyword = keyword;
    }


    public class ViewPagerMediaAdapter extends FragmentPagerAdapter {

        String tabs[] = resources.getStringArray(R.array.assistance_array_temp);

        public ViewPagerMediaAdapter(Context context, FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new Fragment();

            switch (position) {
                case 0:
                    f = WarningLightFragment.newInstance();
                    break;

                case 1:
                    f = QRGFragment.newInstance(); //QuickRefGuideFragment.newInstance();
                    break;

                case 2:
                    f = TyreInfoFragment.newInstance(); //QRGFragment.getInstance(Values.TYRE_TYPE); //TyreInfoFragment.newInstance();
                    break;

                case 3:
                    f = EngineComponentFragment.newInstance(); //EngineCompartmentFragment.newInstance();
                    break;

                case 4:
                    f = WarrantyFragment.newInstance(); //WarrantyFragment.newInstance();
                    break;

                default:
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return tabs.length - 1;
        }

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = getSearchKeyword.getText().toString();
            if (text.length() > 0) {
                imageViewClear.setVisibility(View.VISIBLE);
            } else {
                imageViewClear.setVisibility(View.GONE);
            }
        }
    };

    private void cancelButtonAction() {
        getSearchKeyword.setText("");
        getSearchKeyword.clearFocus();
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            new NissanApp().hideKeyboard(getActivity(), view);
        }
    }

    public static String getTitleName(int _ePubType) {

        if (_ePubType == Values.COMBIMETER_TYPE)
            return resources.getString(R.string.warning_lights);
        else if (_ePubType == Values.HOMEPAGE_TYPE)
            return resources.getString(R.string.quick_reference_guide);
        else if (_ePubType == Values.TYRE_TYPE)
            return resources.getString(R.string.tyre_information);
        else if (_ePubType == Values.ENGINE_TYPE)
            return resources.getString(R.string.engine_compartment);
        else if (_ePubType == Values.WARRANTY_TYPE)
            return resources.getString(R.string.warranty);

        return "";
    }
}
