package io.anuke.koru.modules;

import io.anuke.koru.Koru;
import io.anuke.koru.components.ConnectionComponent;
import io.anuke.koru.entities.EntityType;
import io.anuke.koru.entities.KoruEntity;
import io.anuke.ucore.modules.Module;

public class ClientData extends Module<Koru>{
	public final KoruEntity player;
	
	public ClientData(){
		player = new KoruEntity(EntityType.player);
		player.mapComponent(ConnectionComponent.class).name = "your player";
		player.mapComponent(ConnectionComponent.class).local = true;
		//player.position().set(460, 90);
	}

	@Override
	public void update(){

	}

}