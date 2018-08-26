package com.joer.mariobros.scenes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.joer.mariobros.MarioBrosGame

class Hud(private val spriteBatch: SpriteBatch): Disposable {
    internal var stage: Stage
    private var viewport: Viewport
    private var worldTimer: Int = 0
    private var timeCount: Float = 0f
    private var score = 0

    private var countdownLabel: Label
    private var scoreLabel: Label
    private var timeLabel: Label
    private var levelLabel: Label
    private var worldLabel: Label
    private var marioLabel: Label

    init {
        worldTimer = 300
        timeCount = 0f
        score = 0

        viewport = FitViewport(MarioBrosGame.V_WIDTH, MarioBrosGame.V_HEIGHT, OrthographicCamera())
        stage = Stage(viewport, spriteBatch)
6
        val table = Table()
        table.top()
        table.setFillParent(true)

        countdownLabel = Label(String.format("%03d", worldTimer), Label.LabelStyle(BitmapFont(), Color.WHITE))
        scoreLabel = Label(String.format("%06d", score), Label.LabelStyle(BitmapFont(), Color.WHITE))
        timeLabel = Label(timeCount.toString(), Label.LabelStyle(BitmapFont(), Color.WHITE))
        levelLabel = Label("1-1", Label.LabelStyle(BitmapFont(), Color.WHITE))
        worldLabel = Label("World 1", Label.LabelStyle(BitmapFont(), Color.WHITE))
        marioLabel = Label("Mario", Label.LabelStyle(BitmapFont(), Color.WHITE))

        table.add(marioLabel).expandX().padTop(10f)
        table.add(worldLabel).expandX().padTop(10f)
        table.add(timeLabel).expandX().padTop(10f)
        table.row()
        table.add(scoreLabel).expandX()
        table.add(levelLabel).expandX()
        table.add(countdownLabel).expandX()

        stage.addActor(table)
    }

    fun updateScore(value: Int) {
        score += value
        scoreLabel.setText((String.format("%06d", score)))
    }

    fun update(delta: Float) {
        timeCount += delta

        if(timeCount >= 1) {
            worldTimer--
            countdownLabel.setText(String.format("%03d", worldTimer))
            timeCount = 0f
        }
    }

    override fun dispose() {
        stage.dispose()
    }
}