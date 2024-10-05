package lando.systems.ld56.entities;

import com.badlogic.gdx.utils.Array;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Component;

import java.util.HashMap;
import java.util.Map;

public class EntityData {

    public final Array<Entity> entities = new Array<>();
    private final Map<Class<? extends Component>, Array<? extends Component>> componentsMap = new HashMap<>();

    public EntityData addEntity(Entity entity) {
        entities.add(entity);
        return this;
    }

    public <T extends Component> T get(Entity entity, Class<T> componentClass) {
        var components = getComponents(componentClass);
        for (var component : components) {
            if (component.entity.id == entity.id) {
                return component;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> Array<T> getComponents(Class<T> componentClass) {
        return (Array<T>) componentsMap.getOrDefault(componentClass, new Array<>());
    }

    public <T extends Component> EntityData add(T component, Class<T> componentClass) {
        componentsMap.putIfAbsent(componentClass, new Array<>());
        getComponents(componentClass).add(component);
        return this;
    }

    public <T extends Component> EntityData remove(T component, Class<T> componentClass) {
        getComponents(componentClass).removeValue(component, true);
        return this;
    }
}
