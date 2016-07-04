package hu.rozsa.daniel.rxandroidsample;

import android.os.SystemClock;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import hu.rozsa.daniel.rxandroidsample.pulltorefresh.SampleUser;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

public class FakeRestPlugin {

    public static final FakeRestPlugin INSTANCE = new FakeRestPlugin();

    private ReplaySubject<Long> replaySubject;
    private BehaviorSubject<Long> behaviorSubject;
    private PublishSubject<Long> publishSubject;

    private ReplaySubject<SampleUser> sampleUserObservable;

    private FakeRestPlugin() {
        publishSubject = PublishSubject.create();
        behaviorSubject = BehaviorSubject.create();
        replaySubject = ReplaySubject.create();

        startEmitting();
    }

    public static FakeRestPlugin getInstance() {
        return INSTANCE;
    }

    private void startEmitting() {
        Observable.interval(2500, TimeUnit.MILLISECONDS).subscribe(aLong -> {

            publishSubject.onNext(aLong);
            behaviorSubject.onNext(aLong);
            replaySubject.onNext(aLong);
        });
    }

    public Observable<Long> getPublishObservable() {
        return publishSubject;
    }


    public Observable<Long> getBehaviourSubject() {
        return behaviorSubject;
    }

    public Observable<Long> getReplaySubject() {
        return replaySubject;
    }

    public Observable<Long> getLatestLong() {
        return publishSubject;
    }

    private void createFakeUser(int userId) {
        SampleUser fakeUser = new SampleUser();
        fakeUser.userId = userId;

        String userIdAsString = String.valueOf(userId);
        int age = 0;
        String fakeName = "";
        for (int i = 0; i < userIdAsString.length(); i++) {
            char c = userIdAsString.charAt(i);
            Integer digit = Integer.valueOf(String.valueOf(c));
            age += digit;
            Random r = new Random();
            digit = (r.nextInt(5) + 1) * digit;
            fakeName = fakeName.concat(String.valueOf((char) ('A' + digit)));
        }

        fakeUser.userName = fakeName;
        fakeUser.userAge = age > 80 ? 80 : age;
        sampleUserObservable.onNext(fakeUser);
    }

    public Observable<SampleUser> getUserObservable() {
        if (sampleUserObservable == null) {
            sampleUserObservable = ReplaySubject.create();
        }
        populateFakeUsers();

        return sampleUserObservable;
    }

    public void getNextUsers(){
        populateFakeUsers();
    }


    private void populateFakeUsers() {
        Random random = new Random();
        new Thread(() -> {
            for (int i = 0; i < 2; i++) {
                SystemClock.sleep(2000);
                createFakeUser(random.nextInt(12000));
            }
        }).start();
    }
}
