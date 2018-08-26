package com.joer.mariobros.sprites

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.joer.mariobros.screens.PlayScreen

abstract class Enemy(var screen: PlayScreen, x: Float, y: Float) : Sprite() {

    lateinit var body: Body
    var velocity: Vector2 = Vector2(-1f, -2f)

    protected abstract fun defineEnemy()

    protected abstract fun hitOnHead()

    fun reverseVelocity(x: Boolean, y: Boolean) {
        if (x) {
            velocity.x = -velocity.x
        } else if (y) {
            velocity.y = -velocity.y
        }
    }
}