package lando.systems.ld56.entities.components;

import lando.systems.ld56.entities.Entity;

public class Component {
    public final Entity entity;

    public Component(Entity entity) {
        this.entity = entity;
    }
}
