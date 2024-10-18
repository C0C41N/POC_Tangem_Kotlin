package com.example.poctangem

import android.util.Log
import com.tangem.operations.ScanTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.tangem.crypto.encodeToBase58String
import kotlinx.coroutines.DelicateCoroutinesApi

const val logTag = "Action"

@OptIn(DelicateCoroutinesApi::class)
class Action {

    private val sdk = TangemSdkProvider.getInstance()

    fun scan() {

        GlobalScope.launch(Dispatchers.Main) {

            val startSessionResult = sdk.startSessionAsync(null, null, accessCode = "141414")

            if (!startSessionResult.success || startSessionResult.value == null) {
                println("Start Session failed: ${startSessionResult.error}")
                return@launch
            }

            val session = startSessionResult.value

            val scanTask = ScanTask()
            val scanResult = scanTask.runAsync(session)

            if (!scanResult.success || scanResult.value == null) {
                println("ScanTask failed: ${scanResult.error}")
                session.stop()
                return@launch
            }

            val card = scanResult.value

            Log.d(logTag, card.toJson())

            card.wallets.forEach { wallet ->
                val curve = wallet.curve.name
                val pubKey = wallet.publicKey.encodeToBase58String()
                Log.d(logTag, "Wallet $curve | $pubKey")
            }

            session.stop()
        }
    }
}