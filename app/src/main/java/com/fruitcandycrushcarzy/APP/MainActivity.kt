package com.fruitcandycrushcarzy.APP

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.MobileAds
import com.fruitcandycrushcarzy.APP.game.data.ScoreRepository
import com.fruitcandycrushcarzy.APP.game.util.AdManager
import com.fruitcandycrushcarzy.APP.game.util.SoundManager
import com.fruitcandycrushcarzy.APP.game.util.VibrationManager
import com.fruitcandycrushcarzy.APP.game.viewmodel.GameEvent
import com.fruitcandycrushcarzy.APP.game.viewmodel.GameViewModel
import com.fruitcandycrushcarzy.APP.ui.GameScreen
import com.fruitcandycrushcarzy.APP.ui.theme.FRUITCANDYCRUSHCARZYTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        enableEdgeToEdge()

        setContent {
            FRUITCANDYCRUSHCARZYTheme {

                val context = LocalContext.current

                val scoreRepository = remember {
                    ScoreRepository(context)
                }

                val viewModel: GameViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {

                        override fun <T : ViewModel> create(
                            modelClass: Class<T>
                        ): T {
                            @Suppress("UNCHECKED_CAST")
                            return GameViewModel(
                                scoreRepository
                            ) as T
                        }
                    }
                )

                val uiState =
                    viewModel.uiState.collectAsState().value

                val soundManager = remember {
                    SoundManager(context)
                }

                val vibrationManager = remember {
                    VibrationManager(context)
                }

                val adManager = remember {
                    AdManager(context)
                }

                val mediaPlayer = remember {
                    MediaPlayer.create(
                        context,
                        R.raw.xtremefreddy_loop1
                    )?.apply {
                        isLooping = true
                    }
                }

                DisposableEffect(Unit) {

                    onDispose {

                        mediaPlayer?.release()
                        soundManager.release()
                    }
                }

                LaunchedEffect(
                    uiState.isMusicEnabled
                ) {

                    if (uiState.isMusicEnabled) {

                        if (
                            mediaPlayer != null &&
                            !mediaPlayer.isPlaying
                        ) {
                            mediaPlayer.start()
                        }

                    } else {

                        if (
                            mediaPlayer != null &&
                            mediaPlayer.isPlaying
                        ) {
                            mediaPlayer.pause()
                        }
                    }
                }

                LaunchedEffect(Unit) {

                    viewModel.events.collect { event ->

                        when (event) {

                            GameEvent.MATCH -> {

                                if (
                                    uiState.isSoundEnabled
                                ) {
                                    soundManager.playMatch()
                                }

                                if (
                                    uiState.isVibrationEnabled
                                ) {
                                    vibrationManager.vibrate(
                                        50
                                    )
                                }
                            }

                            GameEvent.SWAP -> {

                                if (
                                    uiState.isSoundEnabled
                                ) {
                                    soundManager.playSwap()
                                }
                            }

                            GameEvent.LEVEL_UP -> {

                                if (
                                    uiState.isSoundEnabled
                                ) {
                                    soundManager.playLevelUp()
                                }

                                if (
                                    uiState.isVibrationEnabled
                                ) {
                                    vibrationManager.vibrate(
                                        200
                                    )
                                }

                                adManager.showInterstitial(
                                    this@MainActivity
                                ) {}
                            }

                            GameEvent.SPECIAL_EXPLOSION -> {

                                if (
                                    uiState.isSoundEnabled
                                ) {
                                    soundManager.playExplosion()
                                }

                                if (
                                    uiState.isVibrationEnabled
                                ) {
                                    vibrationManager.vibrate(
                                        100
                                    )
                                }
                            }

                            GameEvent.REQUEST_REWARDED_AD -> {

                                adManager.showRewarded(
                                    this@MainActivity
                                ) {
                                    viewModel.grantRewardMoves(
                                        5
                                    )
                                }
                            }

                            GameEvent.RATE_APP -> {

                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "market://details?id=$packageName"
                                    )
                                )

                                try {

                                    startActivity(intent)

                                } catch (
                                    e: Exception
                                ) {

                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                "https://play.google.com/store/apps/details?id=$packageName"
                                            )
                                        )
                                    )
                                }
                            }

                            GameEvent.GAME_OVER -> {
                                // GameScreen handles game-over UI
                            }

                            GameEvent.LEVEL_UP -> {
                                // Handled above
                            }
                        }
                    }
                }

                GameScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
