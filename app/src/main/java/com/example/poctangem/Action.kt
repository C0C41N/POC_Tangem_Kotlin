package com.example.poctangem

import android.util.Log

import com.tangem.common.CompletionResult

const val logTag = "Action"

class Action() {

    private val sdk = TangemSdkProvider.getInstance()

    fun scan() {

//        sdk.startSession { session, error ->
//
//        }

        sdk.scanCard(
            initialMessage = null,
            allowRequestUserCodeFromRepository = true
        ) { result ->
            when (result) {
                is CompletionResult.Success -> {
                    val scannedCard = result.data
                    Log.d(logTag, "Scanned Card: $scannedCard")
                }
                is CompletionResult.Failure -> {
                    val error = result.error
                    Log.e(logTag, "Failed to scan card: $error")
                }
            }
        }
    }
}