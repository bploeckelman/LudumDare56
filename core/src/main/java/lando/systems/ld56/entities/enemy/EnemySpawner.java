package lando.systems.ld56.entities.enemy;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.utils.Utils;

public class EnemySpawner {

   public enum EnemyType {
       TARDIGRADE  (Anims.Type.TARDIGRADE),
       BACTERIA    (Anims.Type.BACTERIA),
       BIRD        (Anims.Type.BIRD),
       ANIMAL      (Anims.Type.ANIMAL),
       TRUCK       (Anims.Type.TRUCK),
       PERSON      (Anims.Type.PERSON),
       GOOMBA      (Anims.Type.GOOMBA),
       BOWSER      (Anims.Type.BOWSER);

       public final Anims.Type anims;
       EnemyType(Anims.Type anims) {
           this.anims = anims;
       }
   }


    public enum BiomeEnemy {
        MICROBIOME_ENEMIES (Scene.Type.MICROBIOME, EnemyType.TARDIGRADE, EnemyType.BACTERIA),
        NEIGHBORHOOD_ENEMIES (Scene.Type.NEIGHBORHOOD, EnemyType.BIRD, EnemyType.ANIMAL),
        CITY_ENEMIES (Scene.Type.CITY, EnemyType.BIRD, EnemyType.PERSON, EnemyType.TRUCK),
//        MUSHROOM_KINGDOM_ENEMIES (Scene.Type.MUSHROOM_KINGDOM, EnemyType.GOOMBA, EnemyType.BOWSER)
        ;

        public final Scene.Type biome;
        public final EnemyType[] enemyTypes;
        BiomeEnemy(Scene.Type biome, EnemyType... enemyTypes) {
            this.biome = biome;
            this.enemyTypes = enemyTypes;
        }
    }


    private final Scene scene;
    public float maxSpawnTime = 4f;
    public float minSpawnTime = 1f;
    public int maxSpawn = 5;

    public EnemySpawner(Scene scene) {
        this.scene = scene;

        switch (scene.type) {
            case MICROBIOME:
                minSpawnTime = 5f;
                maxSpawnTime = 10;
                maxSpawn = 3;
                break;
            case CITY:
                minSpawnTime = 0.4f;
                maxSpawnTime = 3;
                break;
//            case MUSHROOM_KINGDOM:
//                minSpawnTime = 0.4f;
//                maxSpawnTime = 3;
//                break;
            case NEIGHBORHOOD:
            default:
                minSpawnTime = 0.4f;
                maxSpawnTime = 3;
                maxSpawn = 3;
                break;
        }

        spawnTime = nextSpawnTime();
    }

    public Enemy spawn() {
        switch (scene.type) {
            case MICROBIOME:
                return spawn(random(BiomeEnemy.MICROBIOME_ENEMIES));
            case CITY:
                return spawn(random(BiomeEnemy.CITY_ENEMIES));
//            case MUSHROOM_KINGDOM:
//                return spawn(random(BiomeEnemy.MUSHROOM_KINGDOM_ENEMIES));
            case NEIGHBORHOOD:
            default:
                return spawn(random(BiomeEnemy.NEIGHBORHOOD_ENEMIES));
        }
    }

    private EnemyType random(BiomeEnemy biomeEnemy) {
        return biomeEnemy.enemyTypes[MathUtils.random(biomeEnemy.enemyTypes.length -1)];
    }

    public Enemy spawn(EnemyType enemyType) {

        Enemy enemy;
        switch (enemyType) {
            case TARDIGRADE:
                enemy = createTardigrade();
                break;
            case BACTERIA:
                enemy = createBacteria();
                break;
            case BIRD:
                enemy = createBird();
                break;
            case ANIMAL:
                enemy = createAnimal();
                break;
            case TRUCK:
                enemy = createTruck();
                break;
            default:
                enemy = createTardigrade();
                break;
        }

        return enemy;
    }

    private Enemy createTardigrade() {
        Utils.log("spawn", "tardigrade");
        var enemy = new SimpleWalker(scene, Anims.Type.TARDIGRADE);
        enemy.setPosition(64, 64);
        enemy.moveX(MathUtils.random(100, 150) * MathUtils.randomSign());
        return enemy;
    }

    private Enemy createBacteria() {
        return createTardigrade();
    }

    private Enemy createTruck() {
        Utils.log("spawn", "truck");
        return new Truck(scene, Anims.Type.TRUCK);
    }

    private Enemy createBird() {
        return createTruck();
    }

    private  Enemy createAnimal() {
        return createTruck();
    }

    private float nextSpawnTime() {
        return MathUtils.random(minSpawnTime, maxSpawnTime);
    }
    public float spawnTime = 0;
    public void update(float dt) {
        spawnTime -= dt;
        if (spawnTime < 0) {
            if (this.scene.enemies.size < maxSpawn) {
                this.scene.enemies.add(this.spawn());
            }
            spawnTime = nextSpawnTime();
        }
    }
}
