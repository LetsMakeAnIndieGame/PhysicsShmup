package com.mygdx.managers;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.game.pathfinding.GraphGenerator;
import com.mygdx.game.pathfinding.GraphImp;


public class LevelManager {
    public static int lvlTileWidth;
    public static int lvlTileHeight;
    public static int lvlPixelWidth;
    public static int lvlPixelHeight;
    public static int tilePixelWidth;
    public static int tilePixelHeight;
    public static TiledMap tiledMap;
    public static GraphImp groundGraph;
    public static GraphImp airGraph;

    public static void loadLevel(String filePath) {
        tiledMap = new TmxMapLoader().load(filePath);

        // get level width/height in both tiles and pixels and hang on to the values
        MapProperties properties = tiledMap.getProperties();
        lvlTileWidth = properties.get("width", Integer.class);
        lvlTileHeight = properties.get("height", Integer.class);
        tilePixelWidth = properties.get("tilewidth", Integer.class);
        tilePixelHeight = properties.get("tileheight", Integer.class);
        lvlPixelWidth = lvlTileWidth * tilePixelWidth;
        lvlPixelHeight = lvlTileHeight * tilePixelHeight;

        groundGraph = GraphGenerator.generateGroundGraph(tiledMap);
        airGraph = GraphGenerator.generateAirGraph(tiledMap);
    }
}
