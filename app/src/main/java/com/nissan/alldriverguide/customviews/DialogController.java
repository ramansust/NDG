package com.nissan.alldriverguide.customviews;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

public class DialogController {

    private Activity activity;
    private Dialog dialog;

    public DialogController(Activity activity) {
        this.activity = activity;
    }

    public Dialog langDialog() {
        dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.download_lang_popup);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    public Dialog modelYearFeatureDialog() {
        dialog = new Dialog(this.activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_model_year);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public Dialog carDownloadDialog() {
        dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.download_lang_popup);
        ((TextView) dialog.findViewById(R.id.txt_header)).setText(activity.getResources().getString(R.string.download));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }


    public Dialog carDialog() {
        dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.download_car_popup);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    public Dialog internetDialog() {
        dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.internet_connection_popup);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    public Dialog tyreDialog() {
        dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tyre_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    public Dialog languageSelectionDialog() {
        dialog = new Dialog(this.activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_selection_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public Dialog callNumberDialog() {
        dialog = new Dialog(this.activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.call_number_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public Dialog contentUpdateDialog() {
        dialog = new Dialog(this.activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.download_lang_popup);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public Dialog pushRegistrationDialog() {
        dialog = new Dialog(this.activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.download_lang_popup);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public Dialog rateOurAppDialog() {
        dialog = new Dialog(this.activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rate_our_app_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String rate_this_app_yes_text = NissanApp.getInstance().getAlertMessage(activity, new PreferenceUtil(activity).getSelectedLang(), Values.RATE_OUR_APP_YES);
        String rate_ask_me_later_text = NissanApp.getInstance().getAlertMessage(activity, new PreferenceUtil(activity).getSelectedLang(), Values.RATE_ASK_ME_LATER);
        String rate_no_thanks_text = NissanApp.getInstance().getAlertMessage(activity, new PreferenceUtil(activity).getSelectedLang(), Values.RATE_NO_THANKS);
        String rate_our_app_subtitle = NissanApp.getInstance().getAlertMessage(activity, new PreferenceUtil(activity).getSelectedLang(), Values.RATE_OUR_APP_SUBTITLE);
        ((TextView) dialog.findViewById(R.id.txt_view_rate_this_app)).setText(rate_this_app_yes_text.isEmpty() ? activity.getResources().getString(R.string.rate_our_app_yes) : rate_this_app_yes_text);
        ((TextView) dialog.findViewById(R.id.txt_view_ask_me_later)).setText(rate_ask_me_later_text.isEmpty() ? activity.getResources().getString(R.string.rate_ask_me_later) : rate_ask_me_later_text);
        ((TextView) dialog.findViewById(R.id.txt_view_no_thanks)).setText(rate_no_thanks_text.isEmpty() ? activity.getResources().getString(R.string.rate_no_thanks) : rate_no_thanks_text);
        ((TextView) dialog.findViewById(R.id.txt_sub_title)).setText(rate_our_app_subtitle.isEmpty() ? activity.getResources().getString(R.string.rate_our_app_sub_title) : rate_our_app_subtitle);
        return dialog;
    }

    public Dialog greatNotGreatDialog() {
        dialog = new Dialog(this.activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.great_not_great_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    public Dialog feedBackDialog() {
        dialog = new Dialog(this.activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedback_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
}
