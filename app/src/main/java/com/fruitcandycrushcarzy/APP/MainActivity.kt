package com.fruitcandycrushcarzy.APP

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.media.MediaPlayer
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fruitcandycrushcarzy.APP.game.data.ScoreRepository
import com.fruitcandycrushcarzy.APP.game.util.SoundManager
import com.fruitcandycrushcarzy.APP.game.util.VibrationManager
import com.fruitcandycrushcarzy.APP.game.viewmodel.GameEvent
import com.fruitcandycrushcarzy.APP.game.viewmodel.GameViewModel
import com.fruitcandycrushcarzy.APP.ui.GameScreen
import com.fruitcandycrushcarzy.APP.ui.theme.FRUITCANDYCRUSHCARZYTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FRUITCANDYCRUSHCARZYTheme {
                val context = LocalContext.current
                val scoreRepository = remember { ScoreRepository(context) }
                
                val viewModel: GameViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return GameViewModel(scoreRepository) as T
                        }
                    }
                )
                val uiState = viewModel.uiState.collectAsState().value
                
                val soundManager = remember { SoundManager(context) }
                val vibrationManager = remember { VibrationManager(context) }
                
                // Background Music
                val mediaPlayer = remember {
                    MediaPlayer.create(context, R.raw.xtremefreddy_loop1).apply {
                        isLooping = true
                    }
                }

                DisposableEffect(Unit) {
                    onDispose {
                        mediaPlayer.release()
                        soundManager.release()
                    }
                }

                LaunchedEffect(uiState.isMusicEnabled) {
                    if (uiState.isMusicEnabled) {
                        mediaPlayer.start()
                    } else {
                        mediaPlayer.pause()
                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            GameEvent.MATCH -> {
                                if (uiState.isSoundEnabled) soundManager.playMatch()
                                if (uiState.isVibrationEnabled) vibrationManager.vibrate(50)
                            }
                            GameEvent.SWAP -> {
                                if (uiState.isSoundEnabled) soundManager.playSwap()
                            }
                            GameEvent.LEVEL_UP -> {
                                if (uiState.isSoundEnabled) soundManager.playLevelUp()
                                if (uiState.isVibrationEnabled) vibrationManager.vibrate(200)
                            }
                            GameEvent.SPECIAL_EXPLOSION -> {
                                if (uiState.isSoundEnabled) soundManager.playExplosion()
                                if (uiState.isVibrationEnabled) vibrationManager.vibrate(100)
                            }
                            else -> {}
                        }
                    }
                }

                GameScreen(viewModel = viewModel)
            }
        }
    }
}
