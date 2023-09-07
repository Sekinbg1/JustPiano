package ly.pp.justpiano3.thread

import android.content.Context
import kotlinx.coroutines.*
import ly.pp.justpiano3.JPApplication
import ly.pp.justpiano3.enums.PlaySongsModeEnum
import ly.pp.justpiano3.utils.PmSongUtil
import ly.pp.justpiano3.utils.SoundEngineUtil

object SongPlay {

    /**
     * 播放模式
     */
    var playSongsMode = PlaySongsModeEnum.ONCE

    /**
     * 自定义线程池产生的协程作用域
     */
    private val threadPoolScope = CoroutineScope(ThreadPoolUtils.getThreadPool().asCoroutineDispatcher())

    /**
     * 协程job
     */
    private var job: Job? = null

    /**
     * 播放结束时切换下一首曲谱的回调
     */
    var callBack: CallBack? = null

    /**
     * 开始播放
     */
    fun startPlay(context: Context, songFilePath: String, tune: Int) {
        PmSongUtil.parsePmDataByFilePath(context, songFilePath)?.let {
            job?.cancel()
            job = threadPoolScope.launch {
                for (i in it.pitchArray.indices) {
                    if (!isActive) {
                        return@launch
                    }
                    delay(it.tickArray[i].toLong() * it.globalSpeed)
                    SoundEngineUtil.playSound((it.pitchArray[i] + tune).toByte(), it.volumeArray[i])
                }
                delay(1000)
                val nextSongFilePath = computeNextSongByPlaySongsMode(songFilePath)
                if (callBack != null && !nextSongFilePath.isNullOrEmpty()) {
                    callBack!!.onSongChangeNext(nextSongFilePath)
                }
            }
        }
    }

    private fun computeNextSongByPlaySongsMode(currentSongFilePath: String): String? {
        when (playSongsMode) {
            PlaySongsModeEnum.RECYCLE -> return currentSongFilePath
            PlaySongsModeEnum.RANDOM -> {
                val songs = JPApplication.getSongDatabase().songDao().getSongByRightHandDegreeWithRandom(0, 10)
                return if (songs.isEmpty()) null else songs[0].filePath
            }
            PlaySongsModeEnum.FAVOR_RANDOM -> {
                val songInFavoriteWithRandom = JPApplication.getSongDatabase().songDao().getSongInFavoriteWithRandom()
                return if (songInFavoriteWithRandom.isEmpty()) null else songInFavoriteWithRandom[0].filePath
            }
            PlaySongsModeEnum.FAVOR -> {
                val favoriteSongList = JPApplication.getSongDatabase().songDao().getFavoriteSongs()
                for ((index, song) in favoriteSongList.withIndex()) {
                    if (song.filePath == currentSongFilePath) {
                        return favoriteSongList[if (index == favoriteSongList.size - 1) 0 else index + 1].filePath
                    }
                }
                return null
            }
            else -> return null
        }
    }

    /**
     * 停止播放
     */
    fun stopPlay() {
        job?.cancel()
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return job?.isActive == true
    }

    /**
     * 回调
     */
    interface CallBack {
        /**
         * This will be called when song changes to the next and ready to start playing
         */
        fun onSongChangeNext(songFilePath: String)
    }
}