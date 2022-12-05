package com.jaldeeinc.jaldee.utils

import android.app.Dialog
import android.content.Context

import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import com.jaldeeinc.jaldee.R

fun showUIDialog(context: Context, title: String, message: String?, success: () -> Unit) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.custom_dialog)

    val tvTitle = dialog.findViewById<TextView>(R.id.mTitle)
    val tvMessage = dialog.findViewById<TextView>(R.id.mMessage)
    tvTitle.text = title
    tvMessage.text = message ?: "Unable to get message from server."
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)
    dialog.show()

    val acceptButtonText = dialog.findViewById<TextView>(R.id.buttonOk)
    acceptButtonText.text = "ok"
    val acceptButton = dialog.findViewById<RelativeLayout>(R.id.rl_positive)

    acceptButton.setOnClickListener {
        dialog.dismiss()
        success.invoke()
    }
}


