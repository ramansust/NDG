package com.nissan.alldriverguide.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.nissan.alldriverguide.R;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created by shubha on 12/6/17.
 */

public class DialogErrorFragment extends DialogFragment {

    private Context context;
    private String errorMsg;

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

        Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        setCancelable(false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        TextView tvErrorMessage = view.findViewById(R.id.tvErrorMessage);

        Button btDismiss = view.findViewById(R.id.btDismiss);
        btDismiss.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).dismiss());

        tvErrorMessage.setText(errorMsg);
    }

    private void setCustomFontForErrorMessage() {
//        tvErrorMessage.setTypeface(tf);
    }
}
