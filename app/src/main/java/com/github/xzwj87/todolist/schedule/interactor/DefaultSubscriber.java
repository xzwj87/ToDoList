package com.github.xzwj87.todolist.schedule.interactor;


public class DefaultSubscriber<T> extends rx.Subscriber<T> {

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {}

    @Override
    public void onNext(T t) {}

}
