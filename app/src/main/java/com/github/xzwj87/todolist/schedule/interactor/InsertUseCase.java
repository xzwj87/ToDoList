package com.github.xzwj87.todolist.schedule.interactor;


import android.content.ContentValues;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public abstract class InsertUseCase {
    private Subscription mSubscription = Subscriptions.empty();

    protected abstract Observable buildUseCaseObservable(ContentValues data);

    @SuppressWarnings("unchecked")
    public void execute(ContentValues data, Subscriber UseCaseSubscriber) {
        this.mSubscription = this.buildUseCaseObservable(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(UseCaseSubscriber);
    }

    public void unsubscribe() {
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
