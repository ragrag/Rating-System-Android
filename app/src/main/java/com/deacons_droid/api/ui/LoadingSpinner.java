package com.deacons_droid.api.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.deacons_droid.R;

/**
 * Created by Raggi on 8/25/2017.
 */

public class LoadingSpinner {
    public static Dialog Spinner(Context mContext){
        Dialog pd = new Dialog(mContext, android.R.style.Theme_Black);
        View view = LayoutInflater.from(mContext).inflate(R.layout.aux_progress_spinner, null);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.setContentView(view);
        return pd;
    }
}
