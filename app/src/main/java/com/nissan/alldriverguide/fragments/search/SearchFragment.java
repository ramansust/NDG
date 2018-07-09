package com.nissan.alldriverguide.fragments.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.TopRecentAdapter;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.model.SearchModel;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;

public class SearchFragment extends Fragment {

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    private EditText getSearchKeyword;
    private ImageView imageViewClear;
    private TextView tvClearRecentSearch, tvSectionHeader;
    private LinearLayout linearLayoutNoContent;
    private RelativeLayout relativeLayoutSectionHeader;
    private CommonDao commonDao;
    private String getKeyword = "";


    //this is from top recent search fragment
    private String[] sectionHeader;

    private RecyclerView recyclerView;
    public Map<String, List<Object>> dataMap;
    private static final int NUM_COLUMNS = 3;

    private TopRecentAdapter adapter;
//    private ArrayList<SearchModel> countWise_List, dateWise_List;
    private ArrayList<SearchModel> dateWise_List;

    private String selectedLanguage = "";

    private Resources resources;
    private DisplayMetrics metrics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_search, container, false);

        initAll(layout);
        setListener();
//        startUp();
        addData();
        return layout;
    }

    /**
     * Initialized all variable
     * @param layout need to fragment layout view
     */
    private void initAll(View layout) {

        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()));

        commonDao = CommonDao.getInstance();
        getSearchKeyword = (EditText) layout.findViewById(R.id.input_search);
        String search_tag_hint = NissanApp.getInstance().getAlertMessage(getActivity(), new PreferenceUtil(getActivity()).getSelectedLang(), Values.SEARCH_BOX_HINT);
        String clear_text = NissanApp.getInstance().getGlobalMessage(getActivity(), new PreferenceUtil(getActivity()).getSelectedLang(), Values.CLEAR);
        String recent_search_text = NissanApp.getInstance().getGlobalMessage(getActivity(), new PreferenceUtil(getActivity()).getSelectedLang(), Values.RECENT_SEARCH);
        getSearchKeyword.setHint(search_tag_hint.isEmpty() ? resources.getString(R.string.search_box_hint).toUpperCase() : search_tag_hint.toUpperCase());// set hint text  allCaps

        imageViewClear = (ImageView) layout.findViewById(R.id.imageViewClearButton);
        tvClearRecentSearch = (TextView) layout.findViewById(R.id.tvClearSearch);
        tvSectionHeader = (TextView) layout.findViewById(R.id.tvSectionHeader);

        tvClearRecentSearch.setText(clear_text.isEmpty() ? resources.getString(R.string.clear) : clear_text);
        tvSectionHeader.setText(recent_search_text.isEmpty() ? resources.getString(R.string.recent_search) : recent_search_text);

        relativeLayoutSectionHeader = (RelativeLayout) layout.findViewById(R.id.rlSectionHeader);
        //this is from top recent fragment
        linearLayoutNoContent = (LinearLayout) layout.findViewById(R.id.linearLayoutNoContent);
        dataMap = new LinkedHashMap<>();
        recyclerView = (RecyclerView) layout.findViewById(R.id.rv_top_recent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);


//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(NUM_COLUMNS, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new FlowLayoutManager().removeItemPerLineLimit());
        selectedLanguage = new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang();
        sectionHeader = new String[]{resources.getString(R.string.recent_searches)};
