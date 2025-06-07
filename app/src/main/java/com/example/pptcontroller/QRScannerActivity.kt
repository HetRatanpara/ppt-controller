package com.example.pptcontroller

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class QRScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start QR Code scanning
        startQRScanner()
    }

    private fun startQRScanner() {
        val options = ScanOptions()
        options.setPrompt("Scan a QR code")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setBarcodeImageEnabled(true)

        qrCodeLauncher.launch(options)
    }

    private val qrCodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedData = result.contents
            Toast.makeText(this, "Scanned: $scannedData", Toast.LENGTH_LONG).show()

            // Send result back to MainActivity
            val intent = Intent()
            intent.putExtra("SCANNED_RESULT", scannedData)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "Scan Canceled", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
