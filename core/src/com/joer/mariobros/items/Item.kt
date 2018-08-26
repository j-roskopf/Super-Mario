package com.joer.mariobros.items

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.joer.mariobros.screens.PlayScreen
import com.joer.mariobros.sprites.Mario

abstract class Item(var screen: PlayScreen, xCord: Float, yCord: Float, private val world: World) : Sprite() {
    internal lateinit var velocity: Vector2
    private var toDestroy: Boolean = false
    internal var destroyed: Boolean = false
    internal lateinit var body: Body


    abstract fun defineItem()
    abstract fun use(mario: Mario)

    open fun update(delta: Float) {
        if(toDestroy && !destroyed) {
            destroyed = true
        }
    }

    fun destroy() {
        toDestroy = true
    }

    override fun draw(batch: Batch?) {
        if(!destroyed) {
            super.draw(batch)
        }
    }

    fun reverseVelocity(x: Boolean, y: Boolean) {
        if (x) {
            velocity.x = -velocity.x
        } else if (y) {
            velocity.y = -velocity.y
        }
    }
}