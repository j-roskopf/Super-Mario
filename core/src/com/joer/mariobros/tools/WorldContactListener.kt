package com.joer.mariobros.tools

import com.badlogic.gdx.physics.box2d.*
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.items.Item
import com.joer.mariobros.sprites.Enemy
import com.joer.mariobros.sprites.Goomba
import com.joer.mariobros.sprites.InteractiveTileObject
import com.joer.mariobros.sprites.Mario
import kotlin.experimental.or

class WorldContactListener: ContactListener {
    override fun endContact(contact: Contact) {

    }

    override fun beginContact(contact: Contact) {
        val firstFixture = contact.fixtureA
        val secondFixture = contact.fixtureB

        val cDef = firstFixture.filterData.categoryBits or secondFixture.filterData.categoryBits

        when(cDef) {
            MarioBrosGame.ENEMY_HEAD_BIT or MarioBrosGame.MARIO_BIT -> {
                if(firstFixture.filterData.categoryBits == MarioBrosGame.ENEMY_HEAD_BIT) {
                    //fixture a is enemy
                    if(firstFixture.userData is Goomba) {
                        (firstFixture.userData as Goomba).hitOnHead()
                    }
                } else {
                    //fixture b is enemy
                    if(secondFixture.userData is Goomba) {
                        (secondFixture.userData as Goomba).hitOnHead()
                    }
                }
            }

            MarioBrosGame.ENEMY_BIT or MarioBrosGame.OBJECT_BIT -> checkEnemyCollision(firstFixture, secondFixture, MarioBrosGame.ENEMY_BIT)
            MarioBrosGame.ENEMY_BIT or MarioBrosGame.ENEMY_BIT -> {
                //both should reverse
                if(firstFixture.userData is Enemy) {
                    (firstFixture.userData as Enemy).reverseVelocity(true, false)
                }
                if(secondFixture.userData is Enemy) {
                    (secondFixture.userData as Enemy).reverseVelocity(true, false)
                }
            }
            MarioBrosGame.ENEMY_BIT or MarioBrosGame.MARIO_BIT -> {
                if(firstFixture.filterData.categoryBits == MarioBrosGame.MARIO_BIT) {
                    (firstFixture.userData as Mario).hit()
                } else {
                    (secondFixture.userData as Mario).hit()
                }
            }

            MarioBrosGame.ITEM_BIT or MarioBrosGame.OBJECT_BIT -> {
                if(firstFixture.filterData.categoryBits == MarioBrosGame.ITEM_BIT) {
                    //fixture a is item
                    if(firstFixture.userData is Item) {
                        (firstFixture.userData as Item).reverseVelocity(true, false)
                    }
                } else {
                    //fixture b is item
                    if(secondFixture.userData is Item) {
                        (secondFixture.userData as Item).reverseVelocity(true, false)
                    }
                }
            }

            MarioBrosGame.ITEM_BIT or MarioBrosGame.MARIO_BIT -> {
                if(firstFixture.filterData.categoryBits == MarioBrosGame.ITEM_BIT) {
                    //fixture a is item
                    if(firstFixture.userData is Item) {
                        (secondFixture.userData as? Mario)?.let { (firstFixture.userData as Item).use(it) }
                    }
                } else {
                    //fixture b is item
                    if(secondFixture.userData is Item) {
                        (firstFixture.userData as? Mario)?.let { (secondFixture.userData as Item).use(it) }
                    }
                }
            }

            MarioBrosGame.MARIO_HEAD_BIT or MarioBrosGame.BRICK_BIT -> {
                if(firstFixture.filterData.categoryBits == MarioBrosGame.MARIO_HEAD_BIT) {
                    if(secondFixture.userData is InteractiveTileObject) {
                        (secondFixture.userData as InteractiveTileObject).onHeadHit(firstFixture.userData as Mario)
                    }
                } else {
                    if(firstFixture.userData is InteractiveTileObject) {
                        (firstFixture.userData as InteractiveTileObject).onHeadHit(secondFixture.userData as Mario)
                    }
                }
            }

            MarioBrosGame.MARIO_HEAD_BIT or MarioBrosGame.COIN_BIT -> {
                if(firstFixture.filterData.categoryBits == MarioBrosGame.MARIO_HEAD_BIT) {
                    if(secondFixture.userData is InteractiveTileObject) {
                        (secondFixture.userData as InteractiveTileObject).onHeadHit(firstFixture.userData as Mario)
                    }
                } else {
                    if(firstFixture.userData is InteractiveTileObject) {
                        (firstFixture.userData as InteractiveTileObject).onHeadHit(secondFixture.userData as Mario)
                    }
                }
            }

        }
    }

    fun checkEnemyCollision(firstFixture: Fixture, secondFixture: Fixture, bit: Short) {
        if(firstFixture.filterData.categoryBits == bit) {
            //fixture a is enemy
            if(firstFixture.userData is Enemy) {
                (firstFixture.userData as Enemy).reverseVelocity(true, false)
            }
        } else {
            //fixture b is enemy
            if(secondFixture.userData is Enemy) {
                (secondFixture.userData as Enemy).reverseVelocity(true, false)
            }
        }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
    }

}