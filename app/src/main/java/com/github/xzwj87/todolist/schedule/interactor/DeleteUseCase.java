package com.github.xzwj87.todolist.schedule.interactor;


import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public abstract class DeleteUseCase {
    private Subscription mSubscription = Subscriptions.empty();

    protected abstract Observable buildUseCaseObservable(long id);

    @SuppressWarnings("unchecked")
    public void execute(long id, Subscriber UseCaseSubscriber) {
        this.mSubscription = this.buildUseCaseObservable(id)
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
