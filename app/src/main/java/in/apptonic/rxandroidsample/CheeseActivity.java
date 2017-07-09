

package in.apptonic.rxandroidsample;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class CheeseActivity extends BaseSearchActivity {

    private Disposable mDisposable;

    private io.reactivex.Observable<String> createButtononCLickObservable() {

        return io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

                mSearchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emitter.onNext(mQueryEditText.getText().toString());

                    }

                });

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        mSearchButton.setOnClickListener(null);
                    }
                });
            }

        });
    }


    private Observable<String> createTextChangeObservable() {

        Observable<String> textChangeObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        emitter.onNext(s.toString());

                    }
                };

                mQueryEditText.addTextChangedListener(watcher);

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        mQueryEditText.removeTextChangedListener(watcher);
                    }
                });

            }
        });
        return textChangeObservable.filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return s.length() >= 2;
            }
        }).debounce(1000, TimeUnit.MILLISECONDS);
    }


    @Override
    protected void onStart() {
        super.onStart();

        Observable<String> buttonClickStream = createButtononCLickObservable();
        Observable<String> textChangeStream = createTextChangeObservable();
        Observable<String> searchTextObservable = Observable.merge(textChangeStream, buttonClickStream);

        mDisposable = searchTextObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        showProgressBar();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, List<String>>() {

                    @Override
                    public List<String> apply(String query) {
                        return mCheeseSearchEngine.search(query);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> result) {
                        hideProgressBar();
                        showResult(result);

                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
