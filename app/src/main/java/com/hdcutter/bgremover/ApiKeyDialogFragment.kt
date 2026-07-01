package com.hdcutter.bgremover

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * Dialog that asks the user for their Hugging Face Space URL.
 *
 * How to get your URL:
 * 1. Go to huggingface.co and sign up (free)
 * 2. Create a new Space → name it "bg-remover" → SDK: Docker or Gradio
 * 3. Upload the backend files (app.py, requirements.txt)
 * 4. Your URL will be: https://YOUR-USERNAME-bg-remover.hf.space
 */
class ApiKeyDialogFragment(
    private val onUrl: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dp = resources.displayMetrics.density

        val input = EditText(requireContext()).apply {
            hint = "https://your-name-bg-remover.hf.space"
            setSingleLine()
        }

        val hint = TextView(requireContext()).apply {
            text = "Paste your Hugging Face Space URL above.\nDon't have one yet? See setup guide."
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
            addView(hint)
        }

        return AlertDialog.Builder(requireContext())
       