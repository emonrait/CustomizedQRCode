package com.example.customizedqrcode

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.argb
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        val qrImageView: ImageView = findViewById(R.id.qr_image_view) as ImageView
        //qrImageView.setImageBitmap(generateQRBitMap("a"))
        qrImageView.setImageBitmap(createQRGradientImage("a", 800, 800))
    }

    private fun generateQRBitMap(content: String): Bitmap? {
        val hints: MutableMap<EncodeHintType, ErrorCorrectionLevel?> = HashMap()
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLUE else Color.WHITE)
                }
            }
            return bmp
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    fun createQRGradientImage(url: String?, width: Int, height: Int): Bitmap? {
        try {
            // Determine the legality of the URL
            if (url == null || "" == url || url.length < 1) {
                return null
            }
            val hints = Hashtable<EncodeHintType, Any>()
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
            hints.put(EncodeHintType.MARGIN, 0)
            // Image data conversion, using matrix conversion
            val bitMatrix = QRCodeWriter().encode(
                url,
                BarcodeFormat.QR_CODE, width, height, hints
            )
            val pixels = IntArray(width * height)

            // Gradient color draw from left to right
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (bitMatrix.get(x, y)) {
                        val red = (56 - (56.0 - 14.0) / height * (y + 1)).toInt()
                        val green = (247 - (247.0 - 145.0) / height * (y + 1)).toInt()
                        val blue = (195 - (195.0 - 79.0) / height * (y + 1)).toInt()
                        val colorInt = argb(255, red, green, blue)
                        // Modify the color of the QR code, you can separately develop the color of the QR code and background
                        pixels[x * height + y] =
                            if (bitMatrix.get(x, y)) colorInt else 16777215// 0x000000:0xffffff
                    } else {
                        //Pixels[x * height + y] = 0x00ffffff// background color
                        Color.WHITE
                    }
                }
            }

            // Generate the format of the QR code image, using ARGB_8888
            val bitmap = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

}