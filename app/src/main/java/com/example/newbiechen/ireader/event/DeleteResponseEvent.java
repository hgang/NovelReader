package com.example.newbiechen.ireader.event;

import com.example.newbiechen.ireader.model.bean.FavoriteBookBean;

/**
 * Created by newbiechen on 17-5-27.
 */

public class DeleteResponseEvent {
    public boolean isDelete;
    public FavoriteBookBean favoriteBook;
    public DeleteResponseEvent(boolean isDelete,FavoriteBookBean favoriteBook){
        this.isDelete = isDelete;
        this.favoriteBook = favoriteBook;
    }
}
