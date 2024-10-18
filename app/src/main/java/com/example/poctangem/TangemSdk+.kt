package com.example.poctangem

import com.tangem.Message
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.tangem.TangemSdk
import com.tangem.common.CompletionResult
import com.tangem.common.SuccessResponse
import com.tangem.common.card.Card
import com.tangem.common.core.CardSession
import com.tangem.common.core.TangemError
import com.tangem.common.json.MoshiJsonConverter
import com.tangem.operations.ScanTask
import com.tangem.operations.sign.SignCommand
import com.tangem.operations.sign.SignResponse
import com.tangem.operations.wallet.CreateWalletResponse
import com.tangem.operations.wallet.CreateWalletTask
import com.tangem.operations.wallet.PurgeWalletCommand

private val json = MoshiJsonConverter.default()

fun Card.toJson(): String {
    return json.prettyPrint(this)
}

suspend fun TangemSdk.startSessionAsync(cardId: String?, initialMessage: Message?, accessCode: String?): Eval<CardSession, TangemError> {
    return suspendCoroutine { continuation ->
        this.startSession(cardId, initialMessage, accessCode) { session, error ->
            if (error != null) {
                continuation.resume(Eval.failure(error))
            } else {
                continuation.resume(Eval.success(session))
            }
        }
    }
}

suspend fun  ScanTask.runAsync(session: CardSession): Eval<Card, TangemError> {
    return suspendCoroutine { continuation ->
        this.run(session) { outcome ->
            when (outcome) {
                is CompletionResult.Success -> {
                    continuation.resume(Eval.success(outcome.data))
                }
                is CompletionResult.Failure -> {
                    continuation.resume(Eval.failure(outcome.error))
                }
            }
        }
    }
}

suspend fun SignCommand.runAsync(session: CardSession): Eval<SignResponse, TangemError> {
    return suspendCoroutine { continuation ->
        this.run(session) { outcome ->
            when (outcome) {
                is CompletionResult.Success -> {
                    continuation.resume(Eval.success(outcome.data))
                }
                is CompletionResult.Failure -> {
                    continuation.resume(Eval.failure(outcome.error))
                }
            }
        }
    }
}

suspend fun PurgeWalletCommand.runAsync(session: CardSession): Eval<SuccessResponse, TangemError> {
    return suspendCoroutine { continuation ->
        this.run(session) { outcome ->
            when (outcome) {
                is CompletionResult.Success -> {
                    continuation.resume(Eval.success(outcome.data))
                }
                is CompletionResult.Failure -> {
                    continuation.resume(Eval.failure(outcome.error))
                }
            }
        }
    }
}

suspend fun CreateWalletTask.runAsync(session: CardSession): Eval<CreateWalletResponse, TangemError> {
    return suspendCoroutine { continuation ->
        this.run(session) { outcome ->
            when (outcome) {
                is CompletionResult.Success -> {
                    continuation.resume(Eval.success(outcome.data))
                }
                is CompletionResult.Failure -> {
                    continuation.resume(Eval.failure(outcome.error))
                }
            }
        }
    }
}
