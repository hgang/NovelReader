package com.example.newbiechen.ireader.ui.adapter;

import com.example.newbiechen.ireader.model.bean.FavoriteBookBean;
import com.example.newbiechen.ireader.ui.adapter.view.FavoriteBookHolder;
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder;
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter;

/**
 * Created by newbiechen on 17-5-8.
 */

public class FavoriteBookAdapter extends WholeAdapter<FavoriteBookBean> {

    @Override
    protected IViewHolder<FavoriteBookBean> createViewHolder(int viewType) {
        return new FavoriteBookHolder();
    }

}
