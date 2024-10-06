package lando.systems.ld56.entities;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import lando.systems.ld56.Main;

// Entity is just an unique identifier
// data for an entity is stored in 'entities/components/*' types
// that are attached to specific entity classes like Player, etc...
// EntityData methods can be used to perform lookups between entities and components
public class Entity {
    public static int NEXT_ID = 1;
    public final int id;
    public final String name;

    public Entity() {
        this.id = NEXT_ID++;
        this.name = ClassReflection.getSimpleName(this.getClass());
        Main.game.entityData.addEntity(this);
    }
}
