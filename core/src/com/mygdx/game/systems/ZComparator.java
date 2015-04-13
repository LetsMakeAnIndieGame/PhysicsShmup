package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.mygdx.game.components.physics.PositionComponent;

import java.util.Comparator;

/**
 * Created by Phil on 3/3/2015.
 */
public class ZComparator implements Comparator<Entity> {
    private ComponentMapper<PositionComponent> posMap = ComponentMapper.getFor(PositionComponent.class);

    @Override
    public int compare(Entity e1, Entity e2) {
        return (int)Math.signum(posMap.get(e1).z - posMap.get(e2).z);
    }
}
