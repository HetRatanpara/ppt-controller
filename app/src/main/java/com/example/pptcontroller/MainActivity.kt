package com.example.pptcontroller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {


    private lateinit var txtStatus: TextView
    private lateinit var btnScanQR: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private lateinit var btnStart: Button
    private lateinit var btnExit: Button

    private var serverIp: String? = null
    private var serverPort: Int = 12345 // Default port

    private val executor = Executors.newSingleThreadExecutor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtStatus = findViewById(R.id.txtStatus)
        btnScanQR = findViewById(R.id.btnScanQR)
        btnNext = findViewById(R.id.btnNext)
        btnPrev = findViewById(R.id.btnPrev)
        btnStart = findViewById(R.id.btnStart)
        btnExit = findViewById(R.id.btnExit)

        btnScanQR.setOnClickListener { scanQRCode() }
        btnNext.setOnClickListener { sendCommand("next") }
        btnPrev.setOnClickListener { sendCommand("prev") }
        btnStart.setOnClickListener { sendCommand("start") }
        btnExit.setOnClickListener { sendCommand("exit") }

        // Initially disable buttons until QR is scanned
        setButtonsEnabled(false)
    }

    private fun scanQRCode() {
        val options = ScanOptions()
        options.setPrompt("Scan the QR Code to connect")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setBarcodeImageEnabled(true)

        qrCodeLauncher.launch(options)
    }

    private val qrCodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedData = result.contents
            Toast.makeText(this, "Scanned: $scannedData", Toast.LENGTH_LONG).show()

            val parts = scannedData.split(":")
            if (parts.size == 2) {  
                serverIp = parts[0]
                serverPort = parts[1].toInt()
                txtStatus.text = "Connected to $serverIp:$serverPort"
                setButtonsEnabled(true)
            } else {
                txtStatus.text = "Invalid QR Code!"
            }
        } else {
            Toast.makeText(this, "Scan Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendCommand(command: String) {
        if (serverIp == null) {
            Toast.makeText(this, "Scan QR Code first!", Toast.LENGTH_SHORT).show()
            return
        }

        executor.execute {
            try {
                Socket(serverIp, serverPort).use { socket ->
                    PrintWriter(socket.getOutputStream(), true).use { out ->
                        out.println(command)
                    }
                }
                runOnUiThread {
                    Toast.makeText(this, "Command Sent: $command", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Failed to connect!", Toast.LENGTH_SHORT).show()
                }
                Log.e("MainActivity", "Error: ${e.message}")
            }
        }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        btnNext.isEnabled = enabled
        btnPrev.isEnabled = enabled
        btnStart.isEnabled = enabled
        btnExit.isEnabled = enabled
    }
}