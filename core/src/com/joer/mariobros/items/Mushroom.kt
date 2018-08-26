package com.joer.mariobros.items

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.screens.PlayScreen
import com.joer.mariobros.sprites.Mario
import kotlin.experimental.or

enum class State { GENERATED , NOT_GENERATED }

class Mushroom(screen: PlayScreen, xCord: Float, yCord: Float, private val world: World) : Item(screen, xCord, yCord, world) {

    var state : State = State.NOT_GENERATED

    init {
        setPosition(xCord, yCord)
        setBounds(x, y + 16 / com.joer.mariobros.MarioBrosGame.PPM, 16 / com.joer.mariobros.MarioBrosGame.PPM, 16 / com.joer.mariobros.MarioBrosGame.PPM)
        defineItem()

        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16)
        velocity = Vector2(0.7f, -1f)
    }

    override fun defineItem() {
        if(state == State.NOT_GENERATED) {
            state = State.GENERATED

            val bodyDef = BodyDef()
            bodyDef.position.set(x, y)
            bodyDef.type = BodyDef.BodyType.DynamicBody

            body = world.createBody(bodyDef)

            val fixtureDef = FixtureDef()
            val circle = CircleShape()
            circle.radius = 6f / MarioBrosGame.PPM

            fixtureDef.filter.categoryBits = MarioBrosGame.ITEM_BIT
            fixtureDef.filter.maskBits = MarioBrosGame.MARIO_BIT or MarioBrosGame.OBJECT_BIT or MarioBrosGame.GROUND_BIT or MarioBrosGame.COIN_BIT or MarioBrosGame.BRICK_BIT or MarioBrosGame.ITEM_BIT

            fixtureDef.shape = circle
            val fix = body.createFixture(fixtureDef)
            fix.userData = this
        }
    }

    override fun use(mario: Mario) {
        destroy()
        if(!mario.marioIsBig) {
            screen.game.assetManager.get("audio/sounds/powerup.wav", Sound::class.java).play()
            mario.grow()
        }

    }

    override fun update(delta: Float) {
        super.update(delta)
        setPosition(body.position.x - width / 2, body.position.y - height / 2)
        velocity.y = body.linearVelocity.y
        body.linearVelocity = velocity
    }
}