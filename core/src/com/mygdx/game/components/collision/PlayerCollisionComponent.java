package com.mygdx.game.components.collision;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.mygdx.game.actors.Collidable;
import com.mygdx.game.components.MoneyComponent;
import com.mygdx.game.components.PlayerDataComponent;
import com.mygdx.managers.PhysicsManager;
import com.mygdx.managers.UIManager;

/**
 * Created by Phil on 2/16/2015.
 */
public class PlayerCollisionComponent extends Component implements Collidable {
    // This is what happens when you're a bad programmer and/or lazy

    @Override
    public void handleCollision(Engine engine, Entity collider, Entity collidee) {
        short type = collidee.getComponent(TypeComponent.class).type;

        if (type == PhysicsManager.COL_MONEY) {
            long moneyVal = collidee.getComponent(MoneyComponent.class).value;
            PlayerDataComponent data = collider.getComponent(PlayerDataComponent.class);
            data.money += moneyVal;
            UIManager.setCash(data.money);
        }
    }
}
