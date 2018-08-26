package com.joer.mariobros.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.joer.mariobros.MarioBrosGame


class GameOverScreen(private val game: MarioBrosGame): Screen {

    var viewport: Viewport
    var stage: Stage

    init {
        viewport = FitViewport(MarioBrosGame.V_WIDTH, MarioBrosGame.V_HEIGHT, OrthographicCamera())
        stage = Stage(viewport, game.batch)

        val font = Label.LabelStyle(BitmapFont(), Color.WHITE)

        val table: Table = Table()
        table.center()
        table.setFillParent(true)

        val gameOverLabel = Label("GAME OVER", font)
        val playAgain = Label("Click to play again", font)

        table.add(gameOverLabel).expandX()
        table.row()

        table.add(playAgain).padTop(10f)

        stage.addActor(table)
    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun render(delta: Float) {

        if(Gdx.input.justTouched()) {
            game.screen = PlayScreen(game)
            dispose()
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.draw()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
        stage.dispose()
    }


}