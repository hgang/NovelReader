package com.example.newbiechen.ireader.event;

import com.example.newbiechen.ireader.model.bean.FavoriteBookBean;

/**
 * Created by newbiechen on 17-5-27.
 */

public class DeleteTaskEvent {
    public FavoriteBookBean favoriteBook;

    public DeleteTaskEvent(FavoriteBookBean favoriteBook){
        this.favoriteBook = favoriteBook;
    }
}
