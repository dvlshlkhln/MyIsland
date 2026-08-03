package com.myisland.dynamic.service

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.myisland.dynamic.data.MediaState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaSessionController(private val context: Context) {

    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState: StateFlow<MediaState> = _mediaState.asStateFlow()

    private var activeController: MediaController? = null

    private val callback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updateMediaState(metadata, activeController?.playbackState)
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updateMediaState(activeController?.metadata, state)
        }

        override fun onSessionDestroyed() {
            _mediaState.value = MediaState()
            activeController = null
        }
    }

    fun init(componentName: ComponentName) {
        try {
            val controllers = mediaSessionManager?.getActiveSessions(componentName)
            attachController(selectBestController(controllers))

            mediaSessionManager?.addOnActiveSessionsChangedListener({ newControllers ->
                attachController(selectBestController(newControllers))
            }, componentName)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun selectBestController(controllers: List<MediaController>?): MediaController? {
        if (controllers.isNullOrEmpty()) return null
        return controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: controllers.firstOrNull { it.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)?.isNotBlank() == true }
            ?: controllers.firstOrNull()
    }

    private fun attachController(controller: MediaController?) {
        activeController?.unregisterCallback(callback)
        activeController = controller
        activeController?.registerCallback(callback)
        updateMediaState(activeController?.metadata, activeController?.playbackState)
    }

    private fun updateMediaState(metadata: MediaMetadata?, state: PlaybackState?) {
        if (metadata == null && state == null) {
            _mediaState.value = MediaState()
            return
        }

        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
        val artBitmap = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        val isPlaying = state?.state == PlaybackState.STATE_PLAYING
        val position = state?.position ?: 0L
        val pkg = activeController?.packageName ?: ""

        _mediaState.value = MediaState(
            title = title,
            artist = artist,
            albumArt = artBitmap,
            isPlaying = isPlaying,
            durationMs = duration,
            positionMs = position,
            packageName = pkg
        )
    }

    fun togglePlayPause() {
        val state = activeController?.playbackState?.state
        if (state == PlaybackState.STATE_PLAYING) {
            activeController?.transportControls?.pause()
        } else {
            activeController?.transportControls?.play()
        }
    }

    fun skipToNext() {
        activeController?.transportControls?.skipToNext()
    }

    fun skipToPrevious() {
        activeController?.transportControls?.skipToPrevious()
    }

    fun seekTo(positionMs: Long) {
        activeController?.transportControls?.seekTo(positionMs)
    }
}
