package com.kamikaze.rotateview

inline fun Int.makeSureInRegularRange(): Int = if (this >= 0) this % 360 else this % 360 + 360

fun Int.round(): Int = when (this) {
    in (0..45), in (315..360) -> 0
    in (45..135) -> 90
    in (135..225) -> 180
    in (225..315) -> 270
    else -> 0
}