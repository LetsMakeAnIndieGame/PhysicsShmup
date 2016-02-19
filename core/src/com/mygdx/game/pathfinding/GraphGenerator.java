package com.mygdx.game.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.mygdx.managers.LevelManager;


public class GraphGenerator {
    public static GraphImp generateGroundGraph(TiledMap map) {
        Array<Node> nodes = new Array<Node>();

        TiledMapTileLayer tiles = (TiledMapTileLayer)map.getLayers().get(0);
        int mapHeight = LevelManager.lvlTileHeight;
        int mapWidth = LevelManager.lvlTileWidth;

        // Loops over the tiles in the map, starting from bottom left corner
        // iterating left to right, then down to up
        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                // generate a node for each tile so that they all exist when we create connections
                Node node = new Node();
                node.type = Node.Type.REGULAR;
                nodes.add(node);
            }
        }

        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                TiledMapTileLayer.Cell target = tiles.getCell(x, y);
                TiledMapTileLayer.Cell up = tiles.getCell(x, y+1);
                TiledMapTileLayer.Cell left = tiles.getCell(x-1, y);
                TiledMapTileLayer.Cell right = tiles.getCell(x+1, y);
                TiledMapTileLayer.Cell down = tiles.getCell(x, y-1);

                Node targetNode = nodes.get(mapWidth * y + x);
                if (target == null) {
                    if (y != 0 && down == null) {
                        Node downNode = nodes.get(mapWidth * (y - 1) + x);
                        targetNode.createConnection(downNode, 1);
                    }
                    if (x != 0 && left == null) {
                        Node leftNode = nodes.get(mapWidth * y + x - 1);
                        targetNode.createConnection(leftNode, 1);
                    }
                    if (x != mapWidth - 1 && right == null) {
                        Node rightNode = nodes.get(mapWidth * y + x + 1);
                        targetNode.createConnection(rightNode, 1);
                    }
                    if (y != mapHeight - 1 && up == null) {
                        Node upNode = nodes.get(mapWidth * (y + 1) + x);
                        targetNode.createConnection(upNode, 1);
                    }
                }
            }
        }

        return new GraphImp(nodes);
    }

    public static GraphImp generateAirGraph(TiledMap map) {
        Array<Node> nodes = new Array<Node>();

        TiledMapTileLayer tiles = (TiledMapTileLayer)map.getLayers().get(0);
        int mapHeight = LevelManager.lvlTileHeight;
        int mapWidth = LevelManager.lvlTileWidth;

        // Loops over the tiles in the map, starting from bottom left corner
        // iterating left to right, then down to up
        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                // generate a node for each tile so that they all exist when we create connections
                Node node = new Node();
                node.type = Node.Type.REGULAR;
                nodes.add(node);
            }
        }

        Gdx.app.log("Size: ", "" + nodes.size);

        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                TiledMapTileLayer.Cell target = tiles.getCell(x, y);
                TiledMapTileLayer.Cell up = tiles.getCell(x, y+1);
                TiledMapTileLayer.Cell upLeft = tiles.getCell(x-1, y+1);
                TiledMapTileLayer.Cell upRight = tiles.getCell(x+1, y+1);
                TiledMapTileLayer.Cell left = tiles.getCell(x-1, y);
                TiledMapTileLayer.Cell right = tiles.getCell(x+1, y);
                TiledMapTileLayer.Cell down = tiles.getCell(x, y-1);
                TiledMapTileLayer.Cell downLeft = tiles.getCell(x-1, y-1);
                TiledMapTileLayer.Cell downRight = tiles.getCell(x+1, y-1);

                Node targetNode = nodes.get(mapWidth * y + x);
                if (target == null) {
                    if (y != 0 && down == null) {
                        Node downNode = nodes.get(mapWidth * (y - 1) + x);
                        targetNode.createConnection(downNode, 1);
                    }
                    if (x != 0 && y != 0 && downLeft == null) {
                        Node downLeftNode = nodes.get(mapWidth * (y - 1) + (x - 1));
                        targetNode.createConnection(downLeftNode, 1.7f);
                    }
                    if (x != mapWidth - 1 && y != 0 && downRight == null) {
                        Node downRightNode = nodes.get(mapWidth * (y - 1) + (x + 1));
                        targetNode.createConnection(downRightNode, 1.7f);
                    }
                    if (x != 0 && left == null) {
                        Node leftNode = nodes.get(mapWidth * y + x - 1);
                        targetNode.createConnection(leftNode, 1);
                    }
                    if (x != mapWidth - 1 && right == null) {
                        Node rightNode = nodes.get(mapWidth * y + x + 1);
                        targetNode.createConnection(rightNode, 1);
                    }
                    if (y != mapHeight - 1 && up == null) {
                        Node upNode = nodes.get(mapWidth * (y + 1) + x);
                        targetNode.createConnection(upNode, 1);
                    }
                    if (x != 0 && y != mapHeight - 1 && upLeft == null) {
                        Node upLeftNode = nodes.get(mapWidth * (y + 1) + (x - 1));
                        targetNode.createConnection(upLeftNode, 1.7f);
                    }
                    if (x != mapWidth - 1 && y != mapHeight - 1 && upRight == null) {
                        Node upRightNode = nodes.get(mapWidth * (y + 1) + (x + 1));
                        targetNode.createConnection(upRightNode, 1.7f);
                    }
                }
            }
        }

        return new GraphImp(nodes);
    }
}
