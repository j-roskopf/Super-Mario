package com.joer.mariobros.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.joer.mariobros.MarioBrosGame
import com.joer.mariobros.items.Item
import com.joer.mariobros.items.ItemDefinition
import com.joer.mariobros.items.Mushroom
import com.joer.mariobros.scenes.Hud
import com.joer.mariobros.sprites.Mario
import com.joer.mariobros.sprites.State
import com.joer.mariobros.tools.Box2DWorldCreator
import com.joer.mariobros.tools.WorldContactListener
import java.util.concurrent.LinkedBlockingQueue

class PlayScreen(internal val game: MarioBrosGame) : Screen {

    private var camera: OrthographicCamera
    private var viewport: Viewport
    internal var hud: Hud
    private var mapLoader: TmxMapLoader
    internal var tiledMap: TiledMap
    private var renderer: OrthogonalTiledMapRenderer
    private val box2DWorldCreator: Box2DWorldCreator
    internal var world: World
    private var box2DDebugRenderer: Box2DDebugRenderer
    private var mario: Mario
    private var atlas: TextureAtlas
    private var music: Music
    private var items: ArrayList<Item>
    private var itemToSpawm: LinkedBlockingQueue<ItemDefinition>


    init {
        atlas = TextureAtlas("mario_and_enemies.atlas")

        camera = OrthographicCamera()

        viewport = FitViewport(MarioBrosGame.V_WIDTH / MarioBrosGame.PPM, MarioBrosGame.V_HEIGHT / MarioBrosGame.PPM, camera)

        hud = Hud(game.batch)

        mapLoader = TmxMapLoader()
        tiledMap = mapLoader.load("level_1.tmx")
        renderer = OrthogonalTiledMapRenderer(tiledMap, 1f / MarioBrosGame.PPM)

        camera.position.set(viewport.worldWidth / 2f, viewport.worldHeight / 2f, 0f)

        world = World(Vector2(0f, -10f), true)

        box2DDebugRenderer = Box2DDebugRenderer()

        box2DWorldCreator = Box2DWorldCreator(this)

        mario = Mario(world, this)

        world.setContactListener(WorldContactListener())

        music = game.assetManager.get("audio/music/mario_music.ogg", Music::class.java)
        //music.isLooping = true
        //music.play()

        items = ArrayList()
        itemToSpawm = LinkedBlockingQueue()
    }

    fun spawnItem(itemDefinition: ItemDefinition) {
        itemToSpawm.add(itemDefinition)
    }

    fun handleSpawningItems() {
        if(itemToSpawm.isNotEmpty()) {

            val itemDefinition = itemToSpawm.poll() as ItemDefinition
            if(itemDefinition.type == Mushroom::class.java) {
                items.add(Mushroom(this, itemDefinition.position.x, itemDefinition.position.y, world))
            }
        }
    }

    fun getAtlas(): TextureAtlas {
        return atlas
    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        update(delta)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderer.render()

        box2DDebugRenderer.render(world, camera.combined)

        game.batch.projectionMatrix = camera.combined

        game.batch.begin()
        mario.draw(game.batch)

        box2DWorldCreator.goombas.forEach {
            it.draw(game.batch)
        }

        box2DWorldCreator.turtles.forEach {
            it.draw(game.batch)
        }

        for(element in items) {
            element.draw(game.batch)
        }

        game.batch.end()

        game.batch.projectionMatrix = hud.stage.camera.combined
        hud.stage.draw()

        if(gameOver()) {
            game.screen = GameOverScreen(game)
            dispose()
        }
    }

    private fun update(delta: Float) {
        world.step(1f / 60f, 12, 4)

        val iterator = box2DWorldCreator.goombas.iterator()
        while(iterator.hasNext()) {
            val g = iterator.next()
            if(g.destroyed) {
                iterator.remove()
                world.destroyBody(g.body)
            }
        }

        val itemIterator = items.iterator()
        while(itemIterator.hasNext()) {
            val g = itemIterator.next()
            if(g.destroyed) {
                itemIterator.remove()
                world.destroyBody(g.body)
            }
        }

        handleInput(delta)
        handleSpawningItems()

        mario.update(delta)

        box2DWorldCreator.goombas.forEach {
            it.update(delta)
            if(it.x < mario.x + (224 / MarioBrosGame.PPM)) {
                it.body.isActive = true
            }
        }

        box2DWorldCreator.turtles.forEach {
            it.update(delta)
        }

        items.forEach {
            it.update(delta)
        }

        if(mario.currentState != State.DEAD) {
            hud.update(delta)

            camera.position.x = mario.body.position.x
        }

        camera.update()
        renderer.setView(camera)
    }

    private fun handleInput(delta: Float) {
        if(mario.currentState != State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                mario.body.applyLinearImpulse(Vector2(0f, 4f), mario.body.worldCenter, true)
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && mario.body.linearVelocity.x <= 2f) {
                mario.body.applyLinearImpulse(Vector2(0.1f, 0f), mario.body.worldCenter, true)
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && mario.body.linearVelocity.x >= -2f) {
                mario.body.applyLinearImpulse(Vector2(-0.1f, 0f), mario.body.worldCenter, true)
            }
        }
    }

    fun gameOver(): Boolean {
        return mario.currentState == State.DEAD && mario.stateTimer > 3
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        tiledMap.dispose()
        renderer.dispose()
        world.dispose()
        box2DDebugRenderer.dispose()
        hud.dispose()
        atlas.dispose()
        music.dispose()
    }


}