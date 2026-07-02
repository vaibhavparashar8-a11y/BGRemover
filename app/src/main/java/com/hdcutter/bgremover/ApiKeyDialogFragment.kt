package com.hdcutter.bgremover

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ApiKeyDialogFragment(
    private val onUrl: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dp = resources.displayMetrics.density

        val input = EditText(requireContext()).apply {
            hint = "https://your-name-bg-remover.hf.space"
            setSingleLine()
        }

        val hintView = TextView(requireContext()).apply {
            text = "Paste your Hugging Face Space URL above."
            textSize = 12f
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
            setPadding(0, (8 * dp).toInt(), 0, 0)
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val hPad = (24 * dp).toInt()
            val vPad = (16 * dp).toInt()
            setPadding(hPad, vPad, hPad, vPad / 2)package com.hdcutter.bgremover

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ApiKeyDialogFragment(
    private val onUrl: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dp = resources.displayMetrics.density

        val input = EditText(requireContext()).apply {
            hint = "https://your-name-bg-remover.hf.space"
            setSingleLine()
        }

        val hintView = TextView(requireContext()).apply {
            text = "Paste your Hugging Face Space URL above."
            textSize = 12f
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
            setPadding(0, (8 * dp).toInt(), 0, 0)
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val hPad = (24 * dp).toInt()
            val vPad = (16 * dp).toInt()
            setPadding(hPad, vPad, hPad, vPad / 2)
            addView(input)
            addView(hintView)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Your API URL")
            .setView(container)
            .setPositiveButton("Remove Background") { _, _ ->
                val url = input.text.toString().trim()
                if (url.isNotEmpty()) {
                    onUrl(url)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}

            addView(input)
            addView(hintView)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Your API URL")
            .setView(container)
            .setPositiveButton("Remove Background") { _, _ ->
                val url = input.text.toString().trim()
                if (url.isNotEmpty()) {
                    onUrl(url)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
