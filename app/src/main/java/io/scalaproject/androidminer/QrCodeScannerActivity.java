// Copyright (c) 2021 Scala
//
// Please see the included LICENSE file for more information.

package io.scalaproject.androidminer;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class QrCodeScannerActivity extends AppCompatActivity implements BarcodeRetriever {

    public TextView scanResult;
    BarcodeCapture barcodeCapture;
    public static final String XLA_SCHEME = "scala:";

    @Override
    protected void onDestroy() {
        try{
            super.onDestroy();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
        assert barcodeCapture != null;
        barcodeCapture.setRetrieval(this);

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    barcodeCapture.stopScanning();
                   finish();
                } catch (Exception ignored) {

                }
            }
        });

        scanResult = findViewById(R.id.scanResult);

        barcodeCapture.setShowDrawRect(true)
                .setSupportMultipleScan(false)
                .setTouchAsCallback(true)
                .shouldAutoFocus(true)
                .setShowFlash(false)
                .setBarcodeFormat(Barcode.ALL_FORMATS)
                .setCameraFacing(CameraSource.CAMERA_FACING_BACK)
                .setShouldShowText(false);
        try{
            barcodeCapture.refresh();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onRetrieved(final Barcode barcode) {

        String miner = barcode.displayValue;
        if(miner.startsWith(XLA_SCHEME)) {
            miner = miner.substring(XLA_SCHEME.length());
        }

        scanResult.setText("Scala Wallet Address: " + miner);
        if(Utils.verifyAddress(miner)) {
            Log.d("CONSOLE:QRCODE", "Barcode read: " + barcode.displayValue);

            Config.write(Config.CONFIG_ADDRESS, miner);
            try{
                barcodeCapture.stopScanning();
                finish();
            } catch (Exception ignored) {

            }

            return;
        }

        Utils.showToast(MainActivity.contextOfApplication, "Invalid scala address", Toast.LENGTH_SHORT);
    }

    @Override
    public void onRetrievedMultiple(final Barcode closetToClick, final List<BarcodeGraphic> barcodeGraphics) {
        final StringBuilder message = new StringBuilder();
        message.append("Code selected : ");
        message.append(closetToClick.displayValue);
        message.append("\n\nother ");
        message.append("codes in frame include : \n");

        for (int index = 0; index < barcodeGraphics.size(); index++) {
            Barcode barcode = barcodeGraphics.get(index).getBarcode();
            message.append(index + 1).append(". ").append(barcode.displayValue).append("\n");
        }
        Log.d("CONSOLE:QRCODE:MULTIPLE", message.toString());
    }
    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        // when image is scanned and processed
        for (int i = 0; i < sparseArray.size(); i++) {
            Barcode barcode = sparseArray.valueAt(i);
            Log.d("CONSOLE:QRCODE:VALUED", barcode.displayValue);
        }
    }

    @Override
    public void onRetrievedFailed(String reason) {
        // in case of failure

        Log.e("CONSOLE:QRCODE:FAIL", reason);
    }

    @Override
    public void onPermissionRequestDenied(){
        Log.e("CONSOLE:QRCODE:FAIL", "DENIED");
    }
}
