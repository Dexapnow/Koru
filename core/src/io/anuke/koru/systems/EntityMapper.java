package io.anuke.koru.systems;

import static io.anuke.ucore.UCore.scl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Predicate;

import io.anuke.koru.Koru;
import io.anuke.koru.components.ConnectionComponent;
import io.anuke.koru.components.PositionComponent;
import io.anuke.koru.components.RenderComponent;
import io.anuke.koru.entities.KoruEntity;
import io.anuke.koru.modules.World;
import io.anuke.ucore.util.GridMap;

public class EntityMapper extends KoruSystem implements EntityListener{
	public static final float gridsize = World.tilesize * World.chunksize * 2;
	public ObjectMap<Long, KoruEntity> entities = new ObjectMap<Long, KoruEntity>();
	private GridMap<ObjectSet<KoruEntity>> map = new GridMap<ObjectSet<KoruEntity>>();
	private Array<KoruEntity> tmp = new Array<KoruEntity>();
	private boolean debug = false;

	public static Predicate<KoruEntity> connectionPredicate = (entity) -> {
		return entity.hasComponent(ConnectionComponent.class);
	};

	public static Predicate<KoruEntity> allPredicate = (entity) -> {
		return true;
	};

	public EntityMapper() {
		super(Family.all(PositionComponent.class).get());
	}

	/**
	 * Returns all the entities near a specific location (within range). May
	 * return entities outside the range.
	 **/
	public Array<KoruEntity> getNearbyEntities(float cx, float cy, float rangex, float rangey,
			Predicate<KoruEntity> pred){
		if(rangex < 1 || rangey < 1)
			throw new IllegalArgumentException("rangex and rangey cannot be negative, are you insane?!");

		tmp.clear();

		int maxx = scl(cx + rangex, gridsize), maxy = scl(cy + rangey, gridsize), minx = scl(cx - rangex, gridsize),
				miny = scl(cy - rangey, gridsize);

		if(debug){
			Koru.log("scan position: " + cx + ", " + cy);
			Koru.log("placed quadrant: " + scl(cx, EntityMapper.gridsize) + ", " + scl(cy, EntityMapper.gridsize));
			Koru.log("bounds: " + minx + ", " + miny + "  " + maxx + ", " + maxy);
		}

		for(int x = minx; x < maxx + 1; x++){
			for(int y = miny; y < maxy + 1; y++){
				ObjectSet<KoruEntity> set = map.get(x, y);
				if(set != null){
					for(KoruEntity e : set)
						if(pred.evaluate(e) && Math.abs(e.getX() - cx) < rangex  && Math.abs(e.getY() - cy) < rangey)
							tmp.add(e);
				}
			}
		}

		return tmp;
	}

	/** Gets nearby entities with a connection */
	public Array<KoruEntity> getNearbyConnections(float cx, float cy, float rangex, float rangey){
		return this.getNearbyEntities(cx, cy, rangex, rangey, connectionPredicate);
	}

	/** Just gets all nearby entities. */
	public Array<KoruEntity> getNearbyEntities(float cx, float cy, float rangex, float rangey){
		return this.getNearbyEntities(cx, cy, rangex, rangey, allPredicate);
	}

	public ObjectSet<KoruEntity> getEntitiesIn(float cx, float cy){
		int x = (int) (cx / gridsize), y = (int) (cy / gridsize);
		return map.get(x, y);
	}

	@Override
	void processEntity(KoruEntity entity, float delta){
		int x = (int) (entity.getX() / gridsize), y = (int) (entity.getY() / gridsize);
		ObjectSet<KoruEntity> set = map.get(x, y);

		if(set == null){
			map.put(x, y, (set = new ObjectSet<KoruEntity>()));
		}

		set.add(entity);
	}

	@Override
	public void update(float deltaTime){
		for(ObjectSet<KoruEntity> set : map.values()){
			set.clear();
		}
		super.update(deltaTime);
	}

	@Override
	public void entityAdded(Entity entity){
		entities.put(((KoruEntity) entity).getID(), (KoruEntity) entity);
	}

	@Override
	public void entityRemoved(Entity entity){
		RenderComponent render = entity.getComponent(RenderComponent.class);
		// don't remove local player
		if(render != null && !(entity.getComponent(ConnectionComponent.class) != null
				&& entity.getComponent(ConnectionComponent.class).local == true))
			render.group.free();

		entities.remove(((KoruEntity) entity).getID());
	}
}