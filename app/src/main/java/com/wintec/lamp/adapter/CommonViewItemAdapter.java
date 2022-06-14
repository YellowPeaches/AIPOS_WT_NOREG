package com.wintec.lamp.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import androidx.annotation.LayoutRes;

/**
 * @描述：
 * @文件名: Desktop.CommonViewItemAdapter
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/6/16 14:06
 */
public abstract class CommonViewItemAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public CommonViewItemAdapter(@LayoutRes int layoutResId, List<T> data) {
        super(layoutResId, data);
    }

    public CommonViewItemAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
    }

}
