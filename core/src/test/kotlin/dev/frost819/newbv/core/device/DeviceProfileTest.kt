package dev.frost819.newbv.core.device

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

/**
 * [DeviceProfile] 低内存判定的单元测试。
 */
class DeviceProfileTest {
    @Test
    fun `tcl device uses low memory strategy even with large heap class`() {
        assertThat(DeviceProfile.shouldUseLowMemory(isTcl = true, memoryClassMb = 512)).isTrue()
    }

    @Test
    fun `heap class at or below threshold uses low memory strategy`() {
        assertThat(DeviceProfile.shouldUseLowMemory(isTcl = false, memoryClassMb = 128)).isTrue()
        assertThat(DeviceProfile.shouldUseLowMemory(isTcl = false, memoryClassMb = 192)).isTrue()
        assertThat(DeviceProfile.shouldUseLowMemory(isTcl = false, memoryClassMb = 256)).isTrue()
    }

    @Test
    fun `non tcl device above threshold keeps standard strategy`() {
        assertThat(DeviceProfile.shouldUseLowMemory(isTcl = false, memoryClassMb = 512)).isFalse()
    }

    @Test
    fun `unknown heap class does not trigger low memory for non tcl device`() {
        assertThat(DeviceProfile.shouldUseLowMemory(isTcl = false, memoryClassMb = 0)).isFalse()
    }
}
