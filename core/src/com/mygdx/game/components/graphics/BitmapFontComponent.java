package com.mygdx.game.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

<<<<<<< HEAD

=======
/**
 * Created by Phil on 2/21/2015.
 */
>>>>>>> 437872d6f8d44f9dc3ffe938a1dca805f6282a1d
public class BitmapFontComponent implements Component {
    public BitmapFont bFont;
    public CharSequence msg;

    public BitmapFontComponent(FileHandle handle, CharSequence msg) {
        bFont = new BitmapFont(handle);
        this.msg = msg;
    }
}
