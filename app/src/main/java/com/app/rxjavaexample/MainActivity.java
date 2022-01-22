package com.app.rxjavaexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.observables.ConnectableObservable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();
    private CompositeDisposable compositeDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        observableUsingJust();
//        observableUsingIterable();
//        observableUsingEmitter();
//        createConnectableObservable();
//        createErrorObservable();
//        createCallableObservable();
//        createIntervalObservable();
//        mapOperator();
//        filterOperator();
//        doOnSubscribe();
//        doOnComplete();
//        doOnNext();
//        doOnFinally();
//        zipOperator();
//        zipWithOperator();
//        flatMapOperator();
//        returningObservableFromMap();
        concatOperator();
    }

    private void observableUsingJust() {
        Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5, 6);

//        If you pass list as argument in just it will directly emit list as the value in onSuccess() callback of observer
//        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9 ,10);
//        Observable<List<Integer>> observable = Observable.just(list);

        observable.subscribe(observer);
    }


    /**
     * Observable.fromIterable is useful in scenarios when we want to create observable out of an iterable list
     */
    private void observableUsingIterable() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Observable<Integer> observable = Observable.fromIterable(list);

        observable.subscribe(observer);
    }


    private void observableUsingEmitter() {
        Observable<Integer> observable = Observable.create(emitter -> {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            for (int i = 0; i < list.size(); i++)
                emitter.onNext(list.get(i));

            emitter.onComplete();
        });

        observable.subscribe(observer);
    }

    private void createConnectableObservable(){
        ConnectableObservable<Integer> connectableObservable = Observable.just(1, 2, 3).publish();
        connectableObservable.subscribe(observer);
        // from the above line observable still does not emits

        // observable starts emitting after the below line
        connectableObservable.connect();

        // below observer won't be able to receive some updates
        connectableObservable.subscribe(observer2);
    }

    private void createErrorObservable(){
        Observable observable = Observable.error(new Exception("some exception"));
        observable.subscribe(observer);
        observable.subscribe(observer2);
        // In the above case both the observer will get same instance of Exception


        // In the case below both the observers will get separate instance of Exception
        Observable observable2 = Observable.error(() -> {
            Log.e(TAG, "createErrorObservable: created new exception instance");
            throw new Exception("some exception");
        });
        observable2.subscribe(observer);
        observable2.subscribe(observer2);
    }

    private void createCallableObservable(){
//        Observable observable = Observable.just(getNumber());
//        observable.subscribe(observer);
        // above call will crash the app giving divide by zero exception


        // below call will handle the exception properly
        Observable<Integer> observable2 = Observable.fromCallable(() -> {
            Log.i(TAG, "createCallableObservable: ");
            return getNumber();
        });
        observable2.subscribe(observer);
    }

    private int getNumber(){
        return 1/0;
    }

    private void createIntervalObservable(){
        Observable observable = Observable.interval(1000, TimeUnit.MILLISECONDS);
        observable.subscribe(item -> Log.i(TAG, "createIntervalObservable: " + item));
    }

    private void mapOperator(){
        Observable observable = Observable.just(1, 2, 3, 4, 5).map(
                new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Throwable {
                        return "value is: " + integer;
                    }
                }
        );

        observable.subscribe(stringObserver);
    }

    /**
     * output: 2, 4, 6
     * filter operator returns an Observable
     * */
    private void filterOperator(){
        Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5, 6).filter(
                new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Throwable {
                        return integer % 2 == 0;
                    }
                }
        );

        observable.subscribe(observer);
    }

    private void doOnSubscribe(){
        Observable<Integer> observable = Observable.just(1, 3, 5, 7).doOnSubscribe(disposable -> {
            Log.i(TAG, "doOnSubscribe: ");
        });

        observable.subscribe(observer);
        observable.subscribe(observer2);
    }

    private void doOnNext(){
        Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5).doOnNext(item -> {
            Log.i(TAG, "doOnNext: " + item);
            // if we modify item value here it won't affect the value received by the observers
        });
        observable.subscribe(observer);
    }

    private void doOnComplete(){
        Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5).doOnComplete(() -> {
            Log.i(TAG, "doOnComplete: ");
        });
        observable.subscribe(observer);
    }

    private void doOnFinally(){
        Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5).doFinally(() -> {
            Log.i(TAG, "doFinally: ");
        });
        observable.subscribe(observer);
    }

    /**
     * output:
     * odd: 1 even: 2
     * odd: 3 even: 4
     * odd: 5 even: 6
     * odd: 7 even: 8
     * odd: 9 even: 10
     * */
    private void zipOperator(){
        Observable<Integer> oddNumbers = Observable.just(1, 3, 5, 7, 9);
        Observable<Integer> evenNumbers = Observable.just(2, 4, 6, 8, 10);

        Observable<String> combinedObservable = Observable.zip(
                oddNumbers,
                evenNumbers,
                new BiFunction<Integer, Integer, String>() {
                    @Override
                    public String apply(Integer odd, Integer even) throws Throwable {
                        return "odd: " + odd + " even: " + even;
                    }
                }
        );

        combinedObservable.subscribe(stringObserver);
    }

    /**
     * output:
     *      3
     *      7
     *      11
     *      15
     *      19
     * */
    private void zipWithOperator(){
        Observable<Integer> oddNumbers = Observable.just(1, 3, 5, 7, 9);
        Observable<Integer> evenNumbers = Observable.just(2, 4, 6, 8, 10);

        Observable<Integer> observable = oddNumbers.zipWith(evenNumbers, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Throwable {
                return integer + integer2;
            }
        });

        observable.subscribe(observer);
    }

    private void flatMapOperator(){
        Observable<Integer> oddNumberObservable = Observable.just(1, 3, 5, 7, 9);

        Observable<String> stringObservable = oddNumberObservable.flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Throwable {
                return Observable.just(integer + " was odd, after increment by 1: " + (integer + 1) + " is now even");
            }
        });

        stringObservable.subscribe(stringObserver);
    }

    private void returningObservableFromMap(){
        Observable.just(1, 3, 5, 7, 9).map(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Throwable {
                return Observable.just(integer);
            }
        }).subscribe(item -> {
            Log.i(TAG, "returningObservableFromMap: " + item);
        });
    }

    /**
    * output:
    * 1  3 5 7 9 2 4 6 8 10
    * */
    private void concatOperator(){
        Observable<Integer> oddNumbers = Observable.just(1, 3, 5, 7, 9);
        Observable<Integer> evenNumbers = Observable.just(2, 4, 6, 8, 10);

        oddNumbers.concatWith(evenNumbers).subscribe(observer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!compositeDisposable.isDisposed()){
            compositeDisposable.dispose();
        }
    }

    Observer<Integer> observer = new Observer<Integer>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.i(TAG, "onSubscribe: observer");
            compositeDisposable.add(d);
        }

        @Override
        public void onNext(@NonNull Integer integer) {
            Log.i(TAG, "observer: " + integer);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "observer: ", e);
        }

        @Override
        public void onComplete() {
            Log.i(TAG, "observer: completed");
        }
    };

    Observer<Integer> observer2 = new Observer<Integer>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.i(TAG, "onSubscribe: observer2");
            compositeDisposable.add(d);
        }

        @Override
        public void onNext(@NonNull Integer integer) {
            Log.i(TAG, "observer2: " + integer);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "observer2: ", e);
        }

        @Override
        public void onComplete() {
            Log.i(TAG, "observer2: completed");
        }
    };

    Observer<String> stringObserver = new Observer<String>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.i(TAG, "onSubscribe: string observer");
            compositeDisposable.add(d);
        }

        @Override
        public void onNext(@NonNull String string) {
            Log.i(TAG, "observer: " + string);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "observer: ", e);
        }

        @Override
        public void onComplete() {
            Log.i(TAG, "observer: completed");
        }
    };
}