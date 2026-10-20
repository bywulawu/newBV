package dev.frost819.newbv.core.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build

/**
 * 设备性能画像。
 *
 * 用于识别电视端的低内存机型并收紧图片缓存、播放器缓冲等内存占用。
 * TCL 电视（含 Q10K 系列的 MT9655 平台）常见应用堆上限仅约 192 MB，
 * 大量 4K 封面解码叠加播放器缓冲时容易触发 `OutOfMemoryError`，
 * 因此将 TCL 设备与低 `memoryClass` 设备统一视为低内存设备。
 *
 * @property manufacturer 厂商（[Build.MANUFACTURER]）
 * @property brand 品牌（[Build.BRAND]）
 * @property model 型号（[Build.MODEL]）
 * @property memoryClassMb 单应用堆等级（[ActivityManager.getMemoryClass]，单位 MB，0 表示未知）
 * @property totalMemBytes 系统总内存（字节，0 表示未知）
 * @property isTcl 是否为 TCL 设备
 * @property isLowMemory 是否应启用低内存策略
 */
data class DeviceProfile(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val memoryClassMb: Int,
    val totalMemBytes: Long,
    val isTcl: Boolean,
    val isLowMemory: Boolean,
) {
    companion object {
        /**
         * 低内存判定阈值。
         *
         * Android 的 `memoryClass` 在低端设备上常见为 128/192/256，
         * 超过 256 MB 的设备不再仅因堆等级触发低内存策略。
         */
        const val LOW_MEMORY_THRESHOLD_MB = 256

        /**
         * 根据厂商与堆等级判断是否需要低内存策略。
         *
         * TCL 电视固件常把堆上限压到 192 MB 左右，即使系统总内存为 4 GB，
         * 因此只要识别为 TCL 就直接启用；其余设备按 [LOW_MEMORY_THRESHOLD_MB] 判断。
         *
         * @param isTcl 是否为 TCL 设备
         * @param memoryClassMb 单应用堆等级（MB），0 表示未知
         * @return 是否应启用低内存策略
         */
        fun shouldUseLowMemory(
            isTcl: Boolean,
            memoryClassMb: Int,
        ): Boolean = isTcl || memoryClassMb in 1..LOW_MEMORY_THRESHOLD_MB

        /**
         * 读取当前设备画像。
         *
         * 读取失败时对应字段回退为 0 或空字符串，不会抛出异常。
         *
         * @param context 任意 Context（内部只使用系统服务与 Build 信息）
         */
        fun detect(context: Context): DeviceProfile {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            runCatching { activityManager?.getMemoryInfo(memoryInfo) }

            val manufacturer = Build.MANUFACTURER.orEmpty()
            val brand = Build.BRAND.orEmpty()
            val model = Build.MODEL.orEmpty()
            val isTcl =
                listOf(manufacturer, brand, model).any {
                    it.contains("tcl", ignoreCase = true)
                }
            val memoryClassMb = activityManager?.memoryClass ?: 0

            return DeviceProfile(
                manufacturer = manufacturer,
                brand = brand,
                model = model,
                memoryClassMb = memoryClassMb,
                totalMemBytes = memoryInfo.totalMem,
                isTcl = isTcl,
                isLowMemory = shouldUseLowMemory(isTcl, memoryClassMb),
            )
        }
    }
}
