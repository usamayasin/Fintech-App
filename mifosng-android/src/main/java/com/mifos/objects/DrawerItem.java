package com.mifos.objects;

import android.view.ViewGroup;

import com.mifos.mifosxdroid.adapters.DrawerAdapter;

/**
 * Created by yarolegovich on 25.03.2017.
 */

public abstract class DrawerItem<T extends DrawerAdapter.ViewHolder> {

    protected boolean isChecked;

    public abstract T createViewHolder(ViewGroup parent);

    public abstract void bindViewHolder(T holder);

    public DrawerItem<T> setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean isSelectable() {
        return true;
    }

}
