package com.joer.mariobros.sprites

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.screens.PlayScreen
import kotlin.experimental.or

public enum class TutleState {WALKING, SHELL}

class Turtle(screen: PlayScreen, x: Float, y: Float): Enemy(screen, x, y) {

    private var currentState : TutleState = TutleState.SHELL
    private var previousState : TutleState = TutleState.SHELL
    private var stateTime: Float = 0f
    private var walkAnimation: Animation<TextureRegion>
    private var shell: TextureRegion
    private var frames : com.badlogic.gdx.utils.Array<TextureRegion> = com.badlogic.gdx.utils.Array()
    private var setToDestory: Boolean = false
    internal var destroyed: Boolean = false

    init {
        setPosition(x, y)
        defineEnemy()
        frames.add(TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24))
        frames.add(TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24))
        shell = TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24)

        walkAnimation = Animation(0.2f, frames)

        setBounds(x, y, 16 / MarioBrosGame.PPM, 24 / MarioBrosGame.PPM)
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

    override fun hitOnHead() {
        if(currentState != TutleState.SHELL) {
            currentState = TutleState.SHELL
            velocity.x = 0f
        }
    }

    fun getFrame(delta: Float): TextureRegion {
        var region = if(currentState == TutleState.SHELL) shell else walkAnimation.getKeyFrame(stateTime, true)

        if(velocity.x > 0 && !region.isFlipX) {
            //walking to the right
            region.flip(true, false)
        }

        if(velocity.x <= 0 && region.isFlipX) {
            //walking to the right
            region.flip(true, false)
        }

        if(currentState == previousState) {
            stateTime += delta
        } else {
            stateTime = 0f
        }

        previousState = currentState

        return region
    }

    fun update(delta: Float) {
        setRegion(getFrame(delta))

        if(currentState == TutleState.SHELL && stateTime > 5) {
            currentState = TutleState.WALKING
            velocity.x = 1f
        } else if(currentState == TutleState.SHELL) {
            velocity.x = 0f
        }

        setPosition(body.position.x - width / 2 , body.position.y - 8 / MarioBrosGame.PPM)
        body.linearVelocity = velocity
    }

}