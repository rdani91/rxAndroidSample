package hu.rozsa.daniel.rxandroidsample.pulltorefresh;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hu.rozsa.daniel.rxandroidsample.FakeRestPlugin;
import hu.rozsa.daniel.rxandroidsample.R;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PullToRefreshFragment extends Fragment {


    private SwipeRefreshLayout swipeRefreshLayout;
    private UserAdapter adapter;
    private List<SampleUser> users = new ArrayList<>();
    private Subscription userSubscription;


    public static PullToRefreshFragment newInstance() {

        Bundle args = new Bundle();

        PullToRefreshFragment fragment = new PullToRefreshFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        userSubscription.unsubscribe();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        ListView lvUsers = (ListView) view.findViewById(R.id.lvUsers);
        adapter = new UserAdapter(users);
        lvUsers.setAdapter(adapter);
        setUpSwipeRefreshLayout();

        userSubscription = FakeRestPlugin.getInstance()
                .getUserObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sampleUser -> {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    users.add(sampleUser);
                    adapter.notifyDataSetChanged();
                });

        refreshUsers();

    }

    private void refreshUsers() {
        FakeRestPlugin.getInstance()
                .getNextUsers();
    }

    private void setUpSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            refreshUsers();
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pull_to_refresh, container, false);
    }
}
