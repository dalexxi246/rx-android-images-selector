package com.wh2.foss.imageselector.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.wh2.foss.imageselector.R;
import com.wh2.foss.imageselector.databinding.ActivityHostBinding;

import io.reactivex.disposables.CompositeDisposable;

public class HostActivity extends AppCompatActivity {

    ActivityHostBinding activityBinding;
    CompositeDisposable subscriptions = new CompositeDisposable();

    public static final String RETURN_HOST = "host";
    public static final int ACTION_RETRIEVE_HOST = 767;
    
    private boolean hostIsValid;

    public static Intent newIntent(Context context) {
        return new Intent(context, HostActivity.class);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_host);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscriptions.add(
                RxTextView
                        .textChanges(activityBinding.editText)
                        .skipInitialValue()
                        .subscribe(
                                this::activateButton,
                                throwable -> {}
                                )
        );

        subscriptions.add(
                RxView
                        .clicks(activityBinding.button)
                        .subscribe(
                                o -> finish(),
                                throwable -> {}
                        )
        );
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void activateButton(CharSequence hostAddress) {
        hostIsValid = isValidHostAddress(hostAddress);
        activityBinding.textErrorMsg.setText(hostIsValid ? getString(R.string.message_ok) : getString(R.string.message_invalid_ip));
        activityBinding.button.setEnabled(hostIsValid);
    }

    private boolean isValidHostAddress(CharSequence hostAddress) {
        return hostAddress.toString().matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.dispose();
    }

    @Override
    public void finish() {
        if (hostIsValid){
            Intent i = new Intent();
            i.putExtra(RETURN_HOST, String.format("http://%s:3000", activityBinding.editText.getText().toString()));
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }
}
