package com.example.poctangem

import android.util.Log

import com.tangem.TangemSdk
import com.tangem.common.CompletionResult

const val logTag = "Action"

class Action {
    fun scan(sdk: TangemSdk) {
        Log.d(logTag, "Init")

        sdk.scanCard(
            initialMessage = null,
            allowRequestUserCodeFromRepository = true
        ) { result ->
            when (result) {
                is CompletionResult.Success -> {
                    val scannedCard = result.data
                    // Log the scanned card information
                    Log.d(logTag, "Scanned Card: $scannedCard")
                }
                is CompletionResult.Failure -> {
                    val error = result.error
                    // Log the error
                    Log.e(logTag, "Failed to scan card: $error")
                }
            }
        }
    }
}