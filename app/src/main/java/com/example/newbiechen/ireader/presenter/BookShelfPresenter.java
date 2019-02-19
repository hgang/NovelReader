package com.example.newbiechen.ireader.presenter;

import com.example.newbiechen.ireader.RxBus;
import com.example.newbiechen.ireader.model.bean.BookChapterBean;
import com.example.newbiechen.ireader.model.bean.BookDetailBean;
import com.example.newbiechen.ireader.model.bean.FavoriteBookBean;
import com.example.newbiechen.ireader.model.bean.DownloadTaskBean;
import com.example.newbiechen.ireader.model.local.BookRepository;
import com.example.newbiechen.ireader.model.remote.RemoteRepository;
import com.example.newbiechen.ireader.presenter.contract.BookShelfContract;
import com.example.newbiechen.ireader.ui.base.RxPresenter;
import com.example.newbiechen.ireader.utils.Constant;
import com.example.newbiechen.ireader.utils.LogUtils;
import com.example.newbiechen.ireader.utils.MD5Utils;
import com.example.newbiechen.ireader.utils.RxUtils;
import com.example.newbiechen.ireader.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by newbiechen on 17-5-8.
 */

public class BookShelfPresenter extends RxPresenter<BookShelfContract.View>
        implements BookShelfContract.Presenter {
    private static final String TAG = "BookShelfPresenter";

    @Override
    public void refreshFavoriteBooks() {
        List<FavoriteBookBean> favoriteBooks = BookRepository
                .getInstance().getFavoriteBooks();
        mView.finishRefresh(favoriteBooks);
    }

    @Override
    public void createDownloadTask(FavoriteBookBean favoriteBookBean) {
        DownloadTaskBean task = new DownloadTaskBean();
        task.setTaskName(favoriteBookBean.getTitle());
        task.setBookId(favoriteBookBean.get_id());
        task.setBookChapters(favoriteBookBean.getBookChapters());
        task.setLastChapter(favoriteBookBean.getBookChapters().size());

        RxBus.getInstance().post(task);
    }


    @Override
    public void loadRecommendBooks(String gender) {
        Disposable disposable = RemoteRepository.getInstance()
                .getRecommendBooks(gender)
                .doOnSuccess(new Consumer<List<FavoriteBookBean>>() {
                    @Override
                    public void accept(List<FavoriteBookBean> favoriteBooks) throws Exception{
                        //更新目录
                        updateCategory(favoriteBooks);
                        //异步存储到数据库中
                        BookRepository.getInstance()
                                .saveFavoriteBooksWithAsync(favoriteBooks);
                    }
                })
                .compose(RxUtils::toSimpleSingle)
                .subscribe(
                        beans -> {
                            mView.finishRefresh(beans);
                            mView.complete();
                        },
                        (e) -> {
                            //提示没有网络
                            LogUtils.e(e);
                            mView.showErrorTip(e.toString());
                            mView.complete();
                        }
                );
        addDisposable(disposable);
    }


    //需要修改
    @Override
    public void updateFavoriteBooks(List<FavoriteBookBean> favoriteBookBeans) {
        if (favoriteBookBeans == null || favoriteBookBeans.isEmpty()) return;
        List<FavoriteBookBean> favoriteBooks = new ArrayList<>(favoriteBookBeans);
        List<Single<BookDetailBean>> observables = new ArrayList<>(favoriteBooks.size());
        Iterator<FavoriteBookBean> it = favoriteBooks.iterator();
        while (it.hasNext()){
            FavoriteBookBean favoriteBook = it.next();
            //删除本地文件
            if (favoriteBook.isLocal()) {
                it.remove();
            }
            else {
                observables.add(RemoteRepository.getInstance()
                        .getBookDetail(favoriteBook.get_id()));
            }
        }
        //zip可能不是一个好方法。
        Single.zip(observables, new Function<Object[], List<FavoriteBookBean>>() {
            @Override
            public List<FavoriteBookBean> apply(Object[] objects) throws Exception {
                List<FavoriteBookBean> newFavoriteBooks = new ArrayList<FavoriteBookBean>(objects.length);
                for (int i=0; i<favoriteBooks.size(); ++i){
                    FavoriteBookBean oldFavoriteBook = favoriteBooks.get(i);
                    FavoriteBookBean newFavoriteBook = ((BookDetailBean)objects[i]).getFavoriteBookBean();
                    //如果是oldBook是update状态，或者newFavoriteBook与oldBook章节数不同
                    if (oldFavoriteBook.isUpdate() ||
                            !oldFavoriteBook.getLastChapter().equals(newFavoriteBook.getLastChapter())){
                        newFavoriteBook.setUpdate(true);
                    }
                    else {
                        newFavoriteBook.setUpdate(false);
                    }
                    newFavoriteBook.setLastRead(oldFavoriteBook.getLastRead());
                    newFavoriteBooks.add(newFavoriteBook);
                    //存储到数据库中
                    BookRepository.getInstance()
                            .saveFavoriteBooks(newFavoriteBooks);
                }
                return newFavoriteBooks;
            }
        })
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<List<FavoriteBookBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onSuccess(List<FavoriteBookBean> value) {
                        //跟原先比较
                        mView.finishUpdate();
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //提示没有网络
                        mView.showErrorTip(e.toString());
                        mView.complete();
                        LogUtils.e(e);
                    }
                });
    }

    //更新每个FavoriteBook的目录
    private void updateCategory(List<FavoriteBookBean> favoriteBookBeans){
        List<Single<List<BookChapterBean>>> observables = new ArrayList<>(favoriteBookBeans.size());
        for (FavoriteBookBean bean : favoriteBookBeans){
            observables.add(
                    RemoteRepository.getInstance().getBookChapters(bean.get_id())
            );
        }
        Iterator<FavoriteBookBean> it = favoriteBookBeans.iterator();
        //执行在上一个方法中的子线程中
        Single.concat(observables)
                .subscribe(
                        chapterList -> {

                            for (BookChapterBean bean : chapterList){
                                bean.setId(MD5Utils.strToMd5By16(bean.getLink()));
                            }

                            FavoriteBookBean bean = it.next();
                            bean.setLastRead(StringUtils.
                                    dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
                            bean.setBookChapters(chapterList);
                        }
                );
    }
}
