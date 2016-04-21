package com.github.xzwj87.todolist.schedule.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.xzwj87.todolist.schedule.internal.di.HasComponent;


public abstract class BaseFragment extends Fragment {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>)getActivity()).getComponent());
    }

}
