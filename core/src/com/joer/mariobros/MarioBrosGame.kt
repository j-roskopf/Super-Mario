package com.joer.mariobros

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.joer.mariobros.screens.PlayScreen

class MarioBrosGame : Game() {

    companion object {
        const val V_WIDTH = 400f
        const val V_HEIGHT = 208f
        const val PPM: Float = 100f

        const val NOTHING_BIT: Short = 0
        const val GROUND_BIT: Short = 1
        const val MARIO_BIT: Short = 2
        const val BRICK_BIT: Short = 4
        const val COIN_BIT: Short = 8
        const val DESTROYED_BIT: Short = 16
        const val OBJECT_BIT: Short = 32
        const val ENEMY_BIT: Short = 64
        const val ENEMY_HEAD_BIT: Short = 128
        const val ITEM_BIT: Short = 256
        const val MARIO_HEAD_BIT: Short = 512
    }
    lateinit var batch: SpriteBatch
    lateinit var assetManager: AssetManager

    override fun create() {
        batch = SpriteBatch()
        assetManager = AssetManager()

        assetManager.load("audio/music/mario_music.ogg", Music::class.java)
        assetManager.load("audio/sounds/coin.wav", Sound::class.java)
        assetManager.load("audio/sounds/bump.wav", Sound::class.java)
        assetManager.load("audio/sounds/breakblock.wav", Sound::class.java)
        assetManager.load("audio/sounds/powerup_spawn.wav", Sound::class.java)
        assetManager.load("audio/sounds/powerup.wav", Sound::class.java)
        assetManager.load("audio/sounds/stomp.wav", Sound::class.java)
        assetManager.load("audio/sounds/powerdown.wav", Sound::class.java)
        assetManager.load("audio/sounds/mariodie.wav", Sound::class.java)
        assetManager.finishLoading()

        setScreen(PlayScreen(this))
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assetManager.dispose()
    }
}
