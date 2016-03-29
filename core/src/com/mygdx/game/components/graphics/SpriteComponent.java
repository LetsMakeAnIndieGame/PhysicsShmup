package com.mygdx.game.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;


public class SpriteComponent implements Component, Poolable {
    public Array<Sprite> sprites = new Array<Sprite>();

    public SpriteComponent() {}

    public SpriteComponent(Texture...textures) {
        addTextures(textures);
    }

    public void addTextures(Texture...textures) {
        for (Texture texture : textures)
            sprites.add(new Sprite(texture));
    }

    @Override
    public void reset() {
        sprites.clear();
    }
}
