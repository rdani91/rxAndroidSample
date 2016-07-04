package hu.rozsa.daniel.rxandroidsample.binding;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;

import java.util.concurrent.TimeUnit;

import hu.rozsa.daniel.rxandroidsample.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BindingFragment extends Fragment {
    private View btnNormalToast, btnRxToast;
    private EditText etNormalSearch, etRxSearch;
    private TextView tvReverseString;
    private Context context;

    private EditText etPassword, etUserName;
    private View imgPasswordError, imgUsernameError;
    private View btnSend;

    private long clickTime, etTime;
    private int count = 0;
    private Handler h = new Handler();

    public static BindingFragment newInstance() {

        Bundle args = new Bundle();

        BindingFragment fragment = new BindingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUiElements(view);

        setUpButtons();
        setUpEditTexts();

        setUpRegisterForm();

    }

    private void setUpRegisterForm() {
        //TODO task: we want to show error image on username/password field if requirements not proper
        //enable send button if both is ok

        Observable<Boolean> userNameObservable = RxTextView.afterTextChangeEvents(etUserName)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map(TextViewAfterTextChangeEvent::editable)
                .map(editable -> editable.length() > 4);

        Observable<Boolean> passwordObservable = RxTextView.afterTextChangeEvents(etPassword)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map(TextViewAfterTextChangeEvent::editable)
                .map(editable -> editable.length() > 8 && containsUpperCaseLetter(editable));

        userNameObservable
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(suitableInput -> {
                    imgUsernameError.setVisibility(suitableInput ? View.INVISIBLE : View.VISIBLE);
                });

        passwordObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(suitableInput -> {
                    imgPasswordError.setVisibility(suitableInput ? View.INVISIBLE : View.VISIBLE);
                });

        Observable.combineLatest(userNameObservable, passwordObservable, (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isEnabled -> {
                    btnSend.setAlpha(isEnabled ? 1f : 0.3f);
                });
    }

    private boolean containsUpperCaseLetter(Editable editable) {
        for (int i = 0; i < editable.length(); i++) {
            char currentChar = editable.charAt(i);
            if (currentChar >= 'A' && currentChar <= 'Z') {
                return true;
            }
        }
        return false;
    }

    private void setUpEditTexts() {
        //TODO task: after 3 seconds of typing and at least 5 character typed,
        // we want to show the reversed string in the textView

        //normal solution
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etTime = System.currentTimeMillis();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                h.postDelayed(() -> {
                    if (editable.length() > 5 && System.currentTimeMillis() - etTime > 3000) {
                        tvReverseString.setText(reverseString(editable));
                    }
                }, 3500);
            }
        };
        etNormalSearch.addTextChangedListener(watcher);


        //rx solution
        RxTextView.afterTextChangeEvents(etRxSearch)
                .debounce(3000, TimeUnit.MILLISECONDS)
                .filter(textViewAfterTextChangeEvent ->
                        textViewAfterTextChangeEvent.editable().length() > 5)
                .map(event -> reverseString(event.editable()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reversedString -> {
                    tvReverseString.setText(reversedString); //
                });

    }

    @NonNull
    private String reverseString(Editable editable) {
        return new StringBuilder(editable).reverse().toString();
    }

    private void setUpButtons() {
        //TODO task: if we clicks 5 times in 1 secs, shows a toast

        //"normal" solution
        btnNormalToast.setOnClickListener(view1 -> {
            if (count == 0) {
                clickTime = System.currentTimeMillis();
                h.postDelayed(() -> count = 0, 1000);
            }
            count++;
            if (count == 5) {
                if (clickTime - System.currentTimeMillis() < 1000) {
                    createToast();
                }
                count = 0;
            }
        });

        //rx solution
        RxView.clicks(btnRxToast)
                .buffer(1000, TimeUnit.MILLISECONDS)
                .filter(voids -> voids.size() >= 4)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> createToast());
    }

    private void createToast() {
        Toast.makeText(context, "Well done", Toast.LENGTH_SHORT).show();
    }

    private void loadUiElements(View view) {
        btnNormalToast = view.findViewById(R.id.btnNormalToast);
        btnRxToast = view.findViewById(R.id.btnRxToast);

        etNormalSearch = (EditText) view.findViewById(R.id.etNormalSearch);
        etRxSearch = (EditText) view.findViewById(R.id.etRxSearch);
        tvReverseString = (TextView) view.findViewById(R.id.tvReverseString);

        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etUserName = (EditText) view.findViewById(R.id.etUserName);

        imgPasswordError = view.findViewById(R.id.imgPasswordError);
        imgUsernameError = view.findViewById(R.id.imgUserNameError);
        btnSend = view.findViewById(R.id.btnSend);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_binding, container, false);
    }
}
