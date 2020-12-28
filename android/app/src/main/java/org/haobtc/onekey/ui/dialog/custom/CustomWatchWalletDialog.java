package org.haobtc.onekey.ui.dialog.custom;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BottomPopupView;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.haobtc.onekey.R;
import org.haobtc.onekey.utils.NoLeakHandler;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CustomWatchWalletDialog extends BottomPopupView implements NoLeakHandler.HandlerCallback {
    private ImageView qrImg;
    private String url;
    private TextView qrExceptionTV;
    private RelativeLayout closeLayout;
    private NoLeakHandler handler;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public CustomWatchWalletDialog (@NonNull Context context, String url) {
        super(context);
        this.url = url;
        handler = new NoLeakHandler(this::handleMessage);
    }

    @Override
    protected void onCreate () {
        super.onCreate();
        closeLayout = findViewById(R.id.close_layout);
        qrExceptionTV = findViewById(R.id.qr_tv);
        qrImg = findViewById(R.id.qr_img);
        startDownBitMap();
        closeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                dismiss();
            }
        });
    }

    private void startDownBitMap () {
        final Observable<Bitmap> observable = io.reactivex.rxjava3.core.Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe (@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Bitmap> emitter) throws Throwable {
                Bitmap bitmap = CodeCreator.createQRCode(url, 250, 250, null);
                emitter.onNext(bitmap);
            }
        });
        io.reactivex.rxjava3.observers.DisposableObserver<Bitmap> disposableObserver = new DisposableObserver<Bitmap>() {
            @Override
            public void onNext (@io.reactivex.annotations.NonNull Bitmap bitmap) {
                qrExceptionTV.setVisibility(GONE);
                qrImg.setImageBitmap(bitmap);
            }

            @Override
            public void onError (@io.reactivex.annotations.NonNull Throwable e) {
                qrExceptionTV.setVisibility(VISIBLE);
            }

            @Override
            public void onComplete () {
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    @Override
    protected int getImplLayoutId () {
        return R.layout.dialog_watch_wallet_qr;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    @Override
    public void handleMessage (Message msg) {
    }

}

