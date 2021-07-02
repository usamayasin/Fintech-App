/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.core.util;

import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.mifos.App;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author fomenkoo
 */
public class Toaster {

    public static final int INDEFINITE = Snackbar.LENGTH_INDEFINITE;
    public static final int LONG = Snackbar.LENGTH_LONG;
    public static final int SHORT = Snackbar.LENGTH_SHORT;

    public static void show(View view, String text, int duration) {
//        final Snackbar snackbar = Snackbar.make(view, text, duration);
//        View sbView = snackbar.getView();
//        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
//        textView.setTextColor(Color.WHITE);
//        textView.setTextSize(12);
//        snackbar.setAction("OK", new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                snackbar.dismiss();
//            }
//        });
//        snackbar.show();

        new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Alert")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                .show();

    }

    public static void show(View view, int res, int duration) {
        show(view, App.getContext().getResources().getString(res), duration);
    }

    public static void show(View view, String text) {
        show(view, text, Snackbar.LENGTH_LONG);
    }

    public static void show(View view, int res) {
        show(view, App.getContext().getResources().getString(res));
    }
}
