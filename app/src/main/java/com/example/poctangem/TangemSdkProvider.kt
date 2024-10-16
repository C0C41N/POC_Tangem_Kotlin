package com.example.poctangem

import androidx.activity.ComponentActivity
import com.tangem.TangemSdk
import com.tangem.common.authentication.DummyAuthenticationManager
import com.tangem.common.card.FirmwareVersion
import com.tangem.common.core.Config
import com.tangem.common.services.secure.SecureStorage
import com.tangem.crypto.bip39.Wordlist
import com.tangem.sdk.DefaultSessionViewDelegate
import com.tangem.sdk.extensions.getWordlist
import com.tangem.sdk.extensions.initKeystoreManager
import com.tangem.sdk.extensions.initNfcManager
import com.tangem.sdk.nfc.AndroidNfcAvailabilityProvider
import com.tangem.sdk.storage.create

object TangemSdkProvider {

    private var instance: TangemSdk? = null

    fun getInstance(): TangemSdk {

        return requireNotNull(instance) { "TangemSdkProvider instance is not initialized" }

    }

    fun init(context: ComponentActivity) {

        val authenticationManager = DummyAuthenticationManager()

        val config = Config().apply {
            linkedTerminal = false
            allowUntrustedCards = true
            filter.allowedCardTypes = FirmwareVersion.FirmwareType.entries
            defaultDerivationPaths = mutableMapOf()
        }

        val secureStorage = SecureStorage.create(context)
        val nfcManager = TangemSdk.initNfcManager(context)

        val viewDelegate = DefaultSessionViewDelegate(nfcManager, context)
        viewDelegate.sdkConfig = config

        val nfcAvailabilityProvider = AndroidNfcAvailabilityProvider(context)

        instance = TangemSdk(
            reader = nfcManager.reader,
            viewDelegate = viewDelegate,
            nfcAvailabilityProvider = nfcAvailabilityProvider,
            secureStorage = secureStorage,
            wordlist = Wordlist.getWordlist(context),
            config = config,
            authenticationManager = authenticationManager,
            keystoreManager = TangemSdk.initKeystoreManager(authenticationManager, secureStorage),
        )

    }

}