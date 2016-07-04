package hu.rozsa.daniel.rxandroidsample.subject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class SubjectFragment extends Fragment {

    private ListView behaviourListView, publishListView, replayListView;
    private SampleListAdapter behaviourAdapter, publishAdapter, replayAdapter;
    private List<SubjectObject> behaviourItems, publishItems, replayItems;
    private Subscription subjectSubscription;
    private Subscription replaySubscription;
    private Subscription publishSubscription;

    public static Fragment newInstance() {
        return new SubjectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subject, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();

        subjectSubscription.unsubscribe(); //possible memory leak if not unsubscribing from observable
        replaySubscription.unsubscribe();
        publishSubscription.unsubscribe();

        behaviourItems.clear();
        publishItems.clear();
        replayItems.clear();
        behaviourItems = null;
        publishItems = null;
        behaviourItems = null;
    }

    @Override
    public void onResume() {
        super.onResume();


        subjectSubscription = FakeRestPlugin.getInstance()
                .getBehaviourSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong1 -> {
                    behaviourItems.add(new SubjectObject(aLong1));
                    behaviourAdapter.notifyDataSetChanged();
                });

        publishSubscription = FakeRestPlugin.getInstance()
                .getPublishObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    publishItems.add(new SubjectObject(aLong));
                    publishAdapter.notifyDataSetChanged();
                });

        replaySubscription = FakeRestPlugin.getInstance()
                .getReplaySubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    replayItems.add(new SubjectObject(aLong));
                    replayAdapter.notifyDataSetChanged();

                });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUiElements(view);
        behaviourItems = new ArrayList<>();
        publishItems = new ArrayList<>();
        replayItems = new ArrayList<>();
        setAdapters();
    }

    private void setAdapters() {
        behaviourAdapter = new SampleListAdapter(behaviourItems);
        publishAdapter = new SampleListAdapter(publishItems);
        replayAdapter = new SampleListAdapter(replayItems);

        behaviourListView.setAdapter(behaviourAdapter);
        publishListView.setAdapter(publishAdapter);
        replayListView.setAdapter(replayAdapter);
    }

    private void loadUiElements(View contentView) {
        behaviourListView = (ListView) contentView.findViewById(R.id.lvBehaviour);
        publishListView = (ListView) contentView.findViewById(R.id.lvPublish);
        replayListView = (ListView) contentView.findViewById(R.id.lvReplay);
    }
}

