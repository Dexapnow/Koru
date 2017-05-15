package io.anuke.koru.network;

import io.anuke.koru.components.InputComponent;
import io.anuke.koru.components.RenderComponent;
import io.anuke.koru.components.SyncComponent;
import io.anuke.koru.entities.KoruEntity;
import io.anuke.koru.network.syncing.SyncData;

public enum SyncType{
	position{
		public SyncData write(KoruEntity entity){
			return new SyncData(entity, entity.getX(), entity.getY());
		}

		public void read(SyncData data, KoruEntity entity){
			SyncComponent sync = entity.get(SyncComponent.class);
			
			if(sync.interpolator != null){
				sync.interpolator.push(entity, data.x(), data.y());
			}else{
				entity.position().set(data.x(), data.y());
			}
		}
	},
	physics{
		public SyncData write(KoruEntity entity){
			return new SyncData(entity, entity.getX(), entity.getY());
		}

		public void read(SyncData data, KoruEntity entity){
			SyncComponent sync = entity.get(SyncComponent.class);
			
			if(sync.interpolator != null){
				sync.interpolator.push(entity, data.x(), data.y());
			}else{
				entity.position().set(data.x(), data.y());
			}
		}
	},
	player{
		public SyncData write(KoruEntity entity){
			return new SyncData(entity, 
					entity.getX(), entity.getY(), 
					entity.get(InputComponent.class).input.mouseangle, 
					entity.renderer().direction);
		}

		public void read(SyncData data, KoruEntity entity){
			entity.get(RenderComponent.class).direction = data.get(3);
			float x = data.get(0);
			float y = data.get(1);
			
			//TODO
			entity.get(RenderComponent.class).renderer.walking = 
					entity.position().dist(x, y) > 0.05f;
			
			entity.get(InputComponent.class).input.mouseangle = data.get(2);
			SyncComponent sync = entity.get(SyncComponent.class);
			
			if(sync.interpolator != null){
				sync.interpolator.push(entity, x, y);
			}else{
				entity.position().set(x, y);
			}
		}
	};

	public abstract SyncData write(KoruEntity entity);

	public abstract void read(SyncData buffer, KoruEntity entity);

}