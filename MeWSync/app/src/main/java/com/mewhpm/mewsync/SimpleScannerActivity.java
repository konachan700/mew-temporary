package com.mewhpm.mewsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import java.util.Collections;

public class SimpleScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    public static final String DATA_ID = "data";
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        mScannerView.setAspectTolerance(0.5f);
        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QRCODE));
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra(DATA_ID, rawResult.getContents());
        setResult(RESULT_OK, intent);
        finish();
    }
}

