package lando.systems.ld56.entities.components;

import lando.systems.ld56.Main;
import lando.systems.ld56.entities.Entity;

public class Component {

    public final Entity entity;

    public <T extends Component> Component(Entity entity, Class<T> componentClass) {
        this.entity = entity;
        Main.game.entityData.add(componentClass.cast(this), componentClass);
    }
}
