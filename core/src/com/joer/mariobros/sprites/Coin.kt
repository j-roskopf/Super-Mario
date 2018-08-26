package com.joer.mariobros.sprites

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileSet
import com.badlogic.gdx.math.Vector2
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.items.ItemDefinition
import com.joer.mariobros.items.Mushroom
import com.joer.mariobros.screens.PlayScreen

public class Coin(private val screen: PlayScreen, private val rectangleMapObject: RectangleMapObject) : InteractiveTileObject(screen, rectangleMapObject) {

    companion object {
        const val BLANK_COIN = 28 //id in tiled
    }

    private var tileSet: TiledMapTileSet


    init {
        fixture.userData = this
        setCategoryFilter(MarioBrosGame.COIN_BIT)

        tileSet = screen.tiledMap.tileSets.getTileSet("tileset_gutter")
    }

    override fun onHeadHit(mario: Mario) {
        println("hit coin with head")

        if(getCell().tile.id == BLANK_COIN) {
            screen.game.assetManager.get("audio/sounds/bump.wav", Sound::class.java).play()
        } else {
            if(rectangleMapObject.properties.containsKey("mushroom")) {
                screen.spawnItem(
                        ItemDefinition(
                                Vector2(body.position.x, body.position.y + 16 / MarioBrosGame.PPM),
                                Mushroom::class.java))

                screen.game.assetManager.get("audio/sounds/powerup_spawn.wav", Sound::class.java).play()
            } else {
                screen.game.assetManager.get("audio/sounds/coin.wav", Sound::class.java).play()
            }
        }

        getCell().tile = tileSet.getTile(BLANK_COIN)
        screen.hud.updateScore(100)
    }


}