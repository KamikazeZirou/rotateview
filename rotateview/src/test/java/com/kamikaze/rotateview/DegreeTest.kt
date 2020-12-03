package com.kamikaze.rotateview

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DegreeTest {
    @Test
    fun makeSureInRegularRange() {
        assertThat((-1).makeSureInRegularRange()).isEqualTo(359)
        assertThat(0.makeSureInRegularRange()).isEqualTo(0)
        assertThat(359.makeSureInRegularRange()).isEqualTo(359)
        assertThat(360.makeSureInRegularRange()).isEqualTo(0)
    }

    @Test
    fun round() {
        assertThat(316.round()).isEqualTo(0)
        assertThat(45.round()).isEqualTo(0)
        assertThat(46.round()).isEqualTo(90)
        assertThat(135.round()).isEqualTo(90)
        assertThat(136.round()).isEqualTo(180)
        assertThat(225.round()).isEqualTo(180)
        assertThat(226.round()).isEqualTo(270)
        assertThat(315.round()).isEqualTo(270)
    }
}