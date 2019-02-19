package com.example.newbiechen.ireader.utils;

import android.support.v4.util.Pair;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.event.RecommendBookEvent;
import com.example.newbiechen.ireader.model.bean.CommentBean;
import com.example.newbiechen.ireader.model.bean.DetailBean;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by newbiechen on 17-4-29.
 */

public class RxUtils {

    public static <T> SingleSource<T> toSimpleSingle(Single<T> upstream){
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableSource<T> toSimpleSingle(Observable<T> upstream){
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T,R> Pair<T,R> createPair(T first, R second){
        return new Pair<>(first, second);
    }

    public static <T> Single<DetailBean<T>> toCommentDetail(Single<T> detailSingle,
                                                Single<List<CommentBean>> bestCommentsSingle,
                                                Single<List<CommentBean>> commentsSingle){
        return Single.zip(detailSingle, bestCommentsSingle, commentsSingle,
                (t, commentBeen, commentBeen2) -> new DetailBean<T>(t,commentBeen,commentBeen2));
    }
}
