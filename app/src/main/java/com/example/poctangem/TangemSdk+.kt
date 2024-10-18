package com.example.poctangem

import com.tangem.Message
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.tangem.TangemSdk
import com.tangem.common.CompletionResult
import com.tangem.common.card.Card
import com.tangem.common.core.CardSession
import com.tangem.common.core.TangemError
import com.tangem.common.json.MoshiJsonConverter
import com.tangem.operations.ScanTask

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

