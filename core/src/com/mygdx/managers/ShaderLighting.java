package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.game.components.physics.PositionComponent;

public class ShaderLighting {
    public static ShaderProgram createShader(String vert, String frag) {
        ShaderProgram prog = new ShaderProgram(vert, frag);
        if (!prog.isCompiled())
            throw new GdxRuntimeException("could not compile shader: " + prog.getLog());
        if (prog.getLog().length() != 0)
            Gdx.app.log("GpuShadows", prog.getLog());
        return prog;
    }

    // need graphics dimensions, occluders FBO, texture region, camera, batch, tiledMapRenderer
    private int width, height,
            lightSize = 1024;
    private float upScale = 1.0f;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TiledMapRenderer tiledMapRenderer;

    private ShaderProgram shadowMapShader;
    private ShaderProgram lightShader;

    private FrameBuffer occludersFBO = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, lightSize, false);
    private TextureRegion occludersTex = new TextureRegion(occludersFBO.getColorBufferTexture());
    private FrameBuffer shadowMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, 1, false);
    private TextureRegion shadowMapTex = new TextureRegion(shadowMapFBO.getColorBufferTexture());

    private final String VERT_PASS_SRC = Gdx.files.internal("Shaders/pass.vert").readString();
    private final String SHADOW_MAP_FRAG_SRC = Gdx.files.internal("Shaders/shadowMap.frag").readString();
    private final String LIGHT_FRAG_SRC = Gdx.files.internal("Shaders/shadowRender.frag").readString();

    private final boolean softShadows = true;

    public ShaderLighting(OrthographicCamera camera, SpriteBatch batch, TiledMapRenderer tiledMapRenderer) {
        this.camera = camera;
        this.batch = batch;
        this.tiledMapRenderer = tiledMapRenderer;

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        shadowMapShader = createShader(VERT_PASS_SRC, SHADOW_MAP_FRAG_SRC);
        lightShader = createShader(VERT_PASS_SRC, LIGHT_FRAG_SRC);
    }

    public void RenderOccluders() {
        int[] fgLayer = {1};

        // bind the fbo to be drawn
        occludersFBO.begin();

        // clear buffer so that it's ready to draw
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // get ready to draw to the fbo
//        batch.begin();

        // we want the source of light to be center of player, hence the +25
        camera.setToOrtho(true, occludersFBO.getWidth(), occludersFBO.getHeight());
        camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x + 25, width / 2), LevelManager.lvlPixelWidth - (width / 2));
        camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y + 25, height / 2), LevelManager.lvlPixelHeight - (height / 2));
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        // render just the foreground
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(fgLayer);

        // end drawing
//        batch.end();

        // unbind the fbo
        occludersFBO.end();

        // Be kind and reset the camera to what it was prior to this
        camera.setToOrtho(false);
        camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
        camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
        camera.update();

        batch.setProjectionMatrix(camera.combined);

//        // debug render the occluders
//        camera.setToOrtho(false);
//        camera.update();
//        batch.setProjectionMatrix(camera.combined);
//
//        batch.begin();
//        batch.draw(occludersTex.getTexture(), 50, 50);
//        batch.end();
    }

    public void renderShadowMap() {
        // bind the FBO to draw to
        shadowMapFBO.begin();

        // clear the FBO to prepare for drawing to it
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // set the shader to the shadowMap shader so that the occluders turn into the shadowMap
        batch.setShader(shadowMapShader);

        // prepare to draw
        batch.begin();

        // The shader needs to know how big the light is and how scaled it should be
        shadowMapShader.setUniformf("resolution", lightSize, lightSize);
        shadowMapShader.setUniformf("upScale", upScale);

        // set the camera's matrices to match up to the shadowmapfbo
        camera.setToOrtho(false, shadowMapFBO.getWidth(), shadowMapFBO.getHeight());
        camera.update();

        // set the batch to draw to camera's dimensions
        batch.setProjectionMatrix(camera.combined);

        // draw the occluders to the fbo, via the shadowMapShader
        batch.draw(occludersTex.getTexture(), 0, 0, lightSize, shadowMapFBO.getHeight());

        // remove the shadowMapshader
        batch.setShader(null);

        // wrap up drawing
        batch.end();

        // unbind the FBO
        shadowMapFBO.end();

//        // debug render the shadowMap
//        camera.setToOrtho(false);
//        camera.update();
//        batch.setProjectionMatrix(camera.combined);
//
//        batch.begin();
//        batch.draw(shadowMapTex.getTexture(), 50, 50, lightSize, 50);
//        batch.end();

        // Be kind and reset the camera to what it was prior to this
        camera.setToOrtho(false);
        camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
        camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
        camera.update();

        batch.setProjectionMatrix(camera.combined);
    }

    public void renderLight() {
        // grab the player's position for convenience
        float x = EntityManager.getPlayer().getComponent(PositionComponent.class).x;
        float y = EntityManager.getPlayer().getComponent(PositionComponent.class).y;

        // light will render upside down if you don't reset to ortho with ydown
        camera.setToOrtho(true);
        camera.position.x = x - 25; // we need to shift the camera to center of player before rendering the light
        camera.position.y = y + 25;
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        // get size of light
        float finalSize = lightSize * upScale;

        // bind the shader to the batch so that light will draw
        batch.setShader(lightShader);

        // prep to draw the light
        batch.begin();

        // pass the information to the shader
        lightShader.setUniformf("resolution", lightSize, lightSize);
        lightShader.setUniformf("softShadows", softShadows ? 1.0f : 0.0f);

        // draw the shadowmap, via the shader, to create a light centered on the player
        batch.draw(shadowMapTex.getTexture(), x - finalSize / 2f, y - finalSize / 2f, finalSize, finalSize);

        // remove the shader
        batch.setShader(null);

        // clean the batch after drawing
        batch.end();

        // reset camera and batch to be polite
        camera.setToOrtho(false);

        camera.position.x = x;
        camera.position.y = y;
        camera.update();

        batch.setProjectionMatrix(camera.combined);
    }
}