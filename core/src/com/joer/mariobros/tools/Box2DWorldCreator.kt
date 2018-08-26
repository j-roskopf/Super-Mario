package com.joer.mariobros.tools

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.screens.PlayScreen
import com.joer.mariobros.sprites.*

class Box2DWorldCreator(playScreen: PlayScreen) {

    var goombas: ArrayList<Goomba>
    var turtles: ArrayList<Turtle>

    init {

        val bodyDef = BodyDef()
        val polygonShape = PolygonShape()
        val fixtureDef = FixtureDef()
        lateinit var body: Body

        //pipe
        for(objects in playScreen.tiledMap.layers.get(3).objects.getByType(RectangleMapObject::class.java)) {
            val rect = objects.rectangle

            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set((rect.x + rect.width / 2f) / MarioBrosGame.PPM, (rect.y + rect.height / 2f) / MarioBrosGame.PPM)

            body = playScreen.world.createBody(bodyDef)

            polygonShape.setAsBox((rect.width / 2f) / MarioBrosGame.PPM, (rect.height / 2f) / MarioBrosGame.PPM)
            fixtureDef.shape = polygonShape
            fixtureDef.filter.categoryBits = MarioBrosGame.OBJECT_BIT

            body.createFixture(fixtureDef)
        }

        //ground
        for(objects in playScreen.tiledMap.layers.get(2).objects.getByType(RectangleMapObject::class.java)) {
            val rect = objects.rectangle

            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set((rect.x + rect.width / 2f) / MarioBrosGame.PPM, (rect.y + rect.height / 2f) / MarioBrosGame.PPM)

            body = playScreen.world.createBody(bodyDef)

            polygonShape.setAsBox((rect.width / 2f) / MarioBrosGame.PPM, (rect.height / 2f) / MarioBrosGame.PPM)
            fixtureDef.shape = polygonShape

            body.createFixture(fixtureDef)
        }

        //brick
        playScreen.tiledMap.layers.get(5).objects.getByType(RectangleMapObject::class.java).forEach {
            Brick(playScreen, it)
        }

        //coin
        playScreen.tiledMap.layers.get(4).objects.getByType(RectangleMapObject::class.java).forEach {
            Coin(playScreen, it)
        }

        goombas = ArrayList()
        for(objects in playScreen.tiledMap.layers.get(6).objects.getByType(RectangleMapObject::class.java)) {
            val rect = objects.rectangle
            goombas.add(Goomba(playScreen, rect.x / MarioBrosGame.PPM, rect.y / MarioBrosGame.PPM))
        }

        turtles = ArrayList()
        for(objects in playScreen.tiledMap.layers.get(7).objects.getByType(RectangleMapObject::class.java)) {
            val rect = objects.rectangle
            turtles.add(Turtle(playScreen, rect.x / MarioBrosGame.PPM, rect.y / MarioBrosGame.PPM))
        }
    }

    fun getAllEnemies() : List<Enemy> {
        return goombas + turtles
    }
}