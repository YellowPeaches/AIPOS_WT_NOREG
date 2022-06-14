package com.wintec.aiposui.callback;

import com.wintec.aiposui.model.GoodsModel;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class GoodsModelDiffCallBack extends DiffUtil.Callback {

    private List<GoodsModel> oldList;
    private List<GoodsModel> newList;

    public GoodsModelDiffCallBack(List<GoodsModel> oldList, List<GoodsModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getScalesCode().equals(newList.get(newItemPosition).getScalesCode());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}
