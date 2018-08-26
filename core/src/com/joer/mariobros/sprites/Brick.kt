package com.joer.mariobros.sprites

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.screens.PlayScreen

public class Brick(private val screen: PlayScreen, rectangleMapObject: RectangleMapObject) : InteractiveTileObject(screen, rectangleMapObject) {

    init {
        fixture.userData = this
        setCategoryFilter(MarioBrosGame.BRICK_BIT)
    }

    override fun onHeadHit(mario: Mario) {
        if(mario.marioIsBig) {
            println("hit brick with head")
            setCategoryFilter(MarioBrosGame.DESTROYED_BIT)
            getCell().tile = null //removes the image

            screen.hud.updateScore(300)

            screen.game.assetManager.get("audio/sounds/breakblock.wav", Sound::class.java).play()
        } else {
            screen.game.assetManager.get("audio/sounds/bump.wav", Sound::class.java).play()
        }

    }


}