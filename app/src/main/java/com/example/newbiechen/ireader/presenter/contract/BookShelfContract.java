package com.example.newbiechen.ireader.presenter.contract;

import com.example.newbiechen.ireader.model.bean.FavoriteBookBean;
import com.example.newbiechen.ireader.ui.base.BaseContract;

import java.util.List;

/**
 * Created by newbiechen on 17-5-8.
 */

public interface BookShelfContract {

    interface View extends BaseContract.BaseView{
        void finishRefresh(List<FavoriteBookBean> favoriteBookBeans);
        void finishUpdate();
        void showErrorTip(String error);
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void refreshFavoriteBooks();
        void createDownloadTask(FavoriteBookBean favoriteBookBean);
        void updateFavoriteBooks(List<FavoriteBookBean> favoriteBookBeans);
        void loadRecommendBooks(String gender);
    }
}
