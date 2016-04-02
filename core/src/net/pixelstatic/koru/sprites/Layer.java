package net.pixelstatic.koru.sprites;

import net.pixelstatic.koru.modules.Renderer;
import net.pixelstatic.koru.modules.World;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Layer implements Comparable<Layer>, Poolable{
	public static PooledLayerList list;
	boolean temp = false;
	public static final float shadowlayer = -10, shadowoffset = -10;
	public static final Color shadowcolor = new Color(0,0,0,0.14f);
	public Color color = Color.WHITE.cpy();
	public float layer, x, y, rotation, scalex = 1f, scaley = 1f, heightoffset, width, height;
	public boolean scaled;
	public String region;
	public LayerType type = LayerType.SPRITE;
	public String text;
	public TextureRegion texture;
	public boolean alignbottom = false;

	public enum LayerType{
		SPRITE, TEXT, TEXTURE, SHAPE
	}

	public void Draw(Renderer renderer){
		renderer.batch().setColor(color);
		if(region.equals("")){
		//	Koru.log(x + " " + y + " " + type);
		}
		if(type == LayerType.SPRITE){
			float yalign = 0;
			if(alignbottom){
				TextureRegion tex = renderer.getRegion(region);
				yalign = tex.getRegionHeight()/2;
			}
			if(scaled){
				renderer.drawscl(region, x, y, scalex, scaley);
			}else if(rotation == 0){
				renderer.draw(region, x, y+yalign);
			}else{
				renderer.draw(region, x, y+yalign, rotation);
			}
		}else if(type == LayerType.TEXT){
			renderer.font().setUseIntegerPositions(false);
			renderer.font().setColor(color);
			renderer.font().getData().setScale(scalex/5f);
			GlyphLayout glyphs = renderer.getBounds(text);
			renderer.font().draw(renderer.batch(), text, x - glyphs.width / 2, y + glyphs.height / 2);
		}else if(type == LayerType.TEXTURE){
			renderer.batch().setColor(color);
			renderer.batch().draw(texture, x - texture.getRegionWidth() / 2, y - texture.getRegionHeight() / 2, texture.getRegionHeight() / 2, texture.getRegionWidth() / 2, texture.getRegionWidth(), texture.getRegionHeight(), 1f, 1f, rotation);
		}else if(type == LayerType.SHAPE){
			renderer.batch().draw(renderer.getRegion(region), x, y, width, height);
		}
	
	}
	
	public Layer add(){
		list.add(this);
		return this;
	}
	
	public Layer addShadow(){
		Layer shadow = obtainLayer();
		shadow.set(this).setColor(shadowcolor).setTemp().setScale(1f, -1f).setLayer(1f).add();
		
		return this;
	}
	
	public Layer setHeightOffset(float offset){
		this.heightoffset = offset;
		return this;
	}
	
	public Layer yLayer(){
		return yLayer(this.y);
	}
	
	public Layer yLayer(float y){
		this.alignbottom = true;
		layer = World.worldheight * World.worldheight + World.tilesize -y + heightoffset;
	//	addShadow();
		return this;
	}
	
	public void update(float x, float y){
		setPosition(x,y);
		yLayer();
		add();
	}
	
	public static Layer obtainLayer(){
		return list.getLayer();
	}
	
	public void free(){
		list.pool.free(this);
	}
	

	protected Layer(){
		
	}
	
	public Layer setTemp(){
		this.temp = true;
		return this;
	}
	
	public Layer alignBottom(){
		this.alignbottom = true;
		return this;
	}
	
	public Layer set(Layer layer){
		return this.set(layer.region, layer.x, layer.y)
		.setColor(layer.color).setScale(layer.scalex, layer.scaley)
		.setType(layer.type)
		.setTexture(layer.texture)
		.setRotation(layer.rotation);
	}

	public Layer(String region, float x, float y){
		this.region = region;
		this.x = x;
		this.y = y;
	}

	public Layer setTexture(TextureRegion texture){
		this.texture = texture;
		return this;
	}

	public Layer setType(LayerType type){
		this.type = type;
		return this;
	}

	public Layer setPosition(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}

	public Layer translate(float x, float y){
		return setPosition(this.x + x, this.y + y);
	}

	public Layer setText(String text){
		this.text = text;
		return this;
	}
	

	public Layer setScale(float scale){
		return setScale(scale, scale);
	}
	
	public Layer setScale(float scalex, float scaley){
		this.scalex = scalex;
		this.scaley = scaley;
		scaled = true;
		return this;
	}

	public Layer setLayer(float layer){
		this.layer = layer;
		return this;
	}

	public Layer setColor(Color c){
		color = c;
		return this;
	}

	public Layer setRotation(float rotation){
		this.rotation = rotation;
		return this;
	}
	
	public Layer rotate(float rotation){
		this.rotation += rotation;
		return this;
	}

	public Layer set(String region, float x, float y){
		this.region = region;
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Layer setShape(float width, float height){
		this.width = width;
		this.height = height;
		this.type = LayerType.SHAPE;
		return this;
	}

	public void clear(){
		region = "";
		layer = 0;
		x = 0;
		y = 0;
		rotation = 0;
		color = Color.WHITE.cpy();
		type = LayerType.SPRITE;
		scalex = 1f;
		scaley = 1f;
		scaled = false;
		texture = null;
		temp = false;
		alignbottom = false;
		heightoffset = 0;
		width =0; height=0;
	}

	@Override
	public int compareTo(Layer s){
		if(s.layer == this.layer){
			return 0;
		}else if(s.layer > this.layer){
			return -1;
		}else{
			return 1;
		}
	}

	@Override
	public void reset(){
		clear();
	}
}