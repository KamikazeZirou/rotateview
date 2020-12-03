package com.kamikaze.rotateview

inline fun Int.makeSureInRegularRange(): Int = if (this >= 0) this % 360 else this % 360 + 360

fun Int.round(): Int = when (this) {
    in (0..45), in (316..359) -> 0
    in (46..135) -> 90
    in (136..225) -> 180
    in (226..315) -> 270
    else -> 0
}