package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.PhysicsShmup;

/**
 * Created by Phil on 1/18/2015.
 */
public class UIManager {
    private static Stage stage;
    private static Table table;
    private static Skin skin;

    private static Image healthBarBG;
    private static Image healthBarFG;

    private static Label cash;
    private static long cashAmount = 0;


    public static void createHUD() {
        Stack healthBarStack = new Stack();
        stage = new Stage();
//        stage.setDebugAll(true);
        table = new Table();
        skin = new Skin(Gdx.files.internal("UI/uiskin.json"));
        healthBarBG = new Image(new Texture("UI/healthbg.png"));
        healthBarFG = new Image(new Texture("UI/healthfg.png"));
        cash = new Label("", skin, "money-font", "green");

        table.setFillParent(true);

        table.align(Align.bottom).padBottom(10f).padLeft(20f).padRight(20f);
        healthBarStack.add(healthBarBG);
        healthBarStack.add(healthBarFG);

        table.add(healthBarStack).expandX().align(Align.left);
        table.add(cash);

        stage.addActor(table);

        PhysicsShmup.inputMultiplexer.addProcessor(stage);
    }

    public static void update() {
        String cashString = "₴";
        for (int index = 0; index < 8; index++) {
            try {
                cashString += " " + String.valueOf(cashAmount).charAt(index);
            } catch(Exception e) {
                break;
            }
        }
        cash.setText(cashString);
    }

    public static void setCash(long amount) {
        cashAmount = amount;
    }

    public static void render() {
        stage.act();
        stage.draw();
    }
}
