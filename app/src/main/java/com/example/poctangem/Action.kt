package com.example.poctangem

import android.util.Log
import com.tangem.common.card.EllipticCurve
import com.tangem.crypto.decodeBase58
import com.tangem.operations.ScanTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.tangem.crypto.encodeToBase58String
import com.tangem.operations.sign.SignCommand
import com.tangem.operations.wallet.CreateWalletTask
import com.tangem.operations.wallet.PurgeWalletCommand
import com.tangem.sdk.codora.TangemSdkProvider
import com.tangem.sdk.codora.runAsync
import com.tangem.sdk.codora.startSessionAsync
import com.tangem.sdk.codora.toJson
import kotlinx.coroutines.DelicateCoroutinesApi

const val logTag = "Action"

@OptIn(DelicateCoroutinesApi::class)
class Action {

    private val sdk = TangemSdkProvider.getInstance()

    fun scan() {

        GlobalScope.launch(Dispatchers.Main) {

            val startSessionResult = sdk.startSessionAsync(null, null, accessCode = "141414")

            if (!startSessionResult.success || startSessionResult.value == null) {
                Log.e(logTag,"Start Session failed: ${startSessionResult.error}")
                return@launch
            }

            val session = startSessionResult.value!!

            val scanTask = ScanTask()
            val scanResult = scanTask.runAsync(session)

            if (!scanResult.success || scanResult.value == null) {
                Log.e(logTag, "ScanTask failed: ${scanResult.error}")
                session.stop()
                return@launch
            }

            val card = scanResult.value!!

            Log.d(logTag, card.toJson())

            for (wallet in card.wallets) {
                val curve = wallet.curve.name
                val pubKey = wallet.publicKey.encodeToBase58String()
                Log.d(logTag, "Wallet $curve | $pubKey")
            }

            session.stop()
        }

    }

    @OptIn(ExperimentalStdlibApi::class)
    fun sign() {

        val publicKeyString = "AXfqRy8Jw3M4JhH1ea29YZH69hQqjndpgBpBSoRVLvYn"
        val unsignedHexString = "01000103c6dadd07fa6b967f95c1c794207a6660b6c103bb3d9225cb65a32aec9233bd4a7851663184f478288effadd0b24e403c625569350166ae9dfc10e1eaf4a203b000000000000000000000000000000000000000000000000000000000000000003aab9ecdda6344c5dea7dc04242579a2171d7e0b7659ac1d16b20ab1f00ba77901020200010c020000001027000000000000"

        val publicKey = publicKeyString.decodeBase58()
        val unsignedHex = unsignedHexString.hexToByteArray()

        GlobalScope.launch(Dispatchers.Main) {

            val startSessionResult = sdk.startSessionAsync(null, null, accessCode = "141414")

            if (!startSessionResult.success || startSessionResult.value == null) {
                Log.e(logTag,"Start Session failed: ${startSessionResult.error}")
                return@launch
            }

            val session = startSessionResult.value!!

            val signTask = SignCommand(arrayOf(unsignedHex), publicKey)
            val signResult = signTask.runAsync(session)

            if (!signResult.success || signResult.value == null) {
                Log.e(logTag,"SignTask failed: ${signResult.error}")
                session.stop()
                return@launch
            }

            val signature = signResult.value!!.signatures[0]
            Log.d(logTag, "Signed Hex | ${signature.toHexString()}")

            session.stop()
        }

    }

    fun purgeAllWallets() {

        GlobalScope.launch(Dispatchers.Main) {

            val startSessionResult = sdk.startSessionAsync(null, null, accessCode = "141414")

            if (!startSessionResult.success || startSessionResult.value == null) {
                Log.e(logTag,"Start Session failed: ${startSessionResult.error}")
                return@launch
            }

            val session = startSessionResult.value!!

            val scanTask = ScanTask()
            val scanResult = scanTask.runAsync(session)

            if (!scanResult.success || scanResult.value == null) {
                Log.e(logTag,"ScanTask failed: ${scanResult.error}")
                session.stop()
                return@launch
            }

            val card = scanResult.value!!

            for (wallet in card.wallets) {
                val purgeTask = PurgeWalletCommand(wallet.publicKey)
                val purgeResult = purgeTask.runAsync(session)

                if (!purgeResult.success || purgeResult.value == null) {
                    Log.e(logTag,"PurgeTask failed: ${purgeResult.error}")
                    session.stop()
                    return@launch
                }

                val curve = wallet.curve.name
                val pubKey = wallet.publicKey.encodeToBase58String()

                Log.d(logTag, "Purged wallet $curve | $pubKey")
            }

            session.stop()
        }

    }

    fun createAllWallets() {

        GlobalScope.launch(Dispatchers.Main) {

            val startSessionResult = sdk.startSessionAsync(null, null, accessCode = "141414")

            if (!startSessionResult.success || startSessionResult.value == null) {
                Log.e(logTag,"Start Session failed: ${startSessionResult.error}")
                return@launch
            }

            val session = startSessionResult.value!!
            val curves: List<EllipticCurve> = listOf(EllipticCurve.Secp256k1, EllipticCurve.Ed25519, EllipticCurve.Bls12381G2Aug, EllipticCurve.Bip0340, EllipticCurve.Ed25519Slip0010)

            for(curve in curves) {
                val createWallet = CreateWalletTask(curve)
                val createWalletResult = createWallet.runAsync(session)

                if (!createWalletResult.success || createWalletResult.value == null) {
                    Log.e(logTag,"CreateWalletTask failed: ${createWalletResult.error}")
                    session.stop()
                    return@launch
                }

                val wallet = createWalletResult.value!!.wallet

                Log.d(logTag, "Created wallet ${wallet.curve.name} | ${wallet.publicKey.encodeToBase58String()}")
            }

            session.stop()
        }

    }
}