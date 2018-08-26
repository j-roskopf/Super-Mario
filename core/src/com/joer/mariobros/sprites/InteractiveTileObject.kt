package com.joer.mariobros.sprites

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.physics.box2d.*
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.screens.PlayScreen

abstract class InteractiveTileObject(private val screen: PlayScreen, rectangleMapObject: RectangleMapObject) {
    internal var body: Body
    internal var fixture: Fixture

    init {
        val bodyDef = BodyDef()
        val fixtureDef = FixtureDef()
        val polygonShape = PolygonShape()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set((rectangleMapObject.rectangle.x + rectangleMapObject.rectangle.width / 2f) / MarioBrosGame.PPM, (rectangleMapObject.rectangle.y + rectangleMapObject.rectangle.height / 2f) / MarioBrosGame.PPM)
        body = screen.world.createBody(bodyDef)
        polygonShape.setAsBox((rectangleMapObject.rectangle.width / 2f) / MarioBrosGame.PPM, (rectangleMapObject.rectangle.height / 2f) / MarioBrosGame.PPM)
        fixtureDef.shape = polygonShape
        fixture = body.createFixture(fixtureDef)
    }

    abstract fun onHeadHit(mario: Mario)

    fun setCategoryFilter(filterBit: Short) {
        val filter = Filter()
        filter.categoryBits = filterBit
        fixture.filterData = filter
    }

    fun getCell(): TiledMapTileLayer.Cell {
        val layer = screen.tiledMap.layers.get(1) as TiledMapTileLayer
        val x = body.position.x * MarioBrosGame.PPM / 16 //16 is tile size
        val y = body.position.y * MarioBrosGame.PPM / 16
        return layer.getCell(x.toInt(), y.toInt())
    }
}