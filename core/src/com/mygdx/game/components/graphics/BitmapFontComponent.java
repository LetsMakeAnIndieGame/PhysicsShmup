package com.mygdx.game.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class BitmapFontComponent implements Component {
    public BitmapFont bFont;
    public CharSequence msg;

    public BitmapFontComponent(FileHandle handle, CharSequence msg) {
        bFont = new BitmapFont(handle);
        this.msg = msg;
    }
}
