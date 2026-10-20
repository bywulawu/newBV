package dev.frost819.newbv.player

/**
 * 播放器配置选项。
 *
 * @param userAgent 自定义 User-Agent，用于 HTTP 请求
 * @param referer 自定义 Referer，用于 B 站防盗链验证
 * @param enableFfmpegAudioRenderer 是否启用 FFmpeg 音频渲染器（用于解码特殊音频编码如 FLAC）
 * @param enableSoftwareVideoDecoder 是否强制使用软件视频解码（用于不支持硬解的编码）
 * @param enableTunneledPlayback 是否启用硬件隧道播放（TCL 等电视的实验性优化）
 * @param lowMemoryMode 是否限制播放器缓冲以降低内存占用（低内存电视适用）
 */
data class VideoPlayerOptions(
    val userAgent: String? = null,
    val referer: String? = null,
    val enableFfmpegAudioRenderer: Boolean = false,
    val enableSoftwareVideoDecoder: Boolean = false,
    val enableTunneledPlayback: Boolean = false,
    val lowMemoryMode: Boolean = false,
)
