package com.nissan.alldriverguide.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.NissanApp;

/**
 * Created by nirob on 4/19/18.
 */

public class Feedback extends Fragment implements View.OnClickListener {

    private ImageView back;
    private Button btnSendFeedback;
    EditText etTitle, etDescription;
    private PreferenceUtil preferenceUtil;

    public static Fragment newInstance() {
        Fragment frag = new Feedback();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        initViews(view);
        setListener();

        return view;
    }

    /**
     * Here initialized all variable
     * @param view need for find id for layout
     */
    private void initViews(View view) {
        preferenceUtil = new PreferenceUtil(getActivity());
        back = (ImageView) view.findViewById(R.id.btn_back);
        btnSendFeedback = (Button) view.findViewById(R.id.send_feedback_button);
        etTitle = (EditText) view.findViewById(R.id.send_feedback_title);
        etDescription = (EditText) view.findViewById(R.id.send_feedback_description);
    }

    /**
     * set the listener for item click
     */
    private void setListener() {
        back.setOnClickListener(this);
        btnSendFeedback.setOnClickListener(this);
        etTitle.addTextChangedListener(textWatcher);
        etDescription.addTextChangedListener(textWatcher);
    }

    /**
     * Text watcher used for send feedback edit text box color change
     * if invalid edit text box show red else gray
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etTitle.getText().hashCode() == s.hashCode()) {
                if (etTitle.getText().toString().trim().length() > 0) {
                    etTitle.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_bg));
                }
            } else if (etDescription.getText().hashCode() == s.hashCode()) {
                if (etDescription.getText().toString().trim().length() > 0) {
                    etDescription.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_bg));
                }
            } else {

            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                ((MainActivity) getActivity()).onBackPressed();
                break;
            case R.id.send_feedback_button:

                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                if (title.equalsIgnoreCase("") && description.equalsIgnoreCase("")) {
                    etTitle.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_red_bg));
                    etDescription.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_red_bg));

                } else if (title.equalsIgnoreCase("")) {
                    etDescription.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_bg));
                    etTitle.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_red_bg));

                } else if (description.equalsIgnoreCase("")) {
                    etDescription.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_red_bg));
                    etTitle.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_bg));

                } else {
                    etTitle.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_bg));
                    etDescription.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.send_feedback_title_bg));

                    sendFeedback(title, description);
                }

                break;

            default:
                break;
        }
    }

    /**
     * Here call the api for send feedback
     * @param title for send feedback edit text title
     * @param description for send feedback edit text description
     */
    private void sendFeedback(String title, String description) {

        new ApiCall().postAddFeedback(NissanApp.getInstance().getDeviceID(getActivity()), title, description, NissanApp.getInstance().getDeviceModel(), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (responseInfo.getStatusCode().equalsIgnoreCase("200")) {

                    preferenceUtil.setSessionOne(false);
                    preferenceUtil.setSessionThree(false);
                    preferenceUtil.setIsFirstTimeGreatNotGreat(false);
                    preferenceUtil.resetUserNavigationCount();

                    Toast.makeText(getActivity(), getResources().getString(R.string.feedback_toast), Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onFailed(String failedReason) {
                Log.e("onDownloaded: ", failedReason);
            }
        });
    }
}
