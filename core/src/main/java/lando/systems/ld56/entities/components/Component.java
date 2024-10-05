package lando.systems.ld56.entities.components;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import lando.systems.ld56.Main;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

public class Component {

    public final Entity entity;

    @SuppressWarnings("unchecked")
    public <T extends Component> Component(Entity entity, Class<T> componentClass) {
        this.entity = entity;
        if (!ClassReflection.isAssignableFrom(componentClass, this.getClass())) {
            Utils.log("Reflection", Stringf.format("Can't cast %s to %s", this.getClass().getName(), componentClass.getName()));
        }
        Main.game.entityData.add((T) this, componentClass);
    }
}
