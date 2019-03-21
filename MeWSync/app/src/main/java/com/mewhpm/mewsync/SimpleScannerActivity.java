package com.mewhpm.mewsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import java.util.Collections;

public class SimpleScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    public static final String DATA_ID = "data";
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setAspectTolerance(0.5f);
        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.setAutoFocus(true);
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra(DATA_ID, Base64.encodeToString(rawResult.getRawBytes(), Base64.DEFAULT));
        setResult(RESULT_OK, intent);
        finish();
    }
}
