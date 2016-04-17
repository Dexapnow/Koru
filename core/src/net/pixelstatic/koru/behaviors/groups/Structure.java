package net.pixelstatic.koru.behaviors.groups;

import net.pixelstatic.koru.behaviors.tasks.PlaceBlockTask;
import net.pixelstatic.koru.behaviors.tasks.Task;
import net.pixelstatic.koru.world.Material;

import com.badlogic.gdx.utils.Array;

public class Structure{
	private Array<Task> tasks = new Array<Task>();
	private int x = 20, y = 20;
	//@formatter:off
	private int[][] blocks = {
		{1,1,1,1,1,1},
		{1,0,0,0,0,1},
		{0,0,0,0,0,1},
		{0,0,0,0,0,1},
		{1,0,0,0,0,1},
		{1,1,1,1,1,1},
	};
	//@formatter:on

	public Structure(int x, int y){
		this.x = x;
		this.y = y;
		addTasks();
	}
	
	public boolean isDone(){
		return tasks.size == 0;
	}
	
	public Task getTask(){
		return tasks.pop();
	}

	private void addTasks(){
		for(int x = 0;x < blocks.length;x ++){
			for(int y = 0;y < blocks[x].length;y ++){
				int worldx = this.x + x, worldy = this.y + y;
				tasks.add(new PlaceBlockTask(worldx, worldy, blocks[x][y] != 0 ? Material.woodblock : Material.woodfloor));
			}
		}
	}

}
