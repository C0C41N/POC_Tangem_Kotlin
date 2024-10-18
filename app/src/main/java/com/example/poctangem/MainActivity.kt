package com.example.poctangem

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
import com.example.poctangem.ui.theme.PocTangemAndroidTheme
import com.tangem.sdk.codora.TangemSdkProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        TangemSdkProvider.init(this)

        setContent {
            PocTangemAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(Modifier.padding(innerPadding))
                }
            }
        }

    }

}


@Composable
fun MainContent(modifier: Modifier = Modifier) {
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
                    actionHandler.scan()
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .width(300.dp)
            ) {
                Text("Scan")
            }

            Button(
                onClick = {
                    actionHandler.sign()
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .width(300.dp)
            ) {
                Text("Sign")
            }

            Button(
                onClick = {
                    actionHandler.purgeAllWallets()
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .width(300.dp)
            ) {
                Text("Purge All Wallets")
            }

            Button(
                onClick = {
                    actionHandler.createAllWallets()
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .width(300.dp)
            ) {
                Text("Create All Wallets")
            }
        }
    }
}
