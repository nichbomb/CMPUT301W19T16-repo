package ca.rededaniskal.Barcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import ca.rededaniskal.R;

public class Barcode_Reader_Activity extends AppCompatActivity implements Barcode_Reader_Fragment.BarcodeReaderListener {
    public static String KEY_CAPTURED_BARCODE = "key_captured_barcode";
    public static String KEY_CAPTURED_RAW_BARCODE = "key_captured_raw_barcode";
    private static final String KEY_AUTO_FOCUS = "key_auto_focus";
    private static final String KEY_USE_FLASH = "key_use_flash";
    private boolean autoFocus = false;
    private boolean useFlash = false;
    private Barcode_Reader_Fragment mBarcodeReaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);

        final Intent intent = getIntent();
        if (intent != null) {
            autoFocus = intent.getBooleanExtra(KEY_AUTO_FOCUS, false);
            useFlash = intent.getBooleanExtra(KEY_USE_FLASH, false);
        }
        mBarcodeReaderFragment = attachBarcodeReaderFragment();
    }

    private Barcode_Reader_Fragment attachBarcodeReaderFragment() {
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        Barcode_Reader_Fragment fragment = Barcode_Reader_Fragment.newInstance(autoFocus, useFlash);
        fragment.setListener(this);
        fragmentTransaction.replace(R.id.fm_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        return fragment;
    }

    public static Intent getLaunchIntent(Context context, boolean autoFocus, boolean useFlash) {
        Intent intent = new Intent(context, Barcode_Reader_Activity.class);
        intent.putExtra(KEY_AUTO_FOCUS, autoFocus);
        intent.putExtra(KEY_USE_FLASH, useFlash);
        return intent;
    }

    @Override
    public void onScanned(Barcode barcode) {
        if (mBarcodeReaderFragment != null) {
            mBarcodeReaderFragment.pauseScanning();
        }
        if (barcode != null) {
            Intent intent = new Intent();
            intent.putExtra(KEY_CAPTURED_BARCODE, barcode);
            intent.putExtra(KEY_CAPTURED_RAW_BARCODE, barcode.rawValue);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }
}
