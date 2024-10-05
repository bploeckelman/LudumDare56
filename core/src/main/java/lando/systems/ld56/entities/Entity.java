package lando.systems.ld56.entities;

import lando.systems.ld56.Main;

// Entity is just an unique identifier
// data for an entity is stored in 'entities/components/*' types
// that are attached to specific entity classes like Player, etc...
// EntityData methods can be used to perform lookups between entities and components
public class Entity {
    public static int NEXT_ID = 1;
    public final int id;

    public Entity() {
        this.id = NEXT_ID++;
        Main.game.entityData.addEntity(this);
    }
}
