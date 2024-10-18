package com.example.poctangem

import android.util.Log
import com.tangem.crypto.decodeBase58
import com.tangem.operations.ScanTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.tangem.crypto.encodeToBase58String
import com.tangem.operations.sign.SignCommand
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

    @OptIn(ExperimentalStdlibApi::class)
    fun sign() {

        val publicKeyString = "6MEvbX4ek5xivDyrMdyrB2X3nJrH1CACWJwK6kjFNyCF"
        val unsignedHexString = "01000103c6dadd07fa6b967f95c1c794207a6660b6c103bb3d9225cb65a32aec9233bd4a7851663184f478288effadd0b24e403c625569350166ae9dfc10e1eaf4a203b000000000000000000000000000000000000000000000000000000000000000003aab9ecdda6344c5dea7dc04242579a2171d7e0b7659ac1d16b20ab1f00ba77901020200010c020000001027000000000000"

        val publicKey = publicKeyString.decodeBase58()
        val unsignedHex = unsignedHexString.hexToByteArray()

        GlobalScope.launch(Dispatchers.Main) {

            val startSessionResult = sdk.startSessionAsync(null, null, accessCode = "141414")

            if (!startSessionResult.success || startSessionResult.value == null) {
                println("Start Session failed: ${startSessionResult.error}")
                return@launch
            }

            val session = startSessionResult.value

            val signTask = SignCommand(arrayOf(unsignedHex), publicKey)
            val signResult = signTask.runAsync(session)

            if (!signResult.success || signResult.value == null) {
                println("ScanTask failed: ${signResult.error}")
                session.stop()
                return@launch
            }

            val signature = signResult.value.signatures[0]
            Log.d(logTag, "Signature: ${signature.toHexString()}")

            session.stop()
        }

    }

    fun purgeAllWallets() {
        //
    }

    fun createAllWallets() {
        //
    }
}