//        sectionHeader = new String[]{resources.getString(R.string.top_search), resources.getString(R.string.recent_search)};
        //end here

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    private void setListener() {
        getSearchKeyword.setOnEditorActionListener(editorActionListener);
        getSearchKeyword.addTextChangedListener(textWatcher);
        imageViewClear.setOnClickListener(clickListener);
        tvClearRecentSearch.setOnClickListener(clickListener);
    }


    //this is from top recent fragment
    public void addData() {
//        countWise_List = commonDao.getCountWiseList(getActivity().getApplicationContext(), Values.carType, selectedLanguage);
        dateWise_List = commonDao.getDateWiseList(getActivity().getApplicationContext(), Values.carType, selectedLanguage);
//        dateWise_List = commonDao.getTotalList(getActivity().getApplicationContext(), Values.carType);

        if (dateWise_List != null && dateWise_List.size() > 0 && dateWise_List.size() > 10) {
            dateWise_List = (ArrayList<SearchModel>) dateWise_List.subList(0, 9);
        }

//        if (countWise_List.size() <= 0 && dateWise_List.size() <= 0) {
        if (dateWise_List != null && dateWise_List.size() <= 0) {
//            linearLayoutNoContent.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            relativeLayoutSectionHeader.setVisibility(View.GONE);
        } else {
            for (String section : sectionHeader) {
                List<Object> list = dataMap.get(section);
                if (list == null) {
                    list = new ArrayList<>();
                    dataMap.put(section, list);
                }

                /*if (section.equalsIgnoreCase(getResources().getString(R.string.top_search))) {
                    list.addAll(countWise_List);
                } else {
                    list.addAll(dateWise_List);
                }*/

                list.addAll(dateWise_List);
            }
            setAdapter();
        }
    }

    private void setAdapter() {
        List<Object> items = new ArrayList<Object>();


        for (Map.Entry<String, List<Object>> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            List<Object> list = dataMap.get(key);
            items.addAll(list);
        }

//        adapter = new TopRecentAdapter(getActivity(), countWise_List, dateWise_List);
        adapter = new TopRecentAdapter(getActivity(), dateWise_List);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            adapter.notifyDataSetChanged();
        }
    }

    //end here

    private long mLastClickTime;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imageViewClearButton:
                    if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    String searchKeyword = getSearchKeyword.getText().toString().trim();
                    if (searchKeyword.length() > 0) {
                        getSearchKeyword.setText("");
                    }
                    break;

                case R.id.tvClearSearch:
                    if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    new deleteRecentSearches().execute();
/*                    final Dialog dialog = new DialogController(getActivity()).langDialog();

                    TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);

                    Button btnYes = (Button) dialog.findViewById(R.id.btn_ok);
                    Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

                    txtViewTitle.setText(getResources().getString(R.string.search_delete_alert));

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            new deleteRecentSearches().execute();

                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                dialog.dismiss();
                        }
                    });

                    dialog.show();*/

                    break;
            }
        }
    };

    /**
     * This method for keyboard search action
     */
    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) { // keyboard search
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    hideKeyboard(view);
                }

                // get search keyword
                getKeyword = getSearchKeyword.getText().toString().trim();

                if (getKeyword.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please Input Search Keyword", Toast.LENGTH_SHORT).show();

                } else { // if search keyword is not blank
                    if (new NissanApp().insertSearchDataIntoDatabase(getActivity().getApplicationContext(), getKeyword)) {
                        long currentTime = System.currentTimeMillis(); //Calendar.getInstance().getTime();

                        String lang_type = new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang();
                        boolean isTagExists = commonDao.isTagExists(getActivity().getApplicationContext(), getKeyword, Values.carType, lang_type);

                        if (isTagExists) {
                            int check_count = commonDao.getCountForSpecificTag(getActivity().getApplicationContext(), getKeyword, Values.carType, lang_type);
                            commonDao.updateSearchCountInSearchTable(getActivity().getApplicationContext(), check_count + 1, currentTime + "", getKeyword, Values.carType, lang_type);
                        } else {
                            SearchModel searchModel = new SearchModel(getKeyword, System.currentTimeMillis() + "", 1, Values.carType, lang_type);
                            new CommonDao().insertNewKeywordInSearchTable(getActivity().getApplicationContext(), searchModel, Values.carType, lang_type);
                        }
                    } else {
                        Logger.error("not found", "______search_result!");
                    }

                    Values.keyWord = getKeyword;
                    // here indicate TabFragment when user take action for keyboard search
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                    transaction.replace(R.id.container, TabFragment.newInstance(getKeyword));
                    transaction.addToBackStack(Values.tabSearch);
                    transaction.commit();
                }

                return true;
            }

            return false;
        }
    };


    /**
     * Text watcher for editText clear icon Visible/InVisible
     */
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


    // delete recent search keyword in FlowLayout
    private class deleteRecentSearches extends AsyncTask<Void, Void, Boolean>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialogController(getActivity()).showDialog(resources.getString(R.string.search_delete_loader_message));

        }

        @Override
        protected Boolean doInBackground(Void... voids) {

             return commonDao.deleteRecentSearchesFromSearchTable(getActivity().getApplicationContext(), Values.carType, selectedLanguage);

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (aBoolean) {
                Logger.error("SearchFragment" ,"_______Successfully cleared your searches.");
                addData();
            } else {
                Logger.error("SearchFragment" ,"_______Problem clearing search.");
            }

        }
    }

    // keyboard hide method
    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (in != null)
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
