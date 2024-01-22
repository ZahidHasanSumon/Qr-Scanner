package com.zahid.qrscanner;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;



public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private DecoratedBarcodeView scannerView;
    private TextView resultTextView;
    private Button scanAgainButton;
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scannerView = findViewById(R.id.cameraView);
        resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setVisibility(View.GONE);
        scanAgainButton = findViewById(R.id.scanAgainButton);



        scanAgainButton.setOnClickListener(view -> {
            // Hide resultTextView and scanAgainButton
            resultTextView.setVisibility(View.GONE);
            //scanAgainButton.setVisibility(View.GONE);
            scanAgainButton.setVisibility(View.GONE);

            // Resume scanning
            startScanning();
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startScanning();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE
            );
        }
    }


    private void startScanning() {
        if (!isScanning) {
            isScanning = true;
            scannerView.decodeSingle(new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    // Handle the result
                    String scannedText = result.getText();
                    resultTextView.setText(result.getText());
                    resultTextView.setVisibility(View.VISIBLE);
                    isScanning = false;
                    scanAgainButton.setVisibility(View.VISIBLE);

                    if(isUrl(scannedText)){
                        resultTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blue));

                    } else {
                        // It's not a URL, change text color to default color
                        resultTextView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.white));
                    }

                        // It's a URL, open it in a browser or perform other actions
                        resultTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(isUrl(scannedText)) openUrl(scannedText);

                            }
                        });


                }


                @Override
                public void possibleResultPoints(List resultPoints) {
                    // Optional: Handle possible result points
                }
            });
        }
    }
    private boolean isUrl(String text) {
        // Simple check for URL format
        return text != null && (text.startsWith("http://") || text.startsWith("https://"));
    }

    private void openUrl(String url) {
        // Open the URL in a browser or perform other actions
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Log.e("QRCodeScanner", "Camera permission denied");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.pause();
    }
}