package com.nissan.alldriverguide.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.nissan.alldriverguide.R;

/**
 * Created by shubha on 12/6/17.
 */

public class DialogErrorFragment extends DialogFragment {

    private Context context;
    private Button btDismiss;
    private TextView tvErrorMessage;
    private String errorMsg;
    private Typeface tf;

    public static DialogErrorFragment getInstance(Context context, String errorMsg) {
        DialogErrorFragment dialog = new DialogErrorFragment();
        dialog.context = context;
        dialog.errorMsg = errorMsg;
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_error_fragment, container);

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        setCancelable(false);

        initViews(view);
        setCustomFontForErrorMessage();

        return view;
    }

    private void initViews(View view) {
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/nissan_brand_bold.otf"); //initialize typeface here.
        tvErrorMessage = (TextView) view.findViewById(R.id.tvErrorMessage);

        btDismiss = (Button) view.findViewById(R.id.btDismiss);
        btDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        tvErrorMessage.setText(errorMsg);
    }

    private void setCustomFontForErrorMessage() {
        tvErrorMessage.setTypeface(tf);
    }
}
