package com.hdcutter.bgremover

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * On-device background remover using Google ML Kit Selfie Segmentation.
 *
 * Works 100% offline — no internet needed.
 * Best for photos of people/portraits.
 * For all other subjects, use the Cloud API option instead.
 */
object SubjectSegmentHelper {

    /**
     * Takes a full-resolution [Bitmap] and returns a new Bitmap
     * with the background made transparent (ARGB_8888).
     * Same pixel dimensions as input — no quality loss.
     */
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

    /**
     * Applies the segmentation mask with smooth alpha blending.
     *
     * Instead of a hard cut (opaque vs transparent), each pixel's alpha
     * is set proportional to its confidence value — this creates natural,
     * feathered edges especially around hair and soft outlines.
     *
     * The mask is scaled to match the original image dimensions.
     */
    private fun applyMask(
        original: Bitmap,
        maskBuffer: java.nio.ByteBuffer,
        maskWidth: Int,
        maskHeight: Int
    ): Bitmap {
        val width = original.width
        val height = original.height

        // Read all mask confidence values into a FloatArray
        maskBuffer.rewind()
        val floatBuffer = maskBuffer.asFloatBuffer()
        val maskValues = FloatArray(maskWidth * maskHeight)
        floatBuffer.get(maskValues)

        // Copy original pixels
        val pixels = IntArray(width * height)
        original.getPixels(pixels, 0, width, 0, 0, width, height)

        // Apply mask with smooth alpha — scale mask coords to image coords
        for (y in 0 until height) {
            for (x in 0 until width) {
                val maskX = (x.toFloat() * maskWidth / width).toInt().coerceIn(0, maskWidth - 1)
                val maskY = (y.toFloat() * maskHeight / height).toInt().coerceIn(0, maskHeight - 1)
                val confidence = maskValues[maskY * maskWidth + maskX]

                val pixelIndex = y * width + x
                val original = pixels[pixelIndex]

                // Smooth alpha: confidence 0.0 → fully transparent, 1.0 → fully opaque
                // Boosted slightly so edge pixel