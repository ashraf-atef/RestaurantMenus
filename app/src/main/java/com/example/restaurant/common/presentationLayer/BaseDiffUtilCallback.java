package com.example.restaurant.common.presentationLayer;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public abstract class BaseDiffUtilCallback<T> extends DiffUtil.Callback {
    protected List<T> oldData;
    protected List<T> newData;

    public BaseDiffUtilCallback(List<T> oldData, List<T> newData) {
        this.newData = newData;
        this.oldData = oldData;
    }

    public BaseDiffUtilCallback() {
    }

    public void setLists(List<T> oldData, List<T> newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public int getOldListSize() {
        return oldData.size();
    }

    @Override
    public int getNewListSize() {
        return newData.size();
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return newData;
    }
}
