package com.example.poctangem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.poctangem.ui.theme.PocTangemAndroidTheme
import com.tangem.SessionViewDelegate

import com.tangem.TangemSdk
import com.tangem.common.authentication.AuthenticationManager
import com.tangem.common.card.FirmwareVersion
import com.tangem.common.core.Config
import com.tangem.common.services.secure.SecureStorage
import com.tangem.crypto.bip39.Wordlist
import com.tangem.sdk.DefaultSessionViewDelegate
import com.tangem.sdk.extensions.getWordlist
import com.tangem.sdk.extensions.initAuthenticationManager
import com.tangem.sdk.extensions.initKeystoreManager
import com.tangem.sdk.extensions.initNfcManager
import com.tangem.sdk.nfc.AndroidNfcAvailabilityProvider
import com.tangem.sdk.storage.create

interface AuthenticationCallback {
    fun onAuthenticationManagerReady(authenticationManager: AuthenticationManager)
}

object AuthManagerHolder {
    var authenticationManager: AuthenticationManager? = null
}

class MainActivity : ComponentActivity(), AuthenticationCallback {
    private val sdkState = mutableStateOf<TangemSdk?>(null)
    private lateinit var viewDelegate: SessionViewDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocTangemAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    sdkState.value?.let { sdk ->
                        MainContent(Modifier.padding(innerPadding), sdk)
                    } ?: Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Initializing...")
                    }
                }
            }
        }

        val intent = Intent(this, EmptyFragmentActivity::class.java)
        @Suppress("DEPRECATION")
        startActivityForResult(intent, REQUEST_CODE_AUTH_MANAGER)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_AUTH_MANAGER) {
            AuthManagerHolder.authenticationManager?.let { authenticationManager ->
                onAuthenticationManagerReady(authenticationManager)
            }
        }
    }

    override fun onAuthenticationManagerReady(authenticationManager: AuthenticationManager) {

        val config = Config().apply {
            linkedTerminal = false
            allowUntrustedCards = true
            filter.allowedCardTypes = FirmwareVersion.FirmwareType.entries
            defaultDerivationPaths = mutableMapOf()
        }

        val secureStorage = SecureStorage.create(this)
        val nfcManager = TangemSdk.initNfcManager(this)

        val viewDelegate = DefaultSessionViewDelegate(nfcManager, this)
        viewDelegate.sdkConfig = config
        this.viewDelegate = viewDelegate

        val nfcAvailabilityProvider = AndroidNfcAvailabilityProvider(this)

        val newSdk = TangemSdk(
            reader = nfcManager.reader,
            viewDelegate = viewDelegate,
            nfcAvailabilityProvider = nfcAvailabilityProvider,
            secureStorage = secureStorage,
            wordlist = Wordlist.getWordlist(this),
            config = config,
            authenticationManager = authenticationManager,
            keystoreManager = TangemSdk.initKeystoreManager(authenticationManager, secureStorage),
        )

        sdkState.value = newSdk
    }

    companion object {
        const val REQUEST_CODE_AUTH_MANAGER = 1001
    }

}

class EmptyFragmentActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authenticationManager = TangemSdk.initAuthenticationManager(this)
        AuthManagerHolder.authenticationManager = authenticationManager

        finish()
    }
}


@Composable
fun MainContent(modifier: Modifier = Modifier, sdk: TangemSdk) {
    val actionHandler = Action()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    actionHandler.scan(sdk)
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
            ) {
                Text("Scan")
            }
        }
    }
}
