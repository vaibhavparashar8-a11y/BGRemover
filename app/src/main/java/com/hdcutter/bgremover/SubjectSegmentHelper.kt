package com.hdcutter.bgremover

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SubjectSegmentHelper {

    suspend fun removeBackground(bitmap: Bitmap): Bitmap = suspendCoroutine { continuation ->
        val options = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()
        val segmenter = Segmentation.getClient(options)
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        segmenter.process(inputImage)
            .addOnSuccessListener { mask ->
                try {
                    val result = applyMask(bitmap, mask.buffer, mask.width, mask.height)
                    continuation.resume(result)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    private fun applyMask(
        original: Bitmap,
        maskBuffer: java.nio.ByteBuffer,
        maskWidth: Int,
        maskHeight: Int
    ): Bitmap {
        val width = original.width
        val height = original.height
        maskBuffer.rewind()
        val floatBuffer = maskBuffer.asFloatBuffer()
        val maskValues = FloatArray(maskWidth * maskHeight)
        floatBuffer.get(maskValues)
        val pixels = IntArray(width * height)
        original.getPixels(pixels, 0, width, 0, 0, width, height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val maskX = (x.toFloat() * maskWidth / width).toInt().coerceIn(0, maskWidth - 1)
                val maskY = (y.toFloat() * maskHeight / height).toInt().coerceIn(0, maskHeight - 1)
                val confidence = maskValues[maskY * maskWidth + maskX]
                val pixelIndex = y * width + x
                val px = pixels[pixelIndex]
                val alpha = (confidence * 255f * 1.3f).toInt().coerceIn(0, 255)
                val r = (px shr 16) and 0xFF
                val g = (px shr 8) and 0xFF
                val b = px and 0xFF
                pixels[pixelIndex] = (alpha shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }
}
