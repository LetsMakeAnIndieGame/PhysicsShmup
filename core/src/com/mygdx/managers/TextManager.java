import com.badlogic.gdx.graphics.g2d.BitmapFont;

//package com.mygdx.managers;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.math.Vector3;
//import com.mygdx.game.actors.GameObject;
//
//
//public class TextManager {
//    private static BitmapFont bfont = new BitmapFont();
//    private static SpriteBatch spriteBatchHandle;
//    private static OrthographicCamera orthoCam;
//
//    // refactor the name of this function, since it's not entirely accurate and it's capitalized
//    public static void SetSpriteBatch(SpriteBatch batch, OrthographicCamera cam) {
//        orthoCam = cam;
//        bfont.scale(2);
//        bfont.setColor(Color.CYAN);
//        spriteBatchHandle = batch;
//    }
//
//    public static void draw(java.lang.CharSequence msg, float x, float y) {
//        Vector3 position = new Vector3(x, y, 0);
//        orthoCam.unproject(position);
//        spriteBatchHandle.begin();
//        bfont.draw(spriteBatchHandle, msg, position.x, position.y);
//        spriteBatchHandle.end();
//    }
//
//    public static class DamageText implements GameObject {
//        private BitmapFont bfont;
//        private java.lang.CharSequence text;
//        private float x;
//        private float y;
//        private float transparency = 0;
//        private float gravity = 0.07f;
//        private Vector2 momentum = new Vector2((float) (Math.random() - 0.5d) * 2, 3);
//
//        public DamageText(java.lang.CharSequence msg, float x, float y) {
//            bfont = new BitmapFont(Gdx.files.internal("Entities/Scene2D/damage.fnt"));
//            text = msg;
//            this.x = (x - bfont.getBounds(msg).width / 2);
//            this.y = y;
//
//            EntityManager.addToUpdate(this);
//        }
//
//        public void update() {
//            x += momentum.x;
//            y += momentum.y;
//
//            momentum.y -= gravity;
//
//            transparency += 0.007f;
//            bfont.setColor(1, 1, 1, 1 - transparency);
//            if (transparency >= 1) {
//                EntityManager.setToDestroy(this);
//            }
//        }
//
//        public void render(Batch batch) {
//            bfont.draw(batch, text, x, y);
//        }
//
//        public void dispose() {
//            bfont.dispose();
//        }
//    }
//}
