package com.joer.mariobros.sprites

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.screens.PlayScreen
import kotlin.experimental.or

class Goomba(screen: PlayScreen, x: Float, y: Float): Enemy(screen, x, y) {
    private var stateTime: Float = 0f
    private var walkAnimation: Animation<TextureRegion>
    private var frames : com.badlogic.gdx.utils.Array<TextureRegion> = com.badlogic.gdx.utils.Array()
    private var setToDestory: Boolean = false
    internal var destroyed: Boolean = false

    init {
        setPosition(x, y)
        defineEnemy()

        for (i in 0 until 2) {
            frames.add(TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16))
        }

        walkAnimation = Animation(0.4f, frames)

        stateTime = 0f

        setBounds(x, y, 16f / MarioBrosGame.PPM, 16f / MarioBrosGame.PPM)

        body.isActive = false
    }

    fun update(delta: Float) {
        stateTime += delta

        if(setToDestory && !destroyed) {
            destroyed = true
            setRegion(TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16))
            stateTime = 0f
        } else if(!destroyed) {
            body.linearVelocity = velocity

            setPosition(body.position.x - width / 2, body.position.y - height / 2)
            setRegion(walkAnimation.getKeyFrame(stateTime, true))
        }
    }

    override fun defineEnemy() {
        val bodyDef = BodyDef()
        bodyDef.position.set(x, y)
        bodyDef.type = BodyDef.BodyType.DynamicBody

        body = screen.world.createBody(bodyDef)

        var fixtureDef = FixtureDef()
        val circle = CircleShape()
        circle.radius = 6f / MarioBrosGame.PPM

        fixtureDef.filter.categoryBits = MarioBrosGame.ENEMY_BIT // what it is
        fixtureDef.filter.maskBits = MarioBrosGame.GROUND_BIT or MarioBrosGame.COIN_BIT or MarioBrosGame.BRICK_BIT or MarioBrosGame.ENEMY_BIT or MarioBrosGame.OBJECT_BIT or MarioBrosGame.MARIO_BIT //what it collides with

        fixtureDef.shape = circle
        var fix = body.createFixture(fixtureDef)
        fix.userData = this

        //create the head
        val head = PolygonShape()

        val verts: Array<Vector2?> = arrayOfNulls(4)

        verts[0] = Vector2(-5f, 8f).scl(1f / MarioBrosGame.PPM)
        verts[1] = Vector2(5f, 8f).scl(1f / MarioBrosGame.PPM)
        verts[2] = Vector2(-3f, 3f).scl(1f / MarioBrosGame.PPM)
        verts[3] = Vector2(3f, 3f).scl(1f / MarioBrosGame.PPM)

        head.set(verts)

        fixtureDef.shape = head
        fixtureDef.restitution = 1f // bounciness when hitting head
        fixtureDef.filter.categoryBits = MarioBrosGame.ENEMY_HEAD_BIT

        fix = body.createFixture(fixtureDef)
        fix.userData = this
    }

    override fun draw(batch: Batch?) {
        if(!destroyed || stateTime < 1) {
            super.draw(batch)
        }
    }

    public override fun hitOnHead() {
        //cant delete any box 2d body here during mid collision render
        setToDestory = true
        screen.game.assetManager.get("audio/sounds/stomp.wav", Sound::class.java).play()
    }
}