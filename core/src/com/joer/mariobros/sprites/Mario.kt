package com.joer.mariobros.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.screens.PlayScreen
import kotlin.experimental.or


class Mario(private val world: World, private val playScreen: PlayScreen) : Sprite(playScreen.getAtlas().findRegion("little_mario")) {

    lateinit var body: Body
    var marioStand: TextureRegion

    var currentState: State
    var previousState: State

    var marioRun: Animation<TextureRegion>
    var marioJump: TextureRegion
    var growMario: Animation<TextureRegion>
    var bigMarioRun: Animation<TextureRegion>

    private var bigMarioJump: TextureRegion
    private var bigMarioStand: TextureRegion
    private var marioDead: TextureRegion

    private var runningRight = false
    var stateTimer = 0f

    var marioIsBig = false
    var runGrowAnimation = false
    var timeToDefineBigMario = false
    var timeToRedefineMario = false
    var marioIsDead = false

    init {
        defineMario()

        currentState = State.STANDING
        previousState = State.STANDING

        val frames = Array<TextureRegion>()
        for (i in 1 until 4) {
            frames.add(TextureRegion(playScreen.getAtlas().findRegion("little_mario"), i * 32, 0, 16, 16))
        }
        marioRun = Animation(0.1f, frames)
        frames.clear()

        for (i in 1 until 4) {
            frames.add(TextureRegion(playScreen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32))
        }
        bigMarioRun = Animation(0.1f, frames)
        frames.clear()

        //mario grow
        frames.add(TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32))
        frames.add(TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32))
        frames.add(TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32))
        frames.add(TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32))
        growMario = Animation(0.2f, frames)

        marioJump = TextureRegion(playScreen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16)
        bigMarioJump = TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32)
        marioDead = TextureRegion(playScreen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16)

        marioStand = TextureRegion(playScreen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16)
        bigMarioStand = TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32)

        setBounds(0f, 0f, 16f / MarioBrosGame.PPM, 16f / MarioBrosGame.PPM)
        setRegion(marioStand)
    }

    private fun defineMario() {
        val bodyDef = BodyDef()

        bodyDef.position.set(32f / MarioBrosGame.PPM, 32f / MarioBrosGame.PPM)
        bodyDef.type = BodyDef.BodyType.DynamicBody

        body = world.createBody(bodyDef)

        var fixtureDef = FixtureDef()
        val circle = CircleShape()
        circle.radius = 6f / MarioBrosGame.PPM

        fixtureDef.filter.categoryBits = MarioBrosGame.MARIO_BIT
        fixtureDef.filter.maskBits = MarioBrosGame.GROUND_BIT or
                MarioBrosGame.COIN_BIT or
                MarioBrosGame.BRICK_BIT or
                MarioBrosGame.ENEMY_BIT or
                MarioBrosGame.OBJECT_BIT or
                MarioBrosGame.ENEMY_HEAD_BIT or
                MarioBrosGame.ITEM_BIT

        fixtureDef.shape = circle
        body.createFixture(fixtureDef).userData = this

        val feet = EdgeShape()
        feet.set(Vector2(-2 / MarioBrosGame.PPM, -6 / MarioBrosGame.PPM), Vector2(2 / MarioBrosGame.PPM, -6 / MarioBrosGame.PPM))
        fixtureDef.shape = feet

        body.createFixture(fixtureDef).userData = this

        val head = EdgeShape()
        head.set(Vector2(-2f / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM), Vector2(2f / MarioBrosGame.PPM, 6f / MarioBrosGame.PPM))
        fixtureDef.shape = head

        fixtureDef.filter.categoryBits = MarioBrosGame.MARIO_HEAD_BIT

        fixtureDef.isSensor = true

        body.createFixture(fixtureDef).userData = this
    }

    fun update(delta: Float) {
        if(marioIsBig) {
            setPosition(body.position.x - width / 2f, body.position.y - height / 2f - (6f / MarioBrosGame.PPM))
        } else {
            setPosition(body.position.x - width / 2f, body.position.y - height / 2f)
        }
        setRegion(getFrame(delta))

        if(timeToDefineBigMario) {
            defineBigMario()
        } else if(timeToRedefineMario) {
            reDefineMario()
        }

        Gdx.app.log("D","position = ${body.linearVelocity.y}")

        if(body.position.y < 0 && !marioIsDead) {
            killMario(8f)
        }
    }

    fun reDefineMario() {
        val currentPosition = Vector2(body.position)
        world.destroyBody(body)

        val bodyDef = BodyDef()
        bodyDef.position.set(currentPosition)
        bodyDef.type = BodyDef.BodyType.DynamicBody

        body = world.createBody(bodyDef)

        var fixtureDef = FixtureDef()
        val circle = CircleShape()
        circle.radius = 6f / MarioBrosGame.PPM

        fixtureDef.filter.categoryBits = MarioBrosGame.MARIO_BIT
        fixtureDef.filter.maskBits = MarioBrosGame.GROUND_BIT or
                MarioBrosGame.COIN_BIT or
                MarioBrosGame.BRICK_BIT or
                MarioBrosGame.ENEMY_BIT or
                MarioBrosGame.OBJECT_BIT or
                MarioBrosGame.ENEMY_HEAD_BIT or
                MarioBrosGame.ITEM_BIT

        fixtureDef.shape = circle

        body.createFixture(fixtureDef).userData = this

        val feet = EdgeShape()
        feet.set(Vector2(-2 / MarioBrosGame.PPM, -6 / MarioBrosGame.PPM), Vector2(2 / MarioBrosGame.PPM, -6 / MarioBrosGame.PPM))
        fixtureDef.shape = feet

        body.createFixture(fixtureDef).userData = this

        val head = EdgeShape()
        head.set(Vector2(-2f / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM), Vector2(2f / MarioBrosGame.PPM, 6f / MarioBrosGame.PPM))
        fixtureDef.shape = head

        fixtureDef.filter.categoryBits = MarioBrosGame.MARIO_HEAD_BIT

        fixtureDef.isSensor = true

        body.createFixture(fixtureDef).userData = this

        timeToRedefineMario = false
    }

    fun defineBigMario() {
        val currentPosition = Vector2(body.position)
        world.destroyBody(body)

        val bodyDef = BodyDef()
        bodyDef.position.set(currentPosition.add(0f, 10 / MarioBrosGame.PPM))
        bodyDef.type = BodyDef.BodyType.DynamicBody

        body = world.createBody(bodyDef)

        var fixtureDef = FixtureDef()
        val circle = CircleShape()
        circle.radius = 6f / MarioBrosGame.PPM

        fixtureDef.filter.categoryBits = MarioBrosGame.MARIO_BIT
        fixtureDef.filter.maskBits = MarioBrosGame.GROUND_BIT or
                MarioBrosGame.COIN_BIT or
                MarioBrosGame.BRICK_BIT or
                MarioBrosGame.ENEMY_BIT or
                MarioBrosGame.OBJECT_BIT or
                MarioBrosGame.ENEMY_HEAD_BIT or
                MarioBrosGame.ITEM_BIT

        fixtureDef.shape = circle

        body.createFixture(fixtureDef).userData = this
        circle.position = Vector2(0f, -14f / MarioBrosGame.PPM)
        body.createFixture(fixtureDef).userData = this

        val feet = EdgeShape()
        feet.set(Vector2(-2 / MarioBrosGame.PPM, -6 / MarioBrosGame.PPM), Vector2(2 / MarioBrosGame.PPM, -6 / MarioBrosGame.PPM))
        fixtureDef.shape = feet

        body.createFixture(fixtureDef).userData = this

        val head = EdgeShape()
        head.set(Vector2(-2f / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM), Vector2(2f / MarioBrosGame.PPM, 6f / MarioBrosGame.PPM))
        fixtureDef.shape = head

        fixtureDef.filter.categoryBits = MarioBrosGame.MARIO_HEAD_BIT

        fixtureDef.isSensor = true

        body.createFixture(fixtureDef).userData = this

        timeToDefineBigMario = false
    }

    private fun getFrame(delta: Float): TextureRegion {
        currentState = getState()

        val region =  when(currentState) {
            State.DEAD -> marioDead
            State.JUMPING -> if(marioIsBig) bigMarioJump else marioJump
            State.RUNNING -> if(marioIsBig) bigMarioRun.getKeyFrame(stateTimer, true) else marioRun.getKeyFrame(stateTimer, true)
            State.FALLING -> if(marioIsBig) bigMarioStand else marioStand
            State.STANDING -> if(marioIsBig) bigMarioStand else marioStand
            State.GROWING -> {
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false
                }
                growMario.getKeyFrame(stateTimer, false)
            }
        } as TextureRegion

        if((body.linearVelocity.x < 0 || !runningRight) && !region.isFlipX) {
            region.flip(true, false)
            runningRight = false
        } else if ((body.linearVelocity.x > 0 || runningRight) && region.isFlipX) {
            region.flip(true, false)
            runningRight = true
        }

        if(currentState == previousState) {
            stateTimer += delta
        } else {
            stateTimer = 0f
        }

        previousState = currentState

        return region
    }

    private fun getState(): State {
        return if(marioIsDead) {
            State.DEAD
        } else if(runGrowAnimation) {
            State.GROWING
        } else if(body.linearVelocity.y > 0f || (body.linearVelocity.y < 0 && previousState == State.JUMPING)) {
            State.JUMPING
        }else if(body.linearVelocity.y < 0f) {
            State.FALLING
        } else if(body.linearVelocity.x != 0f) {
            State.RUNNING
        } else {
            State.STANDING
        }
    }

    fun grow() {
        runGrowAnimation = true
        marioIsBig = true
        timeToDefineBigMario = true
        setBounds(x, y, width, height + 16 / MarioBrosGame.PPM)
    }

    fun hit() {
        if(marioIsBig) {
            marioIsBig = false
            timeToRedefineMario = true
            setBounds(x, y, width, height / 2)
            playScreen.game.assetManager.get("audio/sounds/powerdown.wav", Sound::class.java).play()
        } else {
            killMario(4f)
        }
    }

    fun killMario(yForce: Float) {
        playScreen.game.assetManager.get("audio/music/mario_music.ogg", Music::class.java).stop()
        playScreen.game.assetManager.get("audio/sounds/mariodie.wav", Sound::class.java).play()
        marioIsDead = true
        val filter = Filter()
        filter.maskBits = MarioBrosGame.NOTHING_BIT
        body.fixtureList.forEach {
            it.filterData = filter
        }
        body.applyLinearImpulse(Vector2(0f, yForce), body.worldCenter, true)
    }
}

enum class State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD }