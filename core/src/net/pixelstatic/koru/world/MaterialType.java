package net.pixelstatic.koru.world;

import net.pixelstatic.koru.modules.Renderer;
import net.pixelstatic.koru.modules.World;

import com.badlogic.gdx.math.Rectangle;

public enum MaterialType{
	tile{
		public void draw(Material material, Tile tile, int x, int y, Renderer renderer){
			renderer.layer(material.name(), tile(x), tile(y)).setLayer(tilelayer(material.ordinal()));
			if(this.world.blends(x, y, material)) renderer.layer(material.name() + "edge", tile(x), tile(y)).setLayer(tilelayer(material.ordinal()) - 1);
		}
	},
	block{
		public void draw(Material material, Tile tile, int x, int y, Renderer renderer){
			renderer.layer(material.name(), tile(x), tile(y) - World.tilesize / 2 + 0.5f).yLayer().addShadow("wallshadow");
			renderer.layer("walldropshadow", tile(x), y * World.tilesize + World.tilesize * 0.9f - World.tilesize / 2).setLayer(1f).setScale(0.14f);
		}

		public boolean tile(){
			return false;
		}

		public boolean solid(){
			return true;
		}
	},
	tree{
		public void draw(Material material, Tile tile, int x, int y, Renderer renderer){
			renderer.layer(material.name(), tile(x), tile(y)).alignBottom().yLayer();
		}

		public boolean tile(){
			return false;
		}

		public boolean solid(){
			return true;
		}

		public Rectangle getRect(int x, int y, Rectangle rectangle){
			float width = 7;
			float height = 2;
			return rectangle.set(x * World.tilesize + width / 2, y * World.tilesize + 6 + height / 2, width, height);
		}
	},
	grass{
		public void draw(Material material, Tile tile, int x, int y, Renderer renderer){
			renderer.layer(material.name(), tile(x), tile(y)).alignBottom().yLayer().setColor(tile.tile.foilageColor());
		}

		public boolean tile(){
			return false;
		}
	};
	protected World world;
	static final int tilelayer = -1;

	static int tilelayer(int id){
		return tilelayer - (512 - id * 2);
	}

	public final void drawInternal(Material material, Tile tile, int x, int y, Renderer renderer, World world){
		this.world = world;
		draw(material, tile, x, y, renderer);
	}

	public void draw(Material material, Tile tile, int x, int y, Renderer renderer){
		renderer.layer(material.name(), tile(x), tile(y)).setLayer(tilelayer);
	}

	public boolean solid(){
		return false;
	}

	public Rectangle getRect(int x, int y, Rectangle rectangle){
		return rectangle.set(x * World.tilesize, y * World.tilesize, World.tilesize, World.tilesize);
	}

	public boolean tile(){
		return true;
	}

	int tile(int i){
		return i * World.tilesize + World.tilesize / 2;
	}
}
