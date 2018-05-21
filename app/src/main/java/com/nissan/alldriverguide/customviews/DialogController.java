package com.nissan.alldriverguide.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.nissan.alldriverguide.R;

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
