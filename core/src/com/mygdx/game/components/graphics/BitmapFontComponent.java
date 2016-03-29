package com.mygdx.game.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Pool.Poolable;


public class BitmapFontComponent implements Component, Poolable {
    public BitmapFont bFont;
    public CharSequence msg;

    public BitmapFontComponent() {}

    public BitmapFontComponent(FileHandle handle, CharSequence msg) {
        bFont = new BitmapFont(handle);
        this.msg = msg;
    }

    @Override
    public void reset() {
        bFont = null;
        msg = null;
    }
}
