package com.github.xzwj87.todolist.schedule.interactor;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public abstract class QueryUseCase {

    private Subscription mSubscription = Subscriptions.empty();

    protected abstract Observable buildUseCaseObservable();

    @SuppressWarnings("unchecked")
    public void execute(Subscriber UseCaseSubscriber) {
        this.mSubscription = this.buildUseCaseObservable()
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
