package lando.systems.ld56.entities.components;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import lando.systems.ld56.Main;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.utils.Util;
import text.formic.Stringf;

public class Component {

    public final Entity entity;

    public <T extends Component> Component(Entity entity, Class<T> componentClass) {
        this.entity = entity;
        if (!ClassReflection.isAssignableFrom(componentClass, this.getClass())) {
            Util.log("Reflection", Stringf.format("Can't cast %s to %s", this.getClass().getName(), componentClass.getName()));
        }
        Main.game.entityData.add((T) this, componentClass);
    }
}
