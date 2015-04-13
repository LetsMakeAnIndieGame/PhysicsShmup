//package com.mygdx.managers;
//
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.physics.box2d.World;
//import com.mygdx.game.actors.Enemy;
//import com.mygdx.game.actors.MoneyLoot;
//
///**
// * Created by Phil on 1/22/2015.
// */
//public class LootManager {
//    public static void generateEnemyLoot(Enemy source, World world) {
//        EntityManager.addToUpdate(new MoneyLoot((int) (Math.random() * 500000), new Texture("Entities/Actors/money.png"),
//                        world, source.getX() + source.getWidth() / 2, source.getY() + source.getHeight() / 2));
//        EntityManager.addToUpdate(new MoneyLoot((int) (Math.random() * 500000), new Texture("Entities/Actors/money.png"),
//                world, source.getX() + source.getWidth() / 2, source.getY() + source.getHeight() / 2));
//        EntityManager.addToUpdate(new MoneyLoot((int) (Math.random() * 500000), new Texture("Entities/Actors/money.png"),
//                world, source.getX() + source.getWidth() / 2, source.getY() + source.getHeight() / 2));
//    }
//}
