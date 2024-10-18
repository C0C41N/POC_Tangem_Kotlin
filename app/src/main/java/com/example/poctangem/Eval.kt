package com.example.poctangem

class Eval<T, U>(
    val success: Boolean,
    val value: T? = null,
    val error: U? = null
) {

    companion object {

        fun <T, U> success(value: T): Eval<T, U> {
            return Eval(success = true, value = value)
        }

        fun <T, U> failure(error: U): Eval<T, U> {
            return Eval(success = false, error = error)
        }

    }

}

