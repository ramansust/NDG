package com.nissan.alldriverguide.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;

/**
 * Created by nirob on 4/19/18.
 */

public class Feedback extends Fragment implements View.OnClickListener {

    private ImageView back;
    private Button btnSendFeedback;
    private EditText etTitle, etDescription;
    private TextView tvTitle, tvTitleField, tvDescriptionField;
    private PreferenceUtil preferenceUtil;
    private long mLastClickTime;
    private ProgressDialog progressDialog;

    public static Fragment newInstance() {
        Fragment frag = new Feedback();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        initViews(view);
        loadTextFromDatabase();
        setListener();

        return view;
    }

    private void loadTextFromDatabase() {
        String pageTitle = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.FEEDBACK_TITLE);
        tvTitle.setText(pageTitle.isEmpty() ? getActivity().getResources().getString(R.string.feedback) : pageTitle);
        String titleField = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.TITLE_FIELD);
        tvTitleField.setText(titleField.isEmpty() ? getActivity().getResources().getString(R.string.send_feedback_title) : titleField);
        String descriptionField = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.DESCRIPTION_FIELD);
        tvDescriptionField.setText(descriptionField.isEmpty() ? getActivity().getResources().getString(R.string.send_feedback_description) : descriptionField);
        String send_feedback_button_text = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.SEND_FEEDBACK_BUTTON_TEXT);
        btnSendFeedback.setText(send_feedback_button_text.isEmpty() ? getActivity().getResources().getString(R.string.send_feedback_button) : send_feedback_button_text);
    }

    /**
     * Here initialized all variable
     *
     * @param view need for find id for layout
     */
    private void initViews(View view) {
        preferenceUtil = new PreferenceUtil(getActivity());
        back = (ImageView) view.findViewById(R.id.btn_back);
        btnSendFeedback = (Button) view.findViewById(R.id.send_feedback_button);
        etTitle = (EditText) view.findViewById(R.id.send_feedback_title);
        etDescription = (EditText) view.findViewById(R.id.send_feedback_description);
        tvTitle = (TextView) view.findViewById(R.id.txt_title);
        tvTitleField = (TextView) view.findViewById(R.id.tvTitleField);
        tvDescriptionField = (TextView) view.findViewById(R.id.tvDescriptionField);
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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

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
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ((MainActivity) getActivity()).onBackPressed();
                break;
            case R.id.send_feedback_button:
                if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

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
                    if (DetectConnection.checkInternetConnection(getActivity())) {
                        progressDialog = new ProgressDialogController(getActivity()).showDialog(getResources().getString(R.string.sending_feedback));
                        sendFeedback(title, description);
                    } else {
                        String internetCheckMessage = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
                        NissanApp.getInstance().showInternetAlert(this.getActivity(), internetCheckMessage.isEmpty() ? getResources().getString(R.string.internet_connect) : internetCheckMessage);
                    }
                }

                break;

            default:
                break;
        }
    }

    /**
     * Here call the api for send feedback
     *
     * @param title       for send feedback edit text title
     * @param description for send feedback edit text description
     */
    private void sendFeedback(String title, String description) {
        if (DetectConnection.checkInternetConnection(getActivity())) {
            new ApiCall().postAddFeedback(NissanApp.getInstance().getDeviceID(getActivity()), title, description, NissanApp.getInstance().getDeviceModel(), new CompleteAPI() {
                @Override
                public void onDownloaded(ResponseInfo responseInfo) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    Logger.error("onDownloaded: ", "______" + responseInfo.getStatusCode());

                    if (responseInfo.getStatusCode().equalsIgnoreCase("200")) {

                        preferenceUtil.setSessionOne(false);
                        preferenceUtil.setSessionThree(false);
                        preferenceUtil.setIsFirstTimeGreatNotGreat(false);
                        preferenceUtil.resetUserNavigationCount();
                        if (getActivity() != null) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            getActivity().onBackPressed();
                            String toastMsg = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.SEND_FEEDBACK_COMPLETE_TOAST);
                            Toast.makeText(getActivity(), toastMsg.isEmpty() ? "Thanks for your feedback" : toastMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailed(String failedReason) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Logger.error("onFailed: ", failedReason);
                }
            });
        }
    }
}
