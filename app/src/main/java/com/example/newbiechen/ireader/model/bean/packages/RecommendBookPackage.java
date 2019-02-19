package com.example.newbiechen.ireader.model.bean.packages;

import com.example.newbiechen.ireader.model.bean.BaseBean;
import com.example.newbiechen.ireader.model.bean.FavoriteBookBean;

import java.util.List;

/**
 * Created by newbiechen on 17-5-8.
 */

public class RecommendBookPackage extends BaseBean {

    private List<FavoriteBookBean> books;

    public List<FavoriteBookBean> getBooks() {
        return books;
    }

    public void setBooks(List<FavoriteBookBean> books) {
        this.books = books;
    }
}